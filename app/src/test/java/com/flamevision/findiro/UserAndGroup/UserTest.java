package com.flamevision.findiro.UserAndGroup;


import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;


public class UserTest {
    String userId = "12345";
    String name = "Henk";
    double longitude = 12.3543;
    double latitude = 42.123;
    List<String> groups = new ArrayList<>();

    double epsilon = 1E-5;

    @Test
    public void CreateUser(){
        User user = new User();
    }

    @Test
    public void CreateUserWithData(){
        User user = new User(userId, name, groups, longitude, latitude);
        assert(user.getUserId()).equals(userId);
        assert(user.getGroups()).equals(groups);
        assert(user.getName()).equals(name);
        assertEquals(user.getLongitude(), longitude, epsilon);
        assertEquals(user.getLatitude(), latitude, epsilon);
    }

    @Test
    public void ToStringContainsUserFields(){
        User user = new User(userId, name, groups, longitude, latitude);
        String result = user.toString();
        assert(result).contains(userId);
        assert(result).contains(name);
        assert(result).contains("" + longitude);
        assert(result).contains("" + latitude);
    }


}