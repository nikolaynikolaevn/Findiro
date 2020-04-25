package com.flamevision.findiro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flamevision.findiro.UserAndGroup.TestUserAndGroupActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnTestUserAndGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTestUserAndGroup = findViewById(R.id.MainTestUserAndGroupButton);
        btnTestUserAndGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestUserAndGroupActivity.class);
                startActivity(intent);
            }
        });
    }
}
