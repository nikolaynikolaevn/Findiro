package com.flamevision.findiro.RealTimeLocation;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flamevision.findiro.UserAndGroup.Group;
import com.flamevision.findiro.UserAndGroup.User;
import com.flamevision.findiro.UserAndGroup.UserReference;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DatabaseReference;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class RealTimeLocationTest {

    RealTimeLocation realTimeLocationSUT = new RealTimeLocation(null);
    GoogleMap googleMap = null;

    @Test
    public void onMapReady_mapIsSet() {
        // Arrange

        // Act
        realTimeLocationSUT.mapReady(googleMap);
        GoogleMap googleMapReturned = realTimeLocationSUT.getGoogleMap();

        // Assert
        assertThat("Map set is not set correctly", googleMapReturned, is(googleMap));
    }

    @Test
    public void onLogin_referencesSet() {
        // Arrange
        String userId = "123456";
        UserReference userReference = new UserReference(userId, null, false);

        // Act
        realTimeLocationSUT.login(userReference);
        UserReference currentUserReference = realTimeLocationSUT.getCurrentUserReference();
        DatabaseReference updateLocationReference = realTimeLocationSUT.getUpdateLocationReference();
        DatabaseReference userGroupsReference = realTimeLocationSUT.getUserGroupsReference();
        DatabaseReference groupsReference = realTimeLocationSUT.getGroupsRef();

        // Assert
        assertThat(updateLocationReference.getPath().toString(), containsString("/Users/" + userId + "/location"));
        assertThat(userGroupsReference.getPath().toString(), containsString("/Users/" + userId + "/groups"));
        assertThat(currentUserReference.getUserId(), is(userId));
//        assertThat(groupsReference.getPath().toString(), containsString("/Groups"));
    }

    @Test
    public void groupSelected_usersAddedAndMarkersPlaced() {
//        // Arrange
//        Group group = new Group();
//        group.getMembers().add("User 1");
//        group.getMembers().add("User 2");
//
//        int markersOnMap;
//        int usersInGroup;
//
//        String userId = "123456";
//        UserReference userReference = new UserReference(userId, null, false);
//
//
//        realTimeLocationSUT.login(userReference);
//
//        // Act
//        realTimeLocationSUT.groupSelected(group);
//        markersOnMap = realTimeLocationSUT.getUidMarkerHashMap().size();
//        usersInGroup = realTimeLocationSUT.getUsers().size();
//
//        // Assert
//        assertThat(markersOnMap, is(2));
//        assertThat(usersInGroup, is(group.getMembers().size()));
    }


}