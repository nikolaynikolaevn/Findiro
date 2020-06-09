package com.flamevision.findiro.LoginAndSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flamevision.findiro.LoginActivity;
import com.flamevision.findiro.MainActivity;
import com.flamevision.findiro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CustomSignupActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPass;
    private EditText etName;
    private Button btnSignup;
    private Button btnChoosePicture;
    private ImageView ivPicture;

    private  static final int REQUESTCODE_GET_PICTURE = 123;

    private Uri pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_signup);

        etEmail = findViewById(R.id.SignUp_EmailValue);
        etPass = findViewById(R.id.SignUp_PassValue);
        etName = findViewById(R.id.SignUp_NameValue);
        btnSignup = findViewById(R.id.btnSignUp);
        btnChoosePicture = findViewById(R.id.customSignupPictureButton);
        ivPicture = findViewById(R.id.customSignupPicture);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpViaEmail();
            }
        });
        btnChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, REQUESTCODE_GET_PICTURE);
            }
        });

        //show default picture
        Drawable defaultPic = getResources().getDrawable(R.drawable.ic_user);
        ivPicture.setImageDrawable(defaultPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_GET_PICTURE){
            pictureUri = data.getData();
            ivPicture.setImageURI(pictureUri);
        }
    }

    private void signUpViaEmail(){
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        final String name = etName.getText().toString().trim();

        Log.d("Sign up", "Entered email: " + email + ", pass: " + pass);

        boolean error = false;
        if (email.equalsIgnoreCase("")) {
            etEmail.setError("You must enter your email.");
            error = true;
        }
        if (pass.equalsIgnoreCase("")) {
            etPass.setError("You must enter your password.");
            error = true;
        }
        if (name.equalsIgnoreCase("")) {
            etName.setError("You must enter your name.");
            error = true;
        }
        if(error){
            Log.d("Sign up", "EditText error occurred");
            Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
        }
        else  {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("Sign up", "Sign up success, now updating database");
                        //sign up success, now create user data in the database
                        updateDatabase(name);
                        Log.d("Sign up", "Database updated with new user");
                    } else {
                        //sign up failed
                        Log.d("Sign up", "Sign up failed due to exception: " + task.getException());
                        if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            //password is too weak
                            Log.d("Sign up", "Sign up failed because of weak password");
                            etPass.setError("Your password is too weak");
                        }
                        else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            //email is malformed
                            Log.d("Sign up", "Sign up failed because of malformed email");
                            etEmail.setError("Your email is malformed");
                        }
                        else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            //email exists
                            Log.d("Sign up", "Sign up failed because of used email");
                            etEmail.setError("This email is already used");
                        }
                        Toast.makeText(CustomSignupActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void updateDatabase(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference curUserRef = usersRef.child(user.getUid());
        DatabaseReference curUserNameRef = curUserRef.child("name");
        curUserNameRef.setValue(name);

        //upload picture
        if(pictureUri != null){
            final StorageReference picRef = FirebaseStorage.getInstance().getReference("Profile-Pictures/" + user.getUid());
            picRef.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String pictureUrl = picRef.getPath();
                    curUserRef.child("picture").setValue(pictureUrl);
                    Log.d("Sign up", "Picture has been uploaded to storage");
                    setResult(RESULT_OK);
                    goToMain();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Sign up", "Picture failed to upload to storage");
                    setResult(RESULT_OK);
                    goToMain();
                }
            });
        }
        else {
            setResult(RESULT_OK);
            goToMain();
        }
    }

    private void goToMain(){
        Intent intent = new Intent(CustomSignupActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
