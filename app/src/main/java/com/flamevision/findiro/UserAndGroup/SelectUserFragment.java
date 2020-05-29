package com.flamevision.findiro.UserAndGroup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flamevision.findiro.R;

import java.util.List;

public class SelectUserFragment extends Fragment {

    public  interface UserReceiver{
        void UserSelected(User user);
    }

    private UserReceiver userReceiver;
    private List<User> users;

    private RecyclerView recyclerView;

    public SelectUserFragment(UserReceiver userReceiver, @NonNull List<User> users) {
        this.userReceiver = userReceiver;
        this.users = users;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_user, container, false);

        recyclerView = view.findViewById(R.id.selectUserRecyclerView);
        UserRecyclerAdapter userRecyclerAdapter = new UserRecyclerAdapter(getContext(), users, userReceiver);
        recyclerView.setAdapter(userRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        return  view;
    }
}
