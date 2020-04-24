package com.flamevision.findiro.UserAndGroup;

import androidx.annotation.NonNull;

import java.util.List;

public class Group {
    private List<User> members;
    private User groupCreator;
    private String name;

    public Group(@NonNull List<User> members, @NonNull User groupCreator, @NonNull String name) {
        this.members = members;
        this.groupCreator = groupCreator;
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public User getGroupCreator() {
        return groupCreator;
    }

    public String getName() {
        return name;
    }
}
