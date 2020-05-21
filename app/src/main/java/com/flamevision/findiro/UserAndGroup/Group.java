package com.flamevision.findiro.UserAndGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Group {
    protected String groupId;
    protected List<String> members = new ArrayList<>();
    protected String groupCreator;
    protected String name;

    public Group(){

    }

    public Group(String groupId, String name, String groupCreator, List<String> members) {
        this.groupId = groupId;
        this.members = members;
        this.groupCreator = groupCreator;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        String holder = "groupId: " + groupId;
        holder += "\nname: " + name;
        holder += "\ngroupCreator: " + groupCreator;
        holder += "\nmembers:";
        for(String s: members){
            holder += "\n\t • " + s;
        }
        return holder;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public String getName() {
        return name;
    }
}
