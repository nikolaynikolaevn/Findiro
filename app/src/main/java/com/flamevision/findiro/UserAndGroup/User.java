package com.flamevision.findiro.UserAndGroup;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class User {
    protected String userId;
    protected String name;
    protected List<String> groupIds = new ArrayList<>();
    protected Double longitude;
    protected Double latitude;
    protected String picturePath;

    protected Bitmap picture;

    public User() {

    }

    public User(String userId, String name, List<String> groupIds, Double longitude, Double latitude, String picturePath, Bitmap picture) {
        this.userId = userId;
        this.name = name;
        this.groupIds = groupIds;
        this.longitude = longitude;
        this.latitude = latitude;
        this.picturePath = picturePath;
        this.picture = picture;
    }

    @NonNull
    @Override
    public String toString() {
        String holder = "userId: " + userId;
        holder += "\nname: " + name;
        holder += "\npicturePath: " + picturePath;
        holder += "\npicture: " + picture;
        holder += "\ngroups:";
        for(String s: groupIds){
            holder += "\n\t • " + s;
        }
        holder += "\nlongitude: " + longitude;
        holder += "\nlatitude: " + latitude;
        return holder;
    }

    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public List<String> getGroupIds(){
        return groupIds;
    }
    public Double getLongitude(){
        return longitude;
    }
    public Double getLatitude(){
        return latitude;
    }
    public String getPicturePath() {
        return picturePath;
    }
    public Bitmap getPicture() {
        return picture;
    }
}
