package com.eclev.lawrence.gistmee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BlogDetailsActivity extends AppCompatActivity {
    TextView title,desc,content,author,time,date;
    ImageView blogDetailedImage;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        title = (TextView) findViewById(R.id.blog_detail_title);
        desc = (TextView) findViewById(R.id.blog_detail_description);
        content = (TextView) findViewById(R.id.blog_detail_content);
        author = (TextView) findViewById(R.id.tv_blog_post_author);
        time = (TextView) findViewById(R.id.tv_blog_post_time);
        date= (TextView) findViewById(R.id.tv_blog_post_date);
        blogDetailedImage = (ImageView) findViewById(R.id.iv_blog_detail_image);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        String childPostKey = getIntent().getExtras().getString("D_key");
        Toast.makeText(this, childPostKey, Toast.LENGTH_SHORT).show();
        mDatabaseReference.child(childPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String blogPostTitle = dataSnapshot.child("title").getValue().toString();
                String blogPostDesc = dataSnapshot.child("description").getValue().toString();
                String blogPostContent = dataSnapshot.child("content").getValue().toString();
                String blogPostTimePosted = dataSnapshot.child("timePosted").getValue().toString();
                String blogPostDate = dataSnapshot.child("date").getValue().toString();
                String blogPostImageUrl = dataSnapshot.child("photoUrl").getValue().toString();

                title.setText(blogPostTitle);
                desc.setText(blogPostDesc);
                content.setText(blogPostContent);
                time.setText(blogPostTimePosted);
                date.setText(blogPostDate);
                author.setText("Admin");
                Glide.with(BlogDetailsActivity.this).load(blogPostImageUrl).centerCrop().into(blogDetailedImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
