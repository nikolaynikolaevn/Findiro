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

import com.flamevision.findiro.R;
import com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowGroupFragment extends Fragment implements SelectUserFragment.UserReceiver, GroupReference.GroupReferenceUpdate {

    private Group group;
    private List<User> members;
    private SelectUserFragment.UserReceiver userReceiver;

    private Button btnAddMember;
    private TextView tvGroupName;
    private TextView tvMemberTitle;
    private FrameLayout flMembers;

    public ShowGroupFragment(SelectUserFragment.UserReceiver userReceiver, @NonNull Group group) {
        this.group = group;
        this.userReceiver = userReceiver;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_group, container, false);

        btnAddMember = view.findViewById(R.id.ShowGroupAddMemberButton);
        tvGroupName = view.findViewById(R.id.ShowGroupName);
        tvMemberTitle = view.findViewById(R.id.ShowGroupMemberTitle);
        flMembers = view.findViewById(R.id.ShowGroupFragContainer);

        tvGroupName.setText(group.getName());
        tvMemberTitle.setText("Members in group: " + group.getMembers().size());

        if(group instanceof GroupReference){
            if(((GroupReference) group).isUpdatedOnce()){
                members = new ArrayList<>();
                for (String s : group.members) {
                    UserReference userReference = new UserReference(s, null);
                    members.add(userReference);
                }
            }
            ((GroupReference) group).AddListener(this);
        }
        else {
            members = new ArrayList<>();
            for (String s : group.members) {
                UserReference userReference = new UserReference(s, null);
                members.add(userReference);
            }
        }

        //Add or replace fragment in container
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        SelectUserFragment fragment = new SelectUserFragment(this, members);
        fragTrans.add(R.id.ShowGroupFragContainer, fragment, "MyTag");
        fragTrans.commit();

        return view;
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
            members = new ArrayList<>();
            for (String s : newGroup.members) {
                UserReference userReference = new UserReference(s, null);
                members.add(userReference);
            }
        }
        tvGroupName.setText(newGroup.name);
        tvMemberTitle.setText("Members in group: " + newGroup.getMembers().size());
    }
}
