package com.flamevision.findiro.UserAndGroup;

import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserReference extends User {

    public interface UserReferenceUpdate{
        void UserValuesUpdated(@NonNull User oldUser, @NonNull UserReference newUser);
    }
    private List<UserReferenceUpdate> listeners = new ArrayList<>();

    private boolean updatedOnce;
    private boolean updateErrorOccurred;
    private boolean autoDownloadPicture;

    private DatabaseReference userRef;

    private static  final  String log = "UserReference";
    private  static  boolean printUpdate = true;

    public UserReference(@NonNull String userId, UserReferenceUpdate listener, boolean autoDownloadPicture)
    {
        super();
        this.userId = userId;
        this.updatedOnce = false;
        this.updateErrorOccurred = false;
        this.autoDownloadPicture = autoDownloadPicture;
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
        else{name = null;}

        groupIds = new ArrayList<>();
        for(DataSnapshot groupIdSnapShot : dataSnapshot.child("groups").getChildren()){
            Object oGroupId = groupIdSnapShot.getValue();
            if(oGroupId != null){
                groupIds.add(oGroupId.toString());
            }
        }

        Object oLong = dataSnapshot.child("location").child("long").getValue();
        if(oLong != null){
            longitude = (double)oLong;
        }
        else {longitude = null;}

        Object oLat = dataSnapshot.child("location").child("lat").getValue();
        if(oLat != null){
            latitude = (double)oLat;
        }
        else{latitude = null;}

        Object oOnline = dataSnapshot.child("online").getValue();
        if(oOnline != null){
            online = (Boolean)oOnline;
        }
        else{online = null;}

        Object oPicture = dataSnapshot.child("picture").getValue();
        if(oPicture != null){
            picturePath = oPicture.toString();
        }
        else{
            picturePath = null;
            picture = null;
        }

        if(printUpdate){printUser();}
        updateAllListeners(oldUser);

        //if picturePath updated, download picture
        if(autoDownloadPicture && oldUser.picturePath != this.picturePath && this.picturePath != null) {
            updatePicture();
        }
    }
    private void updatePicture(){
        if(picturePath != null) {
            final User oldUserBeforePic = GetCurrentUser();
            try {
                final File file = File.createTempFile("userProfilePicture", "jpg");
                StorageReference picRef = FirebaseStorage.getInstance().getReference(picturePath);
                picRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        picture = BitmapFactory.decodeFile(file.getAbsolutePath());
                        file.delete();
                        if (printUpdate) {
                            printUser();
                        }
                        updateAllListeners(oldUserBeforePic);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        file.delete();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void SetAutoDownloadPicture(boolean enabled){
        autoDownloadPicture = enabled;
        if(enabled){
           updatePicture();
        }
    }

    public User GetCurrentUser(){
        List<String> tempGroups = new ArrayList<>();
        for(String s: groupIds){tempGroups.add(s);}
        User temp = new User(userId, name, tempGroups, longitude, latitude, picturePath, picture, online);
        return temp;
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
