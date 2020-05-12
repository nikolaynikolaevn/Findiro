package com.flamevision.findiro.LoginAndSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flamevision.findiro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CustomLoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_login);

        etEmail = findViewById(R.id.customLoginEmailText);
        etPass = findViewById(R.id.customLoginPasswordText);
        btnLogin = findViewById(R.id.customLoginButton);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //login succes
                    setResult(RESULT_OK);
                }
                else {
                    //login failed
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
    }
}
