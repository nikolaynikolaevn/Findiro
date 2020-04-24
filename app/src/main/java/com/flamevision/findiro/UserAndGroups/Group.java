package com.flamevision.findiro.UserAndGroups;

import java.util.List;

public class Group {
    private List<User> members;
    private User groupCreator;


    public Group(List<User> members, User groupCreator) {
        this.members = members;
        this.groupCreator = groupCreator;
    }

    public List<User> getMembers() {
        return members;
    }

    public User getGroupCreator() {
        return groupCreator;
    }
}
