package com.flamevision.findiro.UserAndGroup;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GroupTest {
    String name = "myGroup";
    String groupId = "gid12345";
    String groupCreator = "uid12345";
    List<String> members = new ArrayList<>();

    @Test
    public void CreateGroup(){
        Group group = new Group();
    }

    @Test
    public void CreateGroupWithData(){
        Group group = new Group(groupId, name, groupCreator, members);
        assert(group.getGroupId()).equals(groupId);
        assert(group.getName()).equals(name);
        assert(group.getGroupCreator()).equals(groupCreator);
        assert(group.getMembers()).equals(members);
    }

    @Test
    public void ToStringContainsGroupFields(){
        Group group = new Group(groupId, name, groupCreator, members);
        String result = group.toString();
        assert(result).contains(groupId);
        assert(result).contains(groupCreator);
        assert(result).contains(name);
    }

}