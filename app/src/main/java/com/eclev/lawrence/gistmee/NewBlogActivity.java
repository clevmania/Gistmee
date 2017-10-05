package com.eclev.lawrence.gistmee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewBlogActivity extends AppCompatActivity {

    private String mUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private Uri blogImageSelect = null;
    private ProgressDialog mProgressDialog;

    EditText title, desc, content;
    BlogPostMessages bpm;
    private static final int photo_selected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog_post);

        //firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        //firebase storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("BlogPhotos");


        title = (EditText) findViewById(R.id.et_post_title);
        desc = (EditText) findViewById(R.id.et_post_description);
        content = (EditText) findViewById(R.id.et_post_content);

        mProgressDialog = new ProgressDialog(this);

    }

    public void SendTOFirebase() {
        mProgressDialog.setMessage("Sending Post...");
        final String postTitle = title.getText().toString();
        final String postDesc = desc.getText().toString();
        final String postContent = content.getText().toString();

        if(!TextUtils.isEmpty(postTitle)&& !TextUtils.isEmpty(postDesc)
                && !TextUtils.isEmpty(postContent) && blogImageSelect != null){
            mProgressDialog.show();

            Calendar calender = Calendar.getInstance();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE, MMM d, ''yy");
            SimpleDateFormat stf = new SimpleDateFormat("h:mm a");
            final String postDate = simpledateformat.format(calender.getTime());
            final String postedTime = stf.format(calender.getTime());

            StorageReference blogImgRef = mStorageReference
                    .child(blogImageSelect.getLastPathSegment());
            blogImgRef.putFile(blogImageSelect).addOnSuccessListener
                    (this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri imgDownloadUri = taskSnapshot.getDownloadUrl();
                            String photoUrl = imgDownloadUri.toString();

                            bpm = new BlogPostMessages(postTitle,postDesc,postContent,photoUrl
                                    , postDate ,postedTime);
//                            bpm = new BlogPostMessages(postTitle,postDesc,photoUrl);
                            mDatabaseReference.child("Posts").push().setValue(bpm);
                            clear();

                            mProgressDialog.dismiss();
                        }
                    });
        }else{
            Toast.makeText(this, "All Fields are required", Toast.LENGTH_SHORT).show();
        }

    }

    public void clear(){
        title.setText("");
        desc.setText("");
        content.setText("");
    }


    public void pickYourPostImage() {
        Intent pickPixIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickPixIntent.setType("image/*");
        pickPixIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(pickPixIntent,"Complete Action"),photo_selected);
    }

    // Add filters to check editText doesnt go beyond threshold
    // postTitle should be 50 words max, desc should be 120
    // Photo resizing ability is needed


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == photo_selected && resultCode == RESULT_OK){
            blogImageSelect = data.getData();
            Toast.makeText(this, blogImageSelect.toString(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_image:
                pickYourPostImage();
                break;
            case R.id.action_send_post:
                SendTOFirebase();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
