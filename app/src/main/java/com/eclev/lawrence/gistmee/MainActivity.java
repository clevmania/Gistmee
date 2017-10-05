package com.eclev.lawrence.gistmee;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String mUser,title,desc,content,author,date,time,pix;

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference().child("Posts");

        //Firebase Auth initialization
        mFirebaseAuth =  FirebaseAuth.getInstance();


        initCollapsingToolbar();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_blog_list);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mReference.keepSynced(true);


        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.iv_blog_thumbnail));
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent blogIntent = new Intent(MainActivity.this, NewBlogActivity.class);
                startActivity(blogIntent);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user =  firebaseAuth.getCurrentUser();
                if(user != null){
                    // user is signed in
                }else {
                    // user is signed out
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        super.onStart();

        FirebaseRecyclerAdapter<BlogPostMessages, BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<BlogPostMessages, BlogViewHolder>(
                        BlogPostMessages.class,
                        R.layout.blog_cardview,
                        BlogViewHolder.class,
                        mReference
                ) {

                    @Override
                    protected void populateViewHolder(BlogViewHolder viewHolder, BlogPostMessages model, int position) {
                        final String postedChildKey = getRef(position).getKey();
                        viewHolder.setBlogTitle(model.getTitle());
                        viewHolder.setBlogDesc(model.getDescription());
                        viewHolder.setBlogImage(getApplicationContext() , model.getPhotoUrl());
                        viewHolder.setBlogContent(model.getContent());
                        viewHolder.setBlogTime(model.getTimePosted());
                        viewHolder.setBlogDate(model.getDate());

                        // can also load others as extras to the detail intent
                        title = model.getTitle();
                        desc = model.getDescription();
                        content = model.getContent();
                        author = mUser;
                        time = model.getTimePosted();
                        date = model.getDate();
                        pix = model.getPhotoUrl();

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Toast.makeText(MainActivity.this, "i am supposed to do something", Toast.LENGTH_SHORT).show();
                                Intent detailedIntent = new Intent(MainActivity.this, BlogDetailsActivity.class);
                                detailedIntent.putExtra("D_key", postedChildKey);
                                startActivity(detailedIntent);
                            }
                        });

//                        viewHolder.blogCards.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Toast.makeText(v.getContext(), "Now im showing", Toast.LENGTH_SHORT).show();
//                            }
//                        });

                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initCollapsingToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsingToolBar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);

        //Now hide and show the title when toolbar is expanded and collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange == -1)
                    scrollRange = appBarLayout.getTotalScrollRange();
                if(scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle("Gist mee!!!");
                    isShow = true;
                }else if(isShow){
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }

            }
        });
    }

    //Now the recyclerView itemDecoration
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration{
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if(includeEdge){
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if(position < spanCount) outRect.top = spacing;
                outRect.bottom = spacing;
            }else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if(position >= spacing) outRect.top = spacing;
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();

        DisplayMetrics metrics = r.getDisplayMetrics();
        int unit = TypedValue.COMPLEX_UNIT_DIP;
        float show = TypedValue.applyDimension(unit, dp, metrics);
        int dvalue = Math.round(show);
        return dvalue;
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvdesc, tvcontent, tvdate, tvtime;
        ImageView thumbnail;
        View mView;
//        CardView blogCards;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
//            blogCards = (CardView) mView.findViewById(R.id.cv_blog);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //start the intent here
//                    populateBlogDetailsViaIntent();
//
//                    Intent intent = new Intent(v.getContext(),BlogDetailsActivity.class);
//                    intent.putExtra("my_key", mBlogPostMessages.get(getAdapterPosition()));
//                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                    v.getContext().startActivity(intent);
//                }
//            });

        }

        public void setBlogTitle(String title){
            tvTitle = (TextView) mView.findViewById(R.id.tv_blog_title);
            tvTitle.setText(title);
        }

        public void setBlogDesc(String desc){
            tvdesc = (TextView) mView.findViewById(R.id.tv_blog_description);
            tvdesc.setText(desc);
        }

        public void setBlogImage(Context ctx,String blogImage){
            thumbnail = (ImageView) mView.findViewById(R.id.iv_blog_thumbnail);
            Glide.with(ctx).load(blogImage).into(thumbnail);
        }

        public void setBlogTime(String time){
            tvtime = (TextView) mView.findViewById(R.id.tv_blog_card_time);
            tvtime.setText(time);
        }

        public void setBlogDate(String date){
            tvdate = (TextView) mView.findViewById(R.id.tv_blog_card_post_date);
            tvdate.setText(date);
        }

        public void setBlogContent(String content){
            tvcontent = (TextView) mView.findViewById(R.id.tv_blog_card_post_content);
            tvcontent.setText(content);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out){
            mFirebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
