package com.flamevision.findiro.UserAndGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.flamevision.findiro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestUserAndGroupActivity extends AppCompatActivity implements SelectUserFragment.UserReceiver, SelectGroupFragment.GroupReceiver{

    private List<Group> groups = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_user_and_group);

        Button btnShowUsers = findViewById(R.id.testUserAndGroupShowUsersButton);
        btnShowUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllUsers();
            }
        });

        Button btnCreateGroup = findViewById(R.id.testUserAndGroupCreateGroupButton);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestUserAndGroupActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        Button btnShowGroups = findViewById(R.id.testUserAndGroupShowGroupsButton);
        btnShowGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllGroups();
            }
        });

        /*
        //Add or replace fragment in container
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        SelectGroupFragment fragment = new SelectGroupFragment(this, groups);
        fragTrans.add(R.id.TestUserAndGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();
        */
    }

    private ValueEventListener groupValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.e("Show all groups" ,"Total groups: "+snapshot.getChildrenCount());
            for (DataSnapshot groupSnapShot: snapshot.getChildren()) {
                Group group = new GroupReference(groupSnapShot.getKey(), null);
                    /*
                    String name = groupSnapShot.child("name").getValue().toString();
                    String groupCreatorUid = groupSnapShot.child("groupCreator").getValue().toString();
                    Log.e("Show all groups" ,"Group name: "+ name);
                    Log.e("Show all groups" ,"Group creator uid:" + groupCreatorUid);
                    User groupCreator = new User(groupCreatorUid);
                    List<User> members = new ArrayList<>();
                    DataSnapshot membersSnapShot = groupSnapShot.child("members");
                    for(DataSnapshot memberSnapShot : membersSnapShot.getChildren()){
                        String memberUid = memberSnapShot.getValue().toString();
                        Log.e("Show all groups" ,"Member uid:" + memberUid);
                        User member = new User(memberUid);
                        members.add(member);
                    }
                    Group group = new Group(members, groupCreator, name);
                     */
                groups.add(group);
            }
            showGroupsFragment();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("Firebase error", databaseError.getMessage());
        }
    };
    private void showAllGroups(){
        groups = new ArrayList<>();
        groupsRef.addValueEventListener(groupValueListener);
    }

    private ValueEventListener userValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.e("Show all users " ,"Total users: "+snapshot.getChildrenCount());
            for (DataSnapshot userSnapShot: snapshot.getChildren()) {
                if(true){
                    String userUid = userSnapShot.getKey().toString();
                    Log.e("Show all users " ,"UserUid: "+ userUid);
                    User user = new UserReference(userUid, null);
                    users.add(user);
                }
                else { //OLD WAY
                    String name = userSnapShot.child("name").getValue().toString();
                    String userId = userSnapShot.getKey().toString();
                    Log.e("Show all users " ,"UserName: "+ name);
                    User user = new User(userId, name, null, 0, 0);
                    users.add(user);
                }
            }
            showUsersFragment();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("Firebase error", databaseError.getMessage());
        }
    };
    private void showAllUsers(){
        users = new ArrayList<>();
        usersRef.addValueEventListener(userValueListener);
    }

    private void showUsersFragment(){
        usersRef.removeEventListener(userValueListener);
        //Add or replace fragment in container
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        SelectUserFragment fragment = new SelectUserFragment(this, users);
        fragTrans.replace(R.id.testUserAndGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();
    }
    private void showGroupsFragment(){
        groupsRef.removeEventListener(groupValueListener);
        //Add or replace fragment in container
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        SelectGroupFragment fragment = new SelectGroupFragment(this, groups);
        fragTrans.replace(R.id.testUserAndGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();
    }

    @Override
    public void GroupSelected(Group group) {
        //Add or replace fragment in container
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        ShowGroupFragment fragment = new ShowGroupFragment(this, group);
        fragTrans.replace(R.id.testUserAndGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();
    }

    @Override
    public void UserSelected(User user) {
        //Add or replace fragment in container
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        ShowUserFragment fragment = new ShowUserFragment(user);
        fragTrans.replace(R.id.testUserAndGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();
    }
}
