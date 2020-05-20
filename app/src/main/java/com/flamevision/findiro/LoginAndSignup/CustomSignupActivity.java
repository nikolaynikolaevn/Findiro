package com.flamevision.findiro.LoginAndSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flamevision.findiro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomSignupActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPass;
    private EditText etName;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_signup);

        etEmail = findViewById(R.id.customSignupEmailText);
        etPass = findViewById(R.id.customSignupPasswordText);
        etName = findViewById(R.id.customSignupNameText);
        btnSignup = findViewById(R.id.customSignupButton);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpViaEmail();
            }
        });
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
                        setResult(RESULT_OK);
                        finish();
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
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference curUserRef = usersRef.child(user.getUid());
        DatabaseReference curUserNameRef = curUserRef.child("name");
        curUserNameRef.setValue(name);
    }
}
