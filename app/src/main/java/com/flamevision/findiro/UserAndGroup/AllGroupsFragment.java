package com.flamevision.findiro.UserAndGroup;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flamevision.findiro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class AllGroupsFragment extends Fragment implements SelectGroupFragment.GroupReceiver, SelectUserFragment.UserReceiver {

    private DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
    private List<Group> groups;

    public AllGroupsFragment() {
        groups = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_groups, container, false);

        fillGroupListAndShow();

        return view;
    }

    private void fillGroupListAndShow(){
        if(groupsRef != null){
            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot groupSnapShot: dataSnapshot.getChildren()) {
                        Group group = new GroupReference(groupSnapShot.getKey(), null);
                        groups.add(group);
                    }
                    showSelectGroupFragment(groups);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //nothing...
                }
            });
        }
    }
    private void showSelectGroupFragment(List<Group> groupList){
        if(groupList != null && getActivity() != null){
            //Add or replace fragment in container
            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragTrans = fragManager.beginTransaction();
            SelectGroupFragment fragment = new SelectGroupFragment(this, groups);
            fragTrans.replace(R.id.allGroupsLayout, fragment, "MyTag");
            fragTrans.commit();
        }
    }

    @Override
    public void GroupSelected(Group group) {
        if(getActivity() != null){
            //Add or replace fragment in container
            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragTrans = fragManager.beginTransaction();
            ShowGroupFragment fragment = new ShowGroupFragment(this, group);
            fragTrans.replace(R.id.allGroupsLayout, fragment, "MyTag");
            fragTrans.commit();
        }
    }

    @Override
    public void UserSelected(User user) {
        if(getActivity() != null){
            //Add or replace fragment in container
            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragTrans = fragManager.beginTransaction();
            ShowUserFragment fragment = new ShowUserFragment(user);
            fragTrans.replace(R.id.allGroupsLayout, fragment, "MyTag");
            fragTrans.commit();
        }
    }
}