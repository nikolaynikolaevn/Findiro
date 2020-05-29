package com.flamevision.findiro;

import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.flamevision.findiro.LoginAndSignup.TestLoginAndSignupActivity;
import com.flamevision.findiro.Profile.Login2_activity;
import com.flamevision.findiro.UserAndGroup.Group;
import com.flamevision.findiro.UserAndGroup.GroupReference;
import com.flamevision.findiro.UserAndGroup.SelectGroupFragment;
import com.flamevision.findiro.UserAndGroup.TestUserAndGroupActivity;
import com.flamevision.findiro.UserAndGroup.User;
import com.flamevision.findiro.UserAndGroup.UserReference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SelectGroupFragment.GroupReceiver, UserReference.UserReferenceUpdate {

    private final int USER_LOGIN_CODE = 1;

    private Button btnTestUserAndGroup;
    private Button btnTestLoginAndSignUp;
    private Button btnSelectGroup;

    //try new log in UI
    private Button btnTestTheo;

    private GoogleMap gm;
    LocationManager lm;
    private Marker m;


    private String userId = null;
    private UserReference userReference;
    private DatabaseReference databaseReference;
    private DatabaseReference groupsReference;
    Fragment selectGroupFragment;


    private HashMap<String, Marker> uidMarkerHashMap = new HashMap<>();
    private ArrayList<UserReference> userReferences = new ArrayList<>();
    final ArrayList<Group> groups = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();


    private DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
    private UserReference userReferenceThing;
    private DatabaseReference updateLocationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        btnTestUserAndGroup = findViewById(R.id.mainTestUserAndGroupButton);
        btnTestUserAndGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestUserAndGroupActivity.class);
                startActivity(intent);
            }
        });

        btnTestLoginAndSignUp = findViewById(R.id.mainTestLoginAndSignup);
        btnTestLoginAndSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestLoginAndSignupActivity.class);
                startActivityForResult(intent, USER_LOGIN_CODE);
            }
        });

        btnTestTheo = findViewById(R.id.TestTheo);
        btnTestTheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login2_activity.class);
                startActivity(intent);
            }
        });


        ValueEventListener groupValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Show all groups", "Total groups: " + snapshot.getChildrenCount());
                for (DataSnapshot groupSnapShot : snapshot.getChildren()) {
                    Group group = new GroupReference(groupSnapShot.getKey(), null);
                    groups.add(group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase error", databaseError.getMessage());
            }
        };


        MapFragment mf = new MapFragment();
        getFragmentManager().beginTransaction().add(R.id.framelayout_main_fragmentcontainer, mf).commit();
        mf.getMapAsync(this);

        groupsRef.addListenerForSingleValueEvent(groupValueListener);

        btnSelectGroup = findViewById(R.id.buttonSelectGroup);
        selectGroupFragment = new SelectGroupFragment(MainActivity.this, groups);

        btnSelectGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager().beginTransaction().add(R.id.framelayout_main_fragmentcontainer, selectGroupFragment).commit();
            }
        });

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
    }

    private void showSelectGroupFragment(int frameLayout, ArrayList<Group> groups) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
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
        }
        if (userReference != null) {
            updateLocationReference.child("long").setValue(location.getLongitude());
            updateLocationReference.child("lat").setValue(location.getLatitude());
//            userReference.child("location").child("long").setValue(location.getLongitude());
//            userReference.child("location").child("lat").setValue(location.getLatitude());
//            userReference.get
//            for (String group : groups) {
//                groupsReference.child(group).child("members").child(userId).child("longitude").setValue(location.getLongitude());
//                groupsReference.child(group).child("members").child(userId).child("latitude").setValue(location.getLatitude());
//            }
        }

        // Only own stuff to user json
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_LOGIN_CODE) {
            if (resultCode == R.integer.LoggedIn) {
                userId = data.getStringExtra("userId");
                groupsReference = FirebaseDatabase.getInstance().getReference("Groups");
                userReference = new UserReference(userId, this, false);
                updateLocationReference = FirebaseDatabase.getInstance().getReference("Users/").child(userId).child("location");
//                userGroupsReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snap) {
//                        for (DataSnapshot item : snap.getChildren()) {
//                            groups.add((String) item.getValue());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            } else if (resultCode == R.integer.LoggedOut) {
                userId = null;
                userReference = null;
            }
        }
    }

    @Override
    public void GroupSelected(Group group) {
        for (String userId : group.getMembers()) {
            if (!userId.equals(userReference.getUserId())) {

                userReferenceThing = new UserReference(userId, this, false);
//            userReferences.add(userReferenceThing);
                users.add(userReferenceThing);

                Marker marker = null;
                if (userReferenceThing.getLatitude() != null || userReferenceThing.getLongitude() != null) {
                    marker = gm.addMarker(new MarkerOptions()
                            .position(new LatLng(userReferenceThing.getLatitude(), userReferenceThing.getLongitude()))
                            .title(userReferenceThing.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
                }
                uidMarkerHashMap.put(userReferenceThing.getUserId(), marker);
            }

        }
        getSupportFragmentManager().beginTransaction().remove(selectGroupFragment).commit();
    }

    @Override
    public void UserValuesUpdated(@NonNull User oldUser, @NonNull UserReference newUser) {
        // Check old user long with new long....
        if (newUser.getUserId().equals(userReference.getUserId())) {
            return;
        }

        if (newUser.getLongitude() == null || newUser.getLatitude() == null) {
            return;
        }

        if (oldUser.getLongitude() == null && oldUser.getLatitude() == null) {
            if (uidMarkerHashMap.get(oldUser.getUserId()) == null) {
                addMarker(oldUser, newUser);
                return;
            }
        }

        if (!oldUser.getLatitude().equals(newUser.getLatitude()) || !oldUser.getLongitude().equals(newUser.getLongitude())) {
            Marker marker = uidMarkerHashMap.get(oldUser.getUserId());
            marker.setPosition(new LatLng(newUser.getLatitude(), newUser.getLongitude()));
        }

        // Lijst bijhouden
    }

    private void addMarker(User oldUser, UserReference newUser) {

        Marker marker = gm.addMarker(new MarkerOptions()
                .position(new LatLng(newUser.getLatitude(), newUser.getLongitude()))
                .title(newUser.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
        uidMarkerHashMap.put(oldUser.getUserId(), marker);
    }
}
