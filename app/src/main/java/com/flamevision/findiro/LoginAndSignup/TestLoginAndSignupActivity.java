package com.flamevision.findiro.LoginAndSignup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flamevision.findiro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestLoginAndSignupActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignup;
    private Button btnLogout;
    private TextView tvStatus;

    private FirebaseUser user;

    private int REQUESTCODE_SIGNUP = 123;
    private int REQUESTCODE_LOGIN = 321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login_and_signup);

        btnLogin = findViewById(R.id.testLoginAndSignupLoginButton);
        btnSignup = findViewById(R.id.testLoginAndSignupSignupButton);
        btnLogout = findViewById(R.id.testLoginAndSignupLogoutButton);
        tvStatus = findViewById(R.id.testLoginAndSignupStatusTextView);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            //USER IS LOGGED IN
            onLogin();
        }
        else {
            //USER IS NOT LOGGED IN
            onLogout();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if(auth.getCurrentUser() == null){
                    //NOT LOGGED IN YET
                    Intent loginIntent = new Intent(TestLoginAndSignupActivity.this, CustomLoginActivity.class);
                    startActivityForResult(loginIntent, REQUESTCODE_LOGIN);
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if(auth.getCurrentUser() == null){
                    //NOT LOGGED IN YET
                    Intent loginIntent = new Intent(TestLoginAndSignupActivity.this, CustomSignupActivity.class);
                    startActivityForResult(loginIntent, REQUESTCODE_SIGNUP);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTCODE_LOGIN){
            if(resultCode == RESULT_OK){
                //LOGGED IN SUCCES
                onLogin();
            }
            else {
                //LOGGED IN FAILED
                onLogout();
                tvStatus.setText("Login failed or was cancelled");
            }
        }
        if(requestCode == REQUESTCODE_SIGNUP){
            if(resultCode == RESULT_OK){
                //LOGGED IN SUCCES
                onLogin();
            }
            else {
                //LOGGED IN FAILED
                onLogout();
                tvStatus.setText("Singup failed or was cancelled");
            }
        }
    }

    private void onLogin(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/name");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onLoginStatusUpdate(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void onLoginStatusUpdate(String userName){
        String status = "You are logged in";
        status += "\nuser DisplayName: " + user.getDisplayName();
        status += "\nuser DatabaseName: " + userName;
        status += "\nuser email: " + user.getEmail();
        tvStatus.setText(status);
    }

    private  void onLogout(){
        user = null;
        FirebaseAuth.getInstance().signOut();
        tvStatus.setText("You are NOT logged in");
    }


}
