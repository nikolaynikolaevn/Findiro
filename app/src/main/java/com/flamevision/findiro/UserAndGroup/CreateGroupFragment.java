package com.flamevision.findiro.UserAndGroup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flamevision.findiro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGroupFragment extends Fragment {

    private EditText etName;
    private FirebaseAuth auth;
    private DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        auth = FirebaseAuth.getInstance();

        etName = view.findViewById(R.id.createGroupFragName);

        Button btnCreateGroup = view.findViewById(R.id.createGroupFragButton);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                if(name == ""){
                    etName.setError("No name has been given");
                }
                else {
                    createGroup(name);
                }
            }
        });

        return view;
    }
    private void createGroup(String name){
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            Toast.makeText(getContext(), "Failure: You need to be logged in", Toast.LENGTH_LONG).show();
        }
        else{
            //create group in db
            DatabaseReference groupRef = groupsRef.push();
            groupRef.child("name").setValue(name);
            groupRef.child("groupCreator").setValue(user.getUid());
            DatabaseReference firstMemberRef = groupRef.child("members").push();
            firstMemberRef.setValue(user.getUid());

            //add group to user
            DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReference("Users/" + user.getUid() + "/groups");
            DatabaseReference userNewGroupRef = userGroupsRef.push();
            userNewGroupRef.setValue(groupRef.getKey());

            Toast.makeText(getContext(), "Group has been created", Toast.LENGTH_LONG).show();

            getActivity().onBackPressed();
        }
    }
}