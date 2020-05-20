package com.flamevision.findiro.UserAndGroup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flamevision.findiro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText etName;
    private FirebaseAuth auth;
    private DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        auth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.createGroupNameText);

        Button btnCreateGroup = findViewById(R.id.createGroupButton);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                if(name == ""){
                    etName.setError("No name has been given");
                }
                else {
                    createGroup(name);
                }
            }
        });
    }

    private void createGroup(String name){
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            Toast.makeText(this, "Failure: You need to be logged in", Toast.LENGTH_LONG).show();
        }
        else{
            //create group in db
            DatabaseReference groupRef = groupsRef.push();
            groupRef.child("name").setValue(name);
            groupRef.child("groupCreator").setValue(user.getUid());
            DatabaseReference firstMemberRef = groupRef.child("members").push();
            firstMemberRef.setValue(user.getUid());

            //add group to user
            DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReference("Users/" + user.getUid() + "/groups");
            DatabaseReference userNewGroupRef = userGroupsRef.push();
            userNewGroupRef.setValue(groupRef.getKey());

            setResult(RESULT_OK);
            finish();
        }
    }
}
