package com.flamevision.findiro.UserAndGroup;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class User {
    protected String userId;
    protected String name;
    protected List<String> groups = new ArrayList<>();
    protected double longitude;
    protected double latitude;

    public User() {

    }

    public User(String userId, String name, List<String> groups, double longitude, double latitude) {
        this.userId = userId;
        this.name = name;
        this.groups = groups;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @NonNull
    @Override
    public String toString() {
        String holder = "userId: " + userId;
        holder += "\nname: " + name;
        holder += "\ngroups:";
        for(String s: groups){
            holder += "\n\t • " + s;
        }
        holder += "\nlongitude: " + longitude;
        holder += "\nlatitude: " + latitude;
        return holder;
    }

    public String getName() {
        return name;
    }
    public List<String> getGroups(){
        return groups;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
}
