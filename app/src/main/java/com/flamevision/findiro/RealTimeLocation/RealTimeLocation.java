package com.flamevision.findiro.RealTimeLocation;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.Random;

public class RealTimeLocation implements UserReference.UserReferenceUpdate {
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

    private ValueEventListener groupValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.e("Show all groups", "Total groups: " + snapshot.getChildrenCount());
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
            for (DataSnapshot groupSnapShot : snapshot.getChildren()) {
                String group = groupSnapShot.getValue(String.class);
                userGroups.add(group);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("Firebase error", databaseError.getMessage());
        }
    };

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void onLogin(String userId) {
        this.currentUserReference = new UserReference(userId, this, false);
        this.updateLocationReference = FirebaseDatabase.getInstance().getReference("Users/").child(userId).child("location");

        this.userGroupsReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("groups");
        this.userGroupsReference.addListenerForSingleValueEvent(userGroupsValueListener);

        this.groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        this.groupsRef.addListenerForSingleValueEvent(groupValueListener);
    }

    /**
     * Remove all data
     */
    public void onLogout() {
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

        // If no location data is present do nothing
        if (newUser.getLongitude() == null || newUser.getLatitude() == null) {
            return;
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

    public void onLocationChanged(Location location) {
        if (currentUserReference != null) { // If null user is logged out
            updateLocationReference.child("long").setValue(location.getLongitude());
            updateLocationReference.child("lat").setValue(location.getLatitude());
        }
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    private void addMarker(User oldUser, UserReference newUser) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(newUser.getLatitude(), newUser.getLongitude()))
                .title(newUser.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
        uidMarkerHashMap.put(oldUser.getUserId(), marker);
    }
}
