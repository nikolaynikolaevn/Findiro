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

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowGroupFragment extends Fragment implements SelectUserFragment.UserReceiver {

    private Group group;
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

        //Add or replace fragment in container
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();
        SelectUserFragment fragment = new SelectUserFragment(this, group.getMembers());
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
}
