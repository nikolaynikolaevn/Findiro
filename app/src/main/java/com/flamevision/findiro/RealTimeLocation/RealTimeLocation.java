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
    private DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
    private UserReference groupMemberReference;
    private ArrayList<User> users = new ArrayList<>();
    private HashMap<String, Marker> uidMarkerHashMap = new HashMap<>();

    public RealTimeLocation() {
        ValueEventListener groupValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Show all groups", "Total groups: " + snapshot.getChildrenCount());
                for (DataSnapshot groupSnapShot : snapshot.getChildren()) {
                    Group group = new GroupReference(groupSnapShot.getKey(), null);
                    groups.add(group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase error", databaseError.getMessage());
            }
        };
        this.groupsRef.addListenerForSingleValueEvent(groupValueListener);
    }

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void onLogin(String userId) {
        this.currentUserReference = new UserReference(userId, this, false);
        this.updateLocationReference = FirebaseDatabase.getInstance().getReference("Users/").child(userId).child("location");
    }

    public void onLogout() {
        this.groupMemberReference = null;
        this.updateLocationReference = null;
        this.currentUserReference = null;
        this.groupsRef = null;
        this.groups = null;

        for (Marker marker : uidMarkerHashMap.values()) {
            marker.remove();
        }

        this.uidMarkerHashMap.clear();
    }

    public void groupSelected(Group group) {
        for (String userId : group.getMembers()) {
            if (!userId.equals(currentUserReference.getUserId())) {

                groupMemberReference = new UserReference(userId, this, false);
                users.add(groupMemberReference);

                Marker marker = null;
                if (groupMemberReference.getLatitude() != null || groupMemberReference.getLongitude() != null) {
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
        // Check old user long with new long....
        if (newUser.getUserId().equals(currentUserReference.getUserId())) {
            return;
        }

        if (newUser.getLongitude() == null || newUser.getLatitude() == null) {
            return;
        }

        if (oldUser.getLongitude() == null && oldUser.getLatitude() == null) {
            if (uidMarkerHashMap.get(oldUser.getUserId()) == null) {
                addMarker(oldUser, newUser);
                return;
            }
        }

        if (!oldUser.getLatitude().equals(newUser.getLatitude()) || !oldUser.getLongitude().equals(newUser.getLongitude())) {
            Marker marker = uidMarkerHashMap.get(oldUser.getUserId());
            marker.setPosition(new LatLng(newUser.getLatitude(), newUser.getLongitude()));
        }
    }

    public void onLocationChanged(Location location) {
        if (currentUserReference != null) {
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
