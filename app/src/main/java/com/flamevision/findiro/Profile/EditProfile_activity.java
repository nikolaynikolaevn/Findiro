package com.flamevision.findiro.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flamevision.findiro.LoginAndSignup.TestLoginAndSignupActivity;
import com.flamevision.findiro.MainActivity;
import com.flamevision.findiro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class EditProfile_activity extends AppCompatActivity {
    //very useful tutorial about Glide in github
    //https://github.com/bumptech/glide
    private FirebaseUser user;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private EditText username;
    private EditText email;

    private Button uploadPic;
    private ImageView ivPicture;
    private Button saveData;
    private Button changeEmail;

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_activity);

        username = findViewById(R.id.editText);
        email = findViewById(R.id.editText2);
        uploadPic = findViewById(R.id.btnUpload);
        saveData = findViewById(R.id.btnSave);
        changeEmail=findViewById(R.id.btnChanged);
        mAuth=FirebaseAuth.getInstance();

        mAuthListener=new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    username.setText(user.getDisplayName());
                    //getUserName();
                    email.setText(user.getEmail());
                }
            }
        };

        //change name
//        saveData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateUserProfile(v);
//            }
//        });
        //upload pic
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//       changeEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setUserEmailAddress(v);
//            }
//        });
    }
    private void getUserName()
    {
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
    //update user name
    public void updateUserProfile(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        final String newName = username.getText().toString();
        if (TextUtils.isEmpty(newName))
            return;
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfile_activity.this, "User updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SetValue(String value,String path)
    {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference(path);
        myRef.setValue(value);
    }

    public void setUserEmailAddress(View view) {
        String newEmail = email.getText().toString();
        if (TextUtils.isEmpty(newEmail))
            return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(EditProfile_activity.this, "email updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
