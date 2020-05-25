package com.flamevision.findiro.UserAndGroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.flamevision.findiro.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class UserReferenceTest {
    String userId = "12345";

    //@Rule
    //public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void CreateUserReference(){
        UserReference userReference = new UserReference(userId, null);
        assert(userReference.getUserId()).equals(userId);
        assertFalse (userReference.isUpdateErrorOccurred());
    }

    @Test
    public void GetCurrentUser(){
        UserReference userReference = new UserReference(userId, null);
        User user = userReference.GetCurrentUser();
        assert (user.getUserId()).equals(userId);
    }


}
