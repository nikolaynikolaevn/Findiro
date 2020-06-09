package com.flamevision.findiro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.facebook.login.Login;
import com.flamevision.findiro.LoginAndSignup.TestLoginAndSignupActivity;
import com.flamevision.findiro.Profile.EditProfile_activity;
import com.flamevision.findiro.RealTimeLocation.RealTimeLocation;
import com.flamevision.findiro.UserAndGroup.Group;
import com.flamevision.findiro.UserAndGroup.SelectGroupFragment;
import com.flamevision.findiro.UserAndGroup.TestUserAndGroupActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements SelectGroupFragment.GroupReceiver {

    private final int USER_LOGIN_CODE = 1;

    private Button btnTestUserAndGroup;
    private Button btnTestLoginAndSignUp;
    private Button btnSelectGroup;

    //try new log in UI
    private Button btnTestTheo;

    //profile
    private Button btnProfile;

    private String userId = null;
    Fragment selectGroupFragment;

    final ArrayList<Group> groups = new ArrayList<>();

    RealTimeLocation realTimeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        finish();
        //SHOULD NOT BE STARTED ANYMORE
        if(false) {

            btnTestUserAndGroup = findViewById(R.id.mainTestUserAndGroupButton);
            btnTestUserAndGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, TestUserAndGroupActivity.class);
                    startActivity(intent);
                }
            });

            btnTestLoginAndSignUp = findViewById(R.id.mainTestLoginAndSignup);
            btnTestLoginAndSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, TestLoginAndSignupActivity.class);
                    startActivityForResult(intent, USER_LOGIN_CODE);
                }
            });

            selectGroupFragment = new SelectGroupFragment(HomeActivity.this, realTimeLocation.getGroups());
            btnSelectGroup = findViewById(R.id.buttonSelectGroup);

            btnSelectGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSupportFragmentManager().beginTransaction().add(R.id.framelayout_main_fragmentcontainer, selectGroupFragment).commit();
                }
            });
        }
    }

    @Override
    public void GroupSelected(Group group) {
        realTimeLocation.groupSelected(group);
        getSupportFragmentManager().beginTransaction().remove(selectGroupFragment).commit();
    }
}