package com.flamevision.findiro;

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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flamevision.findiro.LoginAndSignup.CustomLoginActivity;
import com.flamevision.findiro.UserAndGroup.TestUserAndGroupActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private Button btnTestUserAndGroup;
    private Button btnTestLoginAndSignup;

    private GoogleMap gm;
    LocationManager lm;
    private Marker m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTestUserAndGroup = findViewById(R.id.mainTestUserAndGroupButton);
        btnTestUserAndGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestUserAndGroupActivity.class);
                startActivity(intent);
            }
        });

        btnTestLoginAndSignup = findViewById(R.id.mainTestLoginAndSignup);
        btnTestLoginAndSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomLoginActivity.class);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]  grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted by the user
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
                }
                else {
                    // permission was denied by the user
                }
                return;
        }
        // other 'case' lines to check for other permissions this app might request
    }
}
