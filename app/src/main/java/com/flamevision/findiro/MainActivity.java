package com.flamevision.findiro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flamevision.findiro.Profile.Fragment_EditProfile;
import com.flamevision.findiro.RealTimeLocation.RealTimeLocation;
import com.flamevision.findiro.UserAndGroup.AllGroupsFragment;
import com.flamevision.findiro.UserAndGroup.CreateGroupFragment;
import com.flamevision.findiro.UserAndGroup.Group;
import com.flamevision.findiro.UserAndGroup.UserReference;
import com.flamevision.findiro.UserAndGroup.SelectGroupFragment;
import com.flamevision.findiro.UserAndGroup.UserReference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SelectGroupFragment.GroupReceiver, NavigationView.OnNavigationItemSelectedListener, RealTimeLocation.UserRange  {

    private final int USER_LOGIN_CODE = 1;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    private GoogleMap gm;
    LocationManager lm;
    private Marker m;

    private String userId = null;

    private TextView title;
    private TextView navLoggedInName;
    private TextView navLoggedInEmail;

    /* Firebase */
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    /* Custom auth */
    RealTimeLocation realTimeLocation;
    UserReference loggedInUser;

    Fragment selectGroupFragment;

    SupportMapFragment mf;

    private UserReference curUserReference = null;
    private String currentGroupName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.fragment_title);
        title.setText(getString(R.string.home));

        mf = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mf).addToBackStack("first").commit();
        mf.getMapAsync(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                ImageView navHeaderImage = findViewById(R.id.nav_image);
                TextView navHeaderMail = findViewById(R.id.nav_email);
                TextView navHeaderName = findViewById(R.id.nav_name);
                TextView navCurrentGroup = findViewById(R.id.nav_group);

                if (curUserReference != null) {
                    navHeaderName.setText(curUserReference.getName());
                    if (curUserReference.getPicture() == null) {
                        Drawable defaultPic = getResources().getDrawable(R.drawable.ic_user);
                        navHeaderImage.setImageDrawable(defaultPic);
                    }
                    else {
                        Bitmap curPic = curUserReference.getPicture();
                        int maxAllowed = 100;
                        int maxSize = Math.max(curPic.getWidth(), curPic.getHeight());
                        int div = maxSize/maxAllowed;
                        Bitmap smallPic = Bitmap.createScaledBitmap(curPic, curPic.getWidth()/div, curPic.getHeight()/div, true);
                        navHeaderImage.setImageBitmap(smallPic);
                        //navHeaderImage.setImageBitmap(curUserReference.getPicture());
                    }

                    navHeaderMail.setText(firebaseUser.getEmail());

                    if (currentGroupName != null) {
                        navCurrentGroup.setText(currentGroupName);
                    }
                }
                else{
                    if(firebaseUser != null){
                        navHeaderName.setText("FB user known");
                        curUserReference = new UserReference(firebaseUser.getUid(), null, true);
                    }
                    else {
                        navHeaderName.setText("FB user unknown");
                    }
                }

                super.onDrawerOpened(drawerView);
            }
        };


        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        realTimeLocation = new RealTimeLocation(this);
        realTimeLocation.AddListener(this);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);



        selectGroupFragment = new SelectGroupFragment(MainActivity.this, realTimeLocation.getGroups());
        notificationManager = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onStart() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference curUserOnlineRef = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid() + "/online");
            curUserOnlineRef.setValue(true);
            curUserOnlineRef.onDisconnect().setValue(false);

            //set user values in nav header
            curUserReference = new UserReference(firebaseUser.getUid(), null, true);
            realTimeLocation.login(curUserReference);
        }
        super.onStart();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = mf;
                title.setText(getString(R.string.home));
                break;
            case R.id.nav_my_groups:
                fragment = new SelectGroupFragment(MainActivity.this, realTimeLocation.getGroups());
                title.setText(getString(R.string.my_groups));
                break;
            case R.id.nav_all_groups:
                fragment = new AllGroupsFragment();
                title.setText(getString(R.string.all_groups));
                break;
            case R.id.nav_create_group:
                fragment = new CreateGroupFragment();
                title.setText(R.string.create_group);
                break;
            case R.id.nav_update:
                //open profile
                //intent = new Intent(MainActivity.this, EditProfile_activity.class);
                //startActivity(intent);
                fragment = new Fragment_EditProfile();
                title.setText(R.string.update_profile);
                break;
            case R.id.nav_logout:
                realTimeLocation.logout();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    DatabaseReference curUserOnlineRef = FirebaseDatabase.getInstance().getReference("Users/" + auth.getCurrentUser().getUid() + "/online");
                    curUserOnlineRef.setValue(false);
                    curUserOnlineRef.onDisconnect().cancel();
                    auth.signOut();
                }
                curUserReference = null;
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                fragment = mf;
                title.setText(getString(R.string.home));
        }

        if (fragment != null) {
            getSupportFragmentManager().popBackStack("first", 0);
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
        realTimeLocation.mapReady(googleMap);

        Location location = null;

        Criteria criteria = new Criteria();
        String bestProvider = lm.getBestProvider(criteria, false);
        if (bestProvider != null)
            location = lm.getLastKnownLocation(bestProvider);

        LatLng coordinates;
        if (location != null)
            coordinates = new LatLng(location.getLatitude(), location.getLongitude());
        else
            coordinates = new LatLng(0, 0);

        m = gm.addMarker(new MarkerOptions().position(coordinates).title("My position"));
        gm.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (m != null) {
            m.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            realTimeLocation.locationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted by the user
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
                } else {
                    // permission was denied by the user
                }
                return;
        }
        // other 'case' lines to check for other permissions this app might request
    }

    @Override
    public void GroupSelected(Group group) {
        realTimeLocation.groupSelected(group);
        currentGroupName = realTimeLocation.getCurrentGroup().getName();
        getSupportFragmentManager().beginTransaction().remove(selectGroupFragment).commit();
    }

    NotificationManager notificationManager;
    public void showNotification(String title, String content, int id){

        //SETTING MANAGER AND CHANNEL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel("abc", "Findiro distance", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(nc);
        }

        //SETTING NOTIFICATION SETTINGS
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "abc");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setAutoCancel(true);

        //BUILDING AND SENDING NOTIFICATION
        Notification notification = null;
        notification = builder.build();
        notificationManager.notify(id, notification);

    }

    @Override
    public void UsersInRangeChanged(@NonNull List<UserReference> usersInRange) {
        int notificationId = 9999;
        String title = "Findiro users nearby";
        String content = "";
        if(usersInRange.size() == 0){ notificationManager.cancel(notificationId);return;}
        if(usersInRange.size() == 1){content = usersInRange.get(0).getName();}
        else {
            for(int i = 0; i < usersInRange.size(); i++){
                if(i == usersInRange.size() - 1){
                    content += " and " + usersInRange.get(i).getName();
                }
                else {
                    if(i != 0){content += ", ";}
                    content += usersInRange.get(i).getName();
                }
            }
        }
        showNotification(title, content, notificationId);
        //Log.e("USER NEARBY", title + " ||| " + content);
    }
}