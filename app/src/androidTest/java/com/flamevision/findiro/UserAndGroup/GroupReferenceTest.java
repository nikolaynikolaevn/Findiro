package com.flamevision.findiro.UserAndGroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.flamevision.findiro.MainActivity;
import com.flamevision.findiro.UserAndGroup.Group;
import com.flamevision.findiro.UserAndGroup.GroupReference;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GroupReferenceTest {
    String groupId = "gid12345";

    //@Rule
    //public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void CreateGroupReference(){
        GroupReference groupReference = new GroupReference(groupId, null);
        assert (groupReference.getGroupId()).equals(groupId);
        assertFalse (groupReference.isUpdateErrorOccurred());
    }

    @Test
    public void GetCurrentGroup(){
        GroupReference groupReference = new GroupReference(groupId, null);
        Group group = groupReference.GetCurrentGroup();
        assert (group.getGroupId()).equals(groupId);
    }


}