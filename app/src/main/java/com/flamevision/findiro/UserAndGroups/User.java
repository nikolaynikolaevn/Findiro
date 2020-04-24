package com.flamevision.findiro.UserAndGroups;

import android.graphics.Bitmap;

public class User {
    private String name;
    private Bitmap profilePicture;
    private boolean hasCorona;

    public User(String name, Bitmap profilePicture, boolean hasCorona) {
        this.name = name;
        this.profilePicture = profilePicture;
        this.hasCorona = hasCorona;
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
}
