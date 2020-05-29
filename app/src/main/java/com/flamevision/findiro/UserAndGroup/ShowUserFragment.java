package com.flamevision.findiro.UserAndGroup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flamevision.findiro.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowUserFragment extends Fragment implements UserReference.UserReferenceUpdate {

    private User user;
    private Bitmap curPic;

    private TextView tvName;
    private ImageView ivPicture;
    private TextView tvOnline;

    public ShowUserFragment(@NonNull User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_user, container, false);

        tvName = view.findViewById(R.id.showUserName);
        ivPicture = view.findViewById(R.id.showUserPicture);
        tvOnline = view.findViewById(R.id.showUserOnline);

        if(user instanceof  UserReference){
            ((UserReference) user).AddListener(this);
        }

        ShowUser(this.user);

        return view;
    }

    public void ShowUser(@NonNull User user){
        this.user = user;
        tvName.setText(user.getName());
        if(user.online != null){
            if(user.online == true){
                tvOnline.setText("online");
            }
            else {
                tvOnline.setText("offline");
            }
        }
        else {
            tvOnline.setText("unknown");
        }
        if(user.picturePath == null){
            Drawable defaultPic = getResources().getDrawable(R.drawable.ic_user);
            ivPicture.setImageDrawable(defaultPic);
        }
        else if(curPic != user.picture){
            curPic = user.picture;
            ivPicture.setImageBitmap(curPic);
        }
    }

    @Override
    public void UserValuesUpdated(@NonNull User oldUser, @NonNull UserReference newUser) {
        ShowUser(newUser);
    }
}
