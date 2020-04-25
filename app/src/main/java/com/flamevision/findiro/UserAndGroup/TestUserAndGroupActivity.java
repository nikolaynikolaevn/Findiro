package com.flamevision.findiro.UserAndGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.flamevision.findiro.R;

import java.util.ArrayList;
import java.util.List;

public class TestUserAndGroupActivity extends AppCompatActivity implements SelectGroupFragment.GroupReceiver, SelectUserFragment.UserReceiver {

    private List<Group> groups = new ArrayList<>();
    private List<User> users = new ArrayList<>();;
    private int phase = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_user_and_group);

        users.add((new User("Gamer_55", null, false)));
        users.add((new User("Coder_99", null, false)));
        users.add((new User("FindiroUser1", null, false)));
        users.add((new User("FindiroUser2", null, false)));
        users.add((new User("Roland Venders", null, false)));
        users.add((new User("Henk Smit", null, false)));
        users.add((new User("SmartGuy7", null, false)));
        users.add((new User("HappyGirl34", null, false)));
        users.add((new User("HappyBoy12", null, false)));

        List<String> groupNames = new ArrayList<>();
        groupNames.add("The best friends");
        groupNames.add("Gamer Group");
        groupNames.add("Music Club");
        groupNames.add("FriendlyMembers");
        groupNames.add("CoolGuys");
        groupNames.add("MathClass Group");
        groupNames.add("Movie Night");
        groupNames.add("Pokemon Go");
        groupNames.add("Calm group");

        for(int i = 0; i < groupNames.size(); i++){
            Group g;
            List<User> groupUsers = new ArrayList<>();
            for(int j = i; j < users.size(); j++){
                groupUsers.add(users.get(j));
            }
            g = new Group(groupUsers, groupUsers.get(0), groupNames.get(i));
            groups.add(g);
        }

        //Add or replace fragment in container
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        SelectGroupFragment fragment = new SelectGroupFragment(this, groups);
        fragTrans.add(R.id.TestUserAndGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();
    }

    @Override
    public void GroupSelected(Group group) {
        if(phase == 0){
            phase = 1;
            //Add or replace fragment in container
            FragmentManager fragManager = getSupportFragmentManager();
            FragmentTransaction fragTrans = fragManager.beginTransaction();
            ShowGroupFragment fragment = new ShowGroupFragment(this, group);
            fragTrans.replace(R.id.TestUserAndGroupFragContainer, fragment, "MyTag");
            fragTrans.commit();
        }
    }

    @Override
    public void UserSelected(User user) {
        if(phase == 1){
            phase = 2;
            //Add or replace fragment in container
            FragmentManager fragManager = getSupportFragmentManager();
            FragmentTransaction fragTrans = fragManager.beginTransaction();
            ShowUserFragment fragment = new ShowUserFragment(user);
            fragTrans.replace(R.id.TestUserAndGroupFragContainer, fragment, "MyTag");
            fragTrans.commit();
        }
    }
}
