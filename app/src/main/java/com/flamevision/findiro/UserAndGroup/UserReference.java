package com.flamevision.findiro.UserAndGroup;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserReference extends User {

    public interface UserReferenceUpdate{
        void UserValuesUpdated(@NonNull User oldUser, @NonNull UserReference newUser);
    }
    private List<UserReferenceUpdate> listeners = new ArrayList<>();

    private boolean updatedOnce;
    private boolean updateErrorOccurred;

    private DatabaseReference userRef;

    private static  final  String log = "UserReference";
    private  static  boolean printUpdate = true;

    public UserReference(@NonNull String userId, UserReferenceUpdate listener)
    {
        super();
        this.userId = userId;
        this.updatedOnce = false;
        this.updateErrorOccurred = false;
        if(listener != null){AddListener(listener);}
        setupReference();
    }

    public void AddListener(UserReferenceUpdate listener){
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    public void RemoveListener(UserReferenceUpdate listener){
        listeners.remove(listener);
    }
    private void setupReference(){
        userRef = FirebaseDatabase.getInstance().getReference("Users/" + userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updatedOnce = true;
                updateValues(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                updateErrorOccurred = true;
                Log.e(log, "Database error occurred: " + databaseError.getMessage());
            }
        });
    }
    private void updateValues(DataSnapshot dataSnapshot){
        User oldUser = GetCurrentUser();

        Object oName = dataSnapshot.child("name").getValue();
        if(oName != null){
            name = oName.toString();
        }

        groups = new ArrayList<>();
        for(DataSnapshot groupIdSnapShot : dataSnapshot.child("groups").getChildren()){
            Object oGroupId = groupIdSnapShot.getValue();
            if(oGroupId != null){
                groups.add(oGroupId.toString());
            }
        }

        Object oLong = dataSnapshot.child("location").child("long").getValue();
        if(oLong != null){
            longitude = (int)oLong;
        }

        Object oLat = dataSnapshot.child("location").child("lat").getValue();
        if(oLat != null){
            latitude = (int)oLat;
        }

        if(printUpdate){printUser();}
        updateAllListeners(oldUser);
    }
    public User GetCurrentUser(){
        List<String> tempGroups = new ArrayList<>();
        for(String s: groups){tempGroups.add(s);}
        User temp = new User(userId, name, tempGroups, longitude, latitude);
        return  temp;
    }
    private void updateAllListeners(@NonNull User oldUser){
        for(UserReferenceUpdate listener : listeners){
            if(listener != null) {
                listener.UserValuesUpdated(oldUser, this);
            }
        }
    }

    private void printUser(){
        Log.e(log, toString());
    }

    public String getUserId() {
        return userId;
    }

    public boolean isUpdateErrorOccurred() {
        return updateErrorOccurred;
    }

    public boolean isUpdatedOnce() {
        return updatedOnce;
    }
}
