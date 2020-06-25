package com.flamevision.findiro.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flamevision.findiro.R;
import com.flamevision.findiro.UserAndGroup.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Fragment_EditProfile extends Fragment {

    private FirebaseUser user;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int REQUESTCODE_GET_PICTURE = 123;
    private Uri pictureUri;
    private EditText username;
    private EditText email;
    private Button uploadPic;
    private ImageView ivPicture;
    private Button changeName;

    public Fragment_EditProfile() {
        // Required empty public constructor

    }
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__edit_profile, container, false);

        username = view.findViewById(R.id.etName);
        email = view.findViewById(R.id.etEmail);
        uploadPic = view.findViewById(R.id.EditProfile);
        changeName = view.findViewById(R.id.EditName);
        mAuth = FirebaseAuth.getInstance();

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String users = username.getText().toString();
                ChangeName(users);
                Toast.makeText(getActivity(), "name updated..", Toast.LENGTH_SHORT).show();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    getUserName();
                    email.setText(user.getEmail());
                }
            }
        };

        //upload pic
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, REQUESTCODE_GET_PICTURE);
            }
        });

        return view;
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
                //Log.d("got users NAME", name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
    private void setUserEmailAddress(View view) {
        String newEmail = email.getText().toString();
        if (TextUtils.isEmpty(newEmail))
            return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateDatabase(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference curUserRef = usersRef.child(user.getUid());
        DatabaseReference curUserNameRef = curUserRef.child("name");
        curUserNameRef.setValue(name);

        //upload picture
        if (pictureUri != null) {
            final StorageReference picRef = FirebaseStorage.getInstance().getReference("Profile-Pictures/" + user.getUid());
            picRef.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String pictureUrl = picRef.getPath();
                    curUserRef.child("picture").setValue(pictureUrl);
                    //Log.d("Sign up", "Picture has been uploaded to storage");
                    //setResult(RESULT_OK);
                    //finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.d("Sign up", "Picture failed to upload to storage");
                    //setResult(RESULT_OK);
                   // finish();
                }
            });
        } else {
            //setResult(RESULT_OK);
            //finish();
        }
    }
    public void ChangeName(String users)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference curUserRef = usersRef.child(user.getUid());
        DatabaseReference curUserNameRef = curUserRef.child("name");
        curUserNameRef.setValue(users);
    }
}