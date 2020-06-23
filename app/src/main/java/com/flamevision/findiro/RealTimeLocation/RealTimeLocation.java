package com.flamevision.findiro.RealTimeLocation;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.flamevision.findiro.R;
import com.flamevision.findiro.UserAndGroup.Group;
import com.flamevision.findiro.UserAndGroup.GroupReference;
import com.flamevision.findiro.UserAndGroup.User;
import com.flamevision.findiro.UserAndGroup.UserReference;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

interface IUpdateGroupsReference{
    void onUserGroupsReceived();
}

public class RealTimeLocation implements UserReference.UserReferenceUpdate, IUpdateGroupsReference {

    private final Activity activity;
    private UserReference currentUserReference;
    private GoogleMap googleMap;
    private DatabaseReference updateLocationReference;
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<String> userGroups = new ArrayList<>();
    private DatabaseReference groupsRef;
    private UserReference groupMemberReference;
    private ArrayList<UserReference> users = new ArrayList<>();
    private HashMap<String, Marker> uidMarkerHashMap = new HashMap<>();

    private DatabaseReference userGroupsReference;
    private Group currentGroup;

    public UserReference getCurrentUserReference() {
        return currentUserReference;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public DatabaseReference getUpdateLocationReference() {
        return updateLocationReference;
    }

    public ArrayList<String> getUserGroups() {
        return userGroups;
    }

    public DatabaseReference getGroupsRef() {
        return groupsRef;
    }

    public UserReference getGroupMemberReference() {
        return groupMemberReference;
    }

    public ArrayList<UserReference> getUsers() {
        return users;
    }

    public HashMap<String, Marker> getUidMarkerHashMap() {
        return uidMarkerHashMap;
    }

    public DatabaseReference getUserGroupsReference() {
        return userGroupsReference;
    }

    public ValueEventListener getGroupValueListener() {
        return groupValueListener;
    }

    public ValueEventListener getUserGroupsValueListener() {
        return userGroupsValueListener;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    private ValueEventListener groupValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.e("Show all groups", "Total groups: " + snapshot.getChildrenCount());
            groups.clear();
            for (DataSnapshot groupSnapShot : snapshot.getChildren()) {
                Group group = new GroupReference(groupSnapShot.getKey(), null);
                for (String g : userGroups) {
                    // Only add group data for groups a user is part of
                    if (group.getGroupId().equals(g)) {
                        groups.add(group);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("Firebase error", databaseError.getMessage());
        }
    };

    private ValueEventListener userGroupsValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.e("Show user groups", "Total groups: " + snapshot.getChildrenCount());
            // Add users groups to a list
            userGroups.clear();
            for (DataSnapshot groupSnapShot : snapshot.getChildren()) {
                String group = groupSnapShot.getValue(String.class);
                userGroups.add(group);
                onUserGroupsReceived(); // Create the actual group reference
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("Firebase error", databaseError.getMessage());
        }
    };

    public RealTimeLocation(Context context) {
        this.activity = (Activity) context;
    }

    public void mapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void login(UserReference currentUserReference) {
        this.currentUserReference = currentUserReference;
        this.updateLocationReference = FirebaseDatabase.getInstance().getReference("Users/").child(currentUserReference.getUserId()).child("location");

        this.userGroupsReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserReference.getUserId()).child("groups");
        this.userGroupsReference.addValueEventListener(userGroupsValueListener);

        // Groups reference will be set after we get the groups the user is part of
    }

    /**
     * Remove all data
     */
    public void logout() {
        this.groupMemberReference = null;
        this.updateLocationReference = null;
        this.currentUserReference = null;
        this.groups = null;
        this.userGroups = null;


        this.userGroupsReference.removeEventListener(userGroupsValueListener);
        this.userGroupsReference = null;

        this.groupsRef.removeEventListener(groupValueListener);
        this.groupsRef = null;

        clearListeners();

        this.uidMarkerHashMap.clear();
    }

    private void clearListeners() {
        for (UserReference user : users) {
            if (user != null) {
                user.RemoveListener(this);
            }
        }
        this.users.clear();

        // Clear old group data
        for (Marker marker : uidMarkerHashMap.values()) {
            if (marker != null) {
                marker.remove();
            }
        }
        this.uidMarkerHashMap.clear();
    }

    public void groupSelected(Group group) {

        this.currentGroup = group;
        // Close the SelectGroupFragment
        activity.onBackPressed();

        clearListeners();

        for (String userId : group.getMembers()) {
            if (!userId.equals(currentUserReference.getUserId())) { // Do not do anything when the data found is from the current user

                groupMemberReference = new UserReference(userId, this, false);
                users.add(groupMemberReference);

                Marker marker = null;
                // If there is location data add a marker
                if (groupMemberReference.getLatitude() != null && groupMemberReference.getLongitude() != null) {
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(groupMemberReference.getLatitude(), groupMemberReference.getLongitude()))
                            .title(groupMemberReference.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
                }
                uidMarkerHashMap.put(groupMemberReference.getUserId(), marker);
            }
        }
    }

    @Override
    public void UserValuesUpdated(@NonNull User oldUser, @NonNull UserReference newUser) {
        // If new data is from user on device do nothing
        if (newUser.getUserId().equals(currentUserReference.getUserId())) {
            return;
        }

        // Do not display marker for offline users
        if(!newUser.getOnline()){
            Marker marker = uidMarkerHashMap.get(newUser.getUserId());
            if(marker != null){
                marker.remove();
            }
            return;
        }

        // If no location data is present do nothing
        if (newUser.getLongitude() == null || newUser.getLatitude() == null) {
            return;
        }

        if (!(newUser.getLongitude() == null || newUser.getLatitude() == null)) {
            checkDistance(newUser);
        }

        // If there is no marker data yet
        if (oldUser.getLongitude() == null && oldUser.getLatitude() == null) {
            // And the marker in HashMap is null add a marker
            if (uidMarkerHashMap.get(oldUser.getUserId()) == null) {
                addMarker(oldUser, newUser);
                return;
            }
        }

        // There is a marker already
        // If the new data is different from the old data, update the marker location
        if (!oldUser.getLatitude().equals(newUser.getLatitude()) || !oldUser.getLongitude().equals(newUser.getLongitude())) {
            Marker marker = uidMarkerHashMap.get(oldUser.getUserId());
            marker.setPosition(new LatLng(newUser.getLatitude(), newUser.getLongitude()));
        }
    }

    public void locationChanged(Location location) {
        if (currentUserReference != null) { // If null user is logged out
            updateLocationReference.child("long").setValue(location.getLongitude());
            updateLocationReference.child("lat").setValue(location.getLatitude());
            for(UserReference userReference : users){
                checkDistance(userReference);
            }
        }
    }

    private void addMarker(User oldUser, UserReference newUser) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(newUser.getLatitude(), newUser.getLongitude()))
                .title(newUser.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
        uidMarkerHashMap.put(oldUser.getUserId(), marker);
    }

