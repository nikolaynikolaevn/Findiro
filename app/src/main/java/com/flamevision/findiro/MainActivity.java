package com.flamevision.findiro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import com.flamevision.findiro.LiveLocationDemo.SimpleUser;
import com.flamevision.findiro.LoginAndSignup.TestLoginAndSignupActivity;
import com.flamevision.findiro.UserAndGroup.TestUserAndGroupActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private Button btnTestUserAndGroup;
    private Button btnTestLoginAndSignUp;
    private DatabaseReference mDatabase;
    private DatabaseReference mGroupReference;

    private GoogleMap gm;
    LocationManager lm;
    private Marker m;

    private String BRICE = "brice1uid";

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
                startActivity(intent);
            }
        });

        MapFragment mf = new MapFragment();
        getFragmentManager().beginTransaction().add(R.id.framelayout_main_fragmentcontainer, mf).commit();

        mf.getMapAsync(this);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mGroupReference = FirebaseDatabase.getInstance().getReference("groups/groupId");

        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear all markers
                gm.clear();

                // Get all users in the group and loop through them
                for (DataSnapshot item : dataSnapshot.child("users").getChildren()) {
                    // Create user from item
                    SimpleUser simpleUser = item.getValue(SimpleUser.class);

                    LatLng coordinates = coordinates = new LatLng(simpleUser.getLatitude(), simpleUser.getLongitude());
                    m = gm.addMarker(new MarkerOptions().position(coordinates).title(simpleUser.getUid()));
                }

//                SimpleGroup simpleGroup = dataSnapshot.getValue(SimpleGroup.class);
//                ArrayList<SimpleGroupUser> simpleGroupUsers = simpleGroup.getUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error getting group update");
            }
        };
        mGroupReference.addValueEventListener(groupListener);
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
        SimpleUser simpleUser = new SimpleUser();
        simpleUser.setUid(BRICE);
        simpleUser.setLatitude(location.getLatitude());
        simpleUser.setLongitude(location.getLongitude());

        mDatabase.child("users").child(BRICE).child("location").setValue("Latitude: " + location.getLatitude() + " - Longitude: " + location.getLongitude());
        mDatabase.child("groups").child("groupId").child("users").child(simpleUser.getUid()).setValue(simpleUser);
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
}
