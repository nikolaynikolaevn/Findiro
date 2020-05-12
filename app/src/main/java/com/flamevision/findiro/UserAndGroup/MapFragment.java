package com.flamevision.findiro.UserAndGroup;

import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flamevision.findiro.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class MapFragment extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    //For getting the location
    private FusedLocationProviderClient fusedLocationClient;
    private Object LocationServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //initialize the location provider
        //something wrong
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            return;
        }
        System.out.println("location is:" + fusedLocationClient.getLastLocation());
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            // Add a marker in User Location and move the camera
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in User Location"));
                            mMap.getMaxZoomLevel();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                            mMap.isBuildingsEnabled();
                            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                        }
                        System.out.println("location: " +location);
                    }
                });

    }
}
