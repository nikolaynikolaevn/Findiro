package com.flamevision.findiro.UserAndGroup;

import android.graphics.Bitmap;
import android.location.Location;

public class User {
    private String name;
    private Bitmap profilePicture;
    private boolean hasCorona;
    private Location location;

    public User(String name, Bitmap profilePicture, boolean hasCorona) {
        this.name = name;
        this.profilePicture = profilePicture;
        this.hasCorona = hasCorona;
    }

    public User(String name, Bitmap profilePicture, boolean hasCorona, Location location) {
        this(name, profilePicture, hasCorona);
        this.location = location;
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
}
