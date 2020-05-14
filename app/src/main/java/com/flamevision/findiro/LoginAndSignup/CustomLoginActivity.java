package com.flamevision.findiro.LoginAndSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flamevision.findiro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CustomLoginActivity extends AppCompatActivity {

    private static final String TAG = CustomLoginActivity.class.getName();

    private EditText etEmail;
    private EditText etPass;
    private EditText etName;
    private TextView tvEmail;
    private TextView tvPass;
    private TextView tvName;
    private Button btnLoginEmail;

    private  String NoPass = "NoPassSetYet1234";

    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_login);

        etEmail = findViewById(R.id.customLoginEmailText);
        etPass = findViewById(R.id.customLoginPasswordText);
        etName = findViewById(R.id.customLoginNameText);
        tvEmail = findViewById(R.id.customLoginEmailTitle);
        tvPass = findViewById(R.id.customLoginPasswordTitle);
        tvName = findViewById(R.id.customLoginNameTitle);
        btnLoginEmail = findViewById(R.id.customLoginNextButton);

        etPass.setVisibility(View.GONE);
        tvPass.setVisibility(View.GONE);
        etName.setVisibility(View.GONE);
        tvName.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        LoginButton loginButton = findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                // App code
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError", exception);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        });

        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViaEmail();
            }
        });
        // [END initialize_fblogin]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(CustomLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    // [END auth_with_facebook]

    private void signUpViaEmail(){
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        final String name = etName.getText().toString().trim();

        Log.d("Sign up", "Entered email: " + email + ", pass: " + pass + ", name: " + name);

        boolean error = false;
        if(pass == NoPass){
            etPass.setError("This password is not allowed");
            error = true;
        }
        if (email.equalsIgnoreCase("") && etEmail.getVisibility() != View.GONE) {
            etEmail.setError("You must enter your email.");
            error = true;
        }
        if (pass.equalsIgnoreCase("") && etPass.getVisibility() != View.GONE) {
            etPass.setError("You must enter your password.");
            error = true;
        }
        if (name.equalsIgnoreCase("") && etName.getVisibility() != View.GONE) {
            etName.setError("You must enter your name.");
            error = true;
        }
        if(error){Log.d("Sign up", "EditText error occurred");}
        else  {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //sign up success, now set user name
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user != null){
                            UserProfileChangeRequest.Builder requestBuilder = new UserProfileChangeRequest.Builder();
                            UserProfileChangeRequest request = requestBuilder.setDisplayName(name).build();
                            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("Sign up", "Sign up success");
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        }
                    } else {
                        //sign up failed
                        Log.d("Sign up", "Sign up failed due to exception: " + task.getException());
                        if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            //password is too weak
                            Log.d("Sign up", "Sign up failed because of weak password");
                            etPass.setError("Your password is too weak");
                        }
                    }
                }
            });
        }
    }

    private void loginViaEmail(){
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if(etPass.getVisibility() == View.GONE){pass = NoPass;}
        Log.d("Login", "Entered email: " + email + ", pass: " + pass);

        boolean error = false;
        if (email.equalsIgnoreCase("") && etEmail.getVisibility() != View.GONE) {
            etEmail.setError("You must enter your email.");
            error = true;
        }
        if (pass.equalsIgnoreCase("") && etPass.getVisibility() != View.GONE) {
            etPass.setError("You must enter your password.");
            error = true;
        }

        if(error){Log.d("Login", "EditText error occurred");}
        else  {
            Log.d("Login", "Trying to login");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //login success
                        Log.d("Login", "Login success");
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        //login failed
                        Log.d("Login", "Login failed due to exception: " + task.getException());
                        if(task.getException() instanceof FirebaseAuthInvalidUserException){
                            //Email does not exists, show sign up screen
                            Log.d("Login", "Email does not exists, showing sign up screen");
                            etPass.setVisibility(View.VISIBLE);
                            etEmail.setVisibility(View.GONE);
                            tvPass.setVisibility(View.VISIBLE);
                            tvEmail.setVisibility(View.GONE);
                            tvName.setVisibility(View.VISIBLE);
                            etName.setVisibility(View.VISIBLE);
                            btnLoginEmail.setText("sign up");
                            btnLoginEmail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    signUpViaEmail();
                                }
                            });
                        }
                        else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            //email does exists, but wrong password
                            if(etPass.getVisibility() != View.GONE){
                                //inform user about wrong password
                                Log.d("Login", "Wrong password, informing user");
                                etPass.setError("wrong password");
                            }
                            else {
                                //show login screen
                                etPass.setVisibility(View.VISIBLE);
                                etEmail.setVisibility(View.GONE);
                                tvPass.setVisibility(View.VISIBLE);
                                tvEmail.setVisibility(View.GONE);
                                btnLoginEmail.setText("Log in");
                                Log.d("Login", "Correct email, showing login screen");
                            }
                        }
                    }
                }
            });
        }
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        //updateUI(null);
    }

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]
}
