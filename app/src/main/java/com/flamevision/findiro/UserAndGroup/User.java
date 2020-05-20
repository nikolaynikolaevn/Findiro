package com.flamevision.findiro.UserAndGroup;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {
    private String name;
    private Bitmap profilePicture;
    private boolean hasCorona;
    private Location location;
    private String uid;

    public User(String name, Bitmap profilePicture, boolean hasCorona) {
        this.name = name;
        this.profilePicture = profilePicture;
        this.hasCorona = hasCorona;
        this.uid = null;
    }

    public User(String name, Bitmap profilePicture, boolean hasCorona, Location location) {
        this(name, profilePicture, hasCorona);
        this.location = location;
    }

    public User(String uid){
        this.uid = uid;
        useUid(uid);
    }

    private void useUid(@NonNull String uid){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/" + uid + "/name");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    name = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public String getName() {
        return name;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public boolean isHasCorona() {
        return hasCorona;
    }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getUid() {
        return uid;
    }
}
