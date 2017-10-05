package com.eclev.lawrence.gistmee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText name, email, password;
    Button save;
    FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.etName);
        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPass);
        save = (Button) findViewById(R.id.btnSave);
        mProgressDialog = new ProgressDialog(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = mFirebaseDatabase.getInstance().getReference().child("Users");


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser(){
        final String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userName)&&
                !TextUtils.isEmpty(userPass)){
            mProgressDialog.setMessage("Signing "+ userName +" Up" );
            mProgressDialog.show();
            mFirebaseAuth.createUserWithEmailAndPassword(userEmail,userPass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String userId = mFirebaseAuth.getCurrentUser().getUid();
                        DatabaseReference currentUser = mDatabaseReference.child(userId);
                        currentUser.child("name").setValue(userName);
                        currentUser.child("image").setValue("default");

                        mProgressDialog.dismiss();
                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    }
                }
            });

        }
    }
}