    @Override
    public void onUserGroupsReceived() {
        this.groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        this.groupsRef.addValueEventListener(groupValueListener);
    }


    public interface UserRange{
        void UsersInRangeChanged(@NonNull List<UserReference> usersInRange);
    }

    public void AddListener(UserRange me){
        listeners.add(me);
    }

    private List<UserRange> listeners = new ArrayList<>();
    private List<UserReference> inRangeUsers = new ArrayList<>();
    private void checkDistance(@NonNull UserReference otherUser){
        LatLng curPos = new LatLng(currentUserReference.getLatitude(), currentUserReference.getLongitude());
        LatLng otherPos = new LatLng(otherUser.getLatitude(), otherUser.getLongitude());
        if(distance(curPos, otherPos, 10)){ //add user
            if(!inRangeUsers.contains(otherUser)){
                inRangeUsers.add(otherUser);
                for(UserRange listener : listeners){
                    if(listener != null){
                        listener.UsersInRangeChanged(inRangeUsers);
                    }
                }
            }
        }
        else if(inRangeUsers.contains(otherUser)){ //remove user
            if(!distance(curPos, otherPos, 20)) {
                inRangeUsers.remove(otherUser);
                for(UserRange listener : listeners){
                    if(listener != null){
                        listener.UsersInRangeChanged(inRangeUsers);
                    }
                }
            }
        }
    }

    private boolean distance(LatLng latLng1, LatLng latLng2, float maxDistance) {
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);
        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);
        return loc1.distanceTo(loc2) <= maxDistance;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }
}
