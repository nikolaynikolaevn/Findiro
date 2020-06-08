package com.flamevision.findiro;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.flamevision.findiro.LoginAndSignup.CustomLoginActivity;
import com.flamevision.findiro.LoginAndSignup.CustomSignupActivity;
import com.flamevision.findiro.Profile.Login2_activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

    private static final String TAG = CustomLoginActivity.class.getName();

    private EditText etEmail;
    private EditText etPass;
    private Button btnLoginEmail;
    private Button signup;
    private TextView staus;

    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.EmailValue);
        etPass = view.findViewById(R.id.PassValue);
        btnLoginEmail = view.findViewById(R.id.btnSignValue);
        staus = view.findViewById(R.id.Overview);
        mAuth = FirebaseAuth.getInstance();

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    loginViaEmail();
                }
            }
        });

        signup = view.findViewById(R.id.btnSignUpValue);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomSignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    // [END on_start_check_user]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
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
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    // [END auth_with_facebook]



    private void loginViaEmail(){
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        Log.d("Login", "Entered email: " + email + ", pass: " + pass);

        boolean error = false;
        if (email.equalsIgnoreCase("")) {
            etEmail.setError("You must enter your email.");
            error = true;
        }
        if (pass.equalsIgnoreCase("")) {
            etPass.setError("You must enter your password.");
            error = true;
        }

        if(error){
            Log.d("Login", "EditText error occurred");
            Toast.makeText(getActivity(), "Log in failed", Toast.LENGTH_SHORT).show();
        }
        else  {
            Log.d("Login", "Trying to login");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //login success
                        Log.d("Login", "Login success");
                        getActivity().setResult(getActivity().RESULT_OK);
                        getActivity().finish();
                    }
                    else {
                        //login failed
                        Log.d("Login", "Login failed due to exception: " + task.getException());
                        if(task.getException() instanceof FirebaseAuthInvalidUserException){
                            //Email does not exists, show sign up screen
                            etEmail.setError("No account exists with this email.");
                        }
                        else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            //email does exists, but wrong password
                            etPass.setError("Wrong password.");
                        }
                        Toast.makeText(getActivity(), "Log in failed", Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]
}