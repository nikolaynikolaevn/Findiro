package com.flamevision.findiro.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flamevision.findiro.LoginAndSignup.TestLoginAndSignupActivity;
import com.flamevision.findiro.MainActivity;
import com.flamevision.findiro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class EditProfile_activity extends AppCompatActivity {

    public FirebaseUser user;
    public FirebaseDatabase database;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenser;
    private EditText username;
    private EditText email;
    private Button uploadPic;
    private ImageView ivPicture;
    private Button saveData;
    private Uri pictureUri;
    //private  static final int REQUESTCODE_GET_PICTURE = 123;

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListenser);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_activity);
        username = findViewById(R.id.editText);
        email = findViewById(R.id.editText2);
        uploadPic = findViewById(R.id.btnUpload);
        saveData = findViewById(R.id.btnSave);

        ShowName();
        getUserEmail();

        mAuth=FirebaseAuth.getInstance();

        mAuthListenser=new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    username.setText(user.getDisplayName());
                    email.setText(user.getEmail());
                }
            }
        };


        //save profile
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //upload pic
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_GET_PICTURE){
//            pictureUri = data.getData();
//            ivPicture.setImageURI(pictureUri);
//        }
//    }


    private void getUserEmail()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String Email = user.getEmail();
        email.setText(Email);
        Log.d("got users email", Email.toString());
    }

    private void ShowName() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                username.setText(name);
                Log.d("got users NAME", name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateName(View view)
    {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null)
            return;
        String newName=username.getText().toString();
        //UserProfileChangeRequest UPC=new UserProfileChangeRequest().Builder()
                //.setDisplayName(newName)
                //.build();
    }
}
