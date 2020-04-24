package com.flamevision.findiro.UserAndGroup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flamevision.findiro.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowUserFragment extends Fragment {

    private User user;

    private Button btnSeeLocation;
    private Button btnAddToGroup;
    private TextView tvName;
    private ImageView ivPicture;

    public ShowUserFragment(@NonNull User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_user, container, false);

        btnSeeLocation = view.findViewById(R.id.ShowUserSeeLocationButton);
        btnAddToGroup = view.findViewById(R.id.ShowUserAddToGroupButton);
        tvName = view.findViewById(R.id.ShowUserName);
        ivPicture = view.findViewById(R.id.ShowUserPicture);

        ShowUser(this.user);

        return view;
    }

    public void ShowUser(@NonNull User user){
        this.user = user;
        tvName.setText(user.getName());
        ivPicture.setImageBitmap(user.getProfilePicture());
    }
}
