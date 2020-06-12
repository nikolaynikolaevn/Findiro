package com.flamevision.findiro.UserAndGroup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flamevision.findiro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowGroupFragment extends Fragment implements SelectUserFragment.UserReceiver, GroupReference.GroupReferenceUpdate {

    private Group group;
    private List<User> members;
    private SelectUserFragment.UserReceiver userReceiver;

    private Button btnMultiFunc;
    private TextView tvGroupName;
    private TextView tvMemberTitle;
    private FrameLayout flMembers;

    private FirebaseUser firebaseUser;

    public ShowGroupFragment(SelectUserFragment.UserReceiver userReceiver, @NonNull Group group) {
        this.group = group;
        this.userReceiver = userReceiver;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_group, container, false);

        btnMultiFunc = view.findViewById(R.id.showGroupMultiFuncButton);
        tvGroupName = view.findViewById(R.id.showGroupName);
        tvMemberTitle = view.findViewById(R.id.showGroupMemberTitle);
        flMembers = view.findViewById(R.id.showGroupFragContainer);

        tvGroupName.setText(group.getName());
        tvMemberTitle.setText("Members in group: " + group.getMembers().size());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            if(group instanceof GroupReference){
                if(group.groupCreator != null && group.groupCreator.equals(firebaseUser.getUid())){
                    showDeleteButton();
                }
                else if (group.members.contains(firebaseUser.getUid())) {
                    //show leave option
                    showLeaveButton();
                }
                else {
                    //show join option
                    showJoinButton();
                }
            }
            else {
                btnMultiFunc.setVisibility(View.GONE);
            }
        }
        else {
            btnMultiFunc.setVisibility(View.GONE);
        }

        if(group instanceof GroupReference){
            if(((GroupReference) group).isUpdatedOnce()){
                members = new ArrayList<>();
                for (String s : group.members) {
                    UserReference userReference = new UserReference(s, null, true);
                    members.add(userReference);
                }
            }
            ((GroupReference) group).AddListener(this);
        }
        else {
            members = new ArrayList<>();
            for (String s : group.members) {
                UserReference userReference = new UserReference(s, null, true);
                members.add(userReference);
            }
        }

        //Add or replace fragment in container
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        SelectUserFragment fragment = new SelectUserFragment(this, members);
        fragTrans.add(R.id.showGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();

        return view;
    }

    private void showDeleteButton(){
        btnMultiFunc.setText("delete group");
        btnMultiFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((GroupReference) group).DeleteGroup()){
                    Toast.makeText(getContext(), "Deleting group failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showEmptyFragment(){
        tvGroupName.setText("");
        tvMemberTitle.setText("");
        btnMultiFunc.setVisibility(View.GONE);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
    private void showJoinButton(){
        btnMultiFunc.setText("join group");
        btnMultiFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update group with new user
                DatabaseReference groupRef = ((GroupReference)group).getGroupRef();
                DatabaseReference newMemberRef = groupRef.child("members").push();
                newMemberRef.setValue(firebaseUser.getUid());

                //add group to user
                DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid() + "/groups");
                DatabaseReference userNewGroupRef = userGroupsRef.push();
                userNewGroupRef.setValue(group.groupId);

                //show leave button
                showLeaveButton();
            }
        });
    }
    private void showLeaveButton(){
        btnMultiFunc.setText("leave group");
        btnMultiFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference groupRef = ((GroupReference)group).getGroupRef().child("members");

                groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot child: dataSnapshot.getChildren()){
                            if(child.getValue() != null){
                                if(firebaseUser.getUid().equals(child.getValue().toString())) {
                                    DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference("Groups/" + group.groupId + "/members/" + child.getKey());
                                    memberRef.removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference curUserGroupsRef = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid() + "/groups");
                curUserGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot child: dataSnapshot.getChildren()){
                            if(child.getValue() != null){
                                if(child.getValue().toString() == group.groupId);
                                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid() + "/groups/" + child.getKey());
                                groupRef.removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //show join button
                showJoinButton();
            }
        });
    }

    @Override
    public void UserSelected(User user) {
        if(userReceiver != null){
            userReceiver.UserSelected(user);
        }
    }

    @Override
    public void GroupValuesUpdated(@NonNull Group oldGroup, @NonNull GroupReference newGroup) {
        if(oldGroup.members.size() != newGroup.members.size()) {
            if(newGroup.members.size() == 0){
                //group has been deleted
                if(getContext() != null) {
                    Toast.makeText(getContext(), "Group has been deleted", Toast.LENGTH_SHORT).show();
                    showEmptyFragment();
                    getActivity().onBackPressed();
                }
                return;
            }
            //Add new members
            for (String s : newGroup.members) {
                boolean memberFound = false;
                for(User member : members){
                    if(member.userId.equals(s)){
                        memberFound = true;
                        break;
                    }
                }
                if(!memberFound) {
                    UserReference userReference = new UserReference(s, null, true);
                    members.add(userReference);
                }
            }
            //Remove members
            for(int i = 0; i < members.size(); i++){
                if(!newGroup.members.contains(members.get(i).userId)){
                    members.remove(i);
                    i--;
                }
            }
            if(getActivity() != null) {
                FragmentManager fragManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragTrans = fragManager.beginTransaction();
                SelectUserFragment fragment = new SelectUserFragment(this, members);
                fragTrans.replace(R.id.showGroupFragContainer, fragment, "MyTag");
                fragTrans.commitAllowingStateLoss();
            }
        }
        tvGroupName.setText(newGroup.name);
        tvMemberTitle.setText("Members in group: " + newGroup.getMembers().size());
    }
}
