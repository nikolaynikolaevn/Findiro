package com.flamevision.findiro.UserAndGroup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    private ListView listView;

    public SelectUserFragment(UserReceiver userReceiver, @NonNull List<User> users) {
        this.userReceiver = userReceiver;
        this.users = users;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_user, container, false);

        listView = view.findViewById(R.id.SelectUserListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                Log.d("SELECT USER FRAGMENT", "ITEM CLICKED");
                if(item != null){
                    if(item instanceof User){
                        User user = (User)item;
                        Log.d("SELECT USER FRAGMENT", "USER " + user.getName() + " HAS BEEN SELECTED");
                        if(userReceiver != null) {
                            userReceiver.UserSelected(user);
                        }
                    }
                }
            }
        });

        listView.setAdapter(new CustomUserListAdapter(getContext(), this.users));

        return  view;
    }
}
