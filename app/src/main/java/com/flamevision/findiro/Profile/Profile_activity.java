package com.flamevision.findiro.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.flamevision.findiro.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Profile_activity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int MAP_PERMISSION = 001;

    private GoogleApiClient googleApiClient;
    private TextView userName, tvemail;
    private FirebaseFirestore mFirestore;
    static DocumentReference docRef;
    private FirebaseAuth auth;

    private FirebaseUser currentUser;
    ImageView profileImage;
    static String photoUrl;
    //get current email
    String email;
    String username;
    //very useful tutorial about Glide in github
    //https://github.com/bumptech/glide

    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        btnEditProfile = findViewById(R.id.ProfileTest);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_activity.this, EditProfile_activity.class);
                startActivity(intent);
            }
        });

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//
//        //para
//        profileImage = (ImageView) findViewById(R.id.ivProfileImage);
//        userName = findViewById(R.id.tvMyUser);
//        tvemail = findViewById(R.id.tvMyAddress);
//
//        auth = FirebaseAuth.getInstance();
//        currentUser = auth.getCurrentUser();
//
//        this.mFirestore = FirebaseFirestore.getInstance();
//        email = currentUser.getEmail();
//
//        //Glide.with(this).load(photoUrl).into(profileImage);
//        //gets reference to user's document
//        docRef = mFirestore.collection("userTest").document(email);
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Log.d("got users email", "onSuccess: ");
//                getData();
//                getPhotoUrl();
//            }
//        });
//        if (googleApiClient == null) {
//            //...
//        }
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    // Update UI with location data
//                    //update db with new location
//                    //have not done yet
//                }
//            }
//        };
//    }
//    public void getData() {
//        docRef.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            username = documentSnapshot.getString("first");
//                            tvemail.setText(email);
//                            userName.setText(username);
//                        } else {
//                            Toast.makeText(Profile_activity.this, "Not found", Toast.LENGTH_SHORT).show();
//                            Log.d("error", "onError: ");
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(Profile_activity.this, "Coundn't find", Toast.LENGTH_SHORT).show();
//                        Log.d("Another error", "onError: ");
//                    }
//                });
//    }
//
//    //edit my profile page
//    public void startEditProfile(View view) {
//        Intent intent = new Intent(Profile_activity.this, EditProfile_activity.class);
//        startActivity(intent);
//    }
//
//    //get Photo Url
//    private void getPhotoUrl() {
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("onStart", "Listen failed.", e);
//                    return;
//                }
//                if (documentSnapshot.exists()) {
//                    if (documentSnapshot.get("photoUrl") != null) {
//                        photoUrl = documentSnapshot.get("photoUrl").toString();
//                        Log.d("url is found " + photoUrl, "onEvent: ");
//                        //Glide.with(getApplicationContext()).load(photoUrl).into(profileImage);
//                    }
//                }
//            }
//        });
//    }
//    @Override
//    protected void onStop() {
//        googleApiClient.disconnect();
//        super.onStop();
//    }
    }
}

