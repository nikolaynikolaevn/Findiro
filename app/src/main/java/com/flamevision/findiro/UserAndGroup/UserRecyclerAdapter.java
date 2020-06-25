package com.flamevision.findiro.UserAndGroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.flamevision.findiro.R;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder implements UserReference.UserReferenceUpdate {

        private ImageView ivPicture;
        private TextView tvName;
        private ConstraintLayout layout;
        private TextView tvOnline;
        private User user;

        private Bitmap curPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.selectUserItemName);
            ivPicture = itemView.findViewById(R.id.selectUserItemPicture);
            tvOnline = itemView.findViewById(R.id.selectUserItemOnline);
            layout = itemView.findViewById(R.id.selectUserLayout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user != null){
                        //Log.e(log, "User has been selected: " + user.name);
                        if(userReceiver != null){
                            userReceiver.UserSelected(user);
                        }
                    }
                }
            });
        }

        public void setUser(User user){
            if(user != null){
                //Log.e(log, "User has been set: " + user.name);
                this.user = user;
                if(user instanceof UserReference){
                    ((UserReference) user).AddListener(this);
                }
                showUser();
            }
        }

        public void unsetUser(){
            if(user != null){
                if(user instanceof UserReference){
                    ((UserReference) user).RemoveListener(this);
                }
            }
        }

        private void showUser(){
            tvName.setText(user.name);
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
            if(user.picture == null){
                Drawable defaultPic = context.getResources().getDrawable(R.drawable.ic_user);
                ivPicture.setImageDrawable(defaultPic);
            }
            else if(user.picture != curPic){
                //THIS IS NEEDED BECAUSE SCROLLING WAS VERY SLOW
                curPic = user.picture;
                int maxAllowed = 100;
                int maxSize = Math.max(curPic.getWidth(), curPic.getHeight());
                int div = maxSize/maxAllowed;
                Bitmap resized = Bitmap.createScaledBitmap(curPic, curPic.getWidth()/div, curPic.getHeight()/div, true);
                ivPicture.setImageBitmap(resized);
            }
        }

        @Override
        public void UserValuesUpdated(@NonNull User oldUser, @NonNull UserReference newUser) {
            showUser();
        }
    }

    private static final String log = "UserRecyclerAdapter";

    private List<User> users = new ArrayList<>();
    private SelectUserFragment.UserReceiver userReceiver;
    private Context context;

    public UserRecyclerAdapter(@NonNull Context context, @NonNull List<User> users, SelectUserFragment.UserReceiver userReceiver){
        this.users = users;
        this.context = context;
        this.userReceiver = userReceiver;
        //Log.e(log, "RecyclerAdapter created\n" + toString());
    }

    @NonNull
    @Override
    public String toString() {
        String holder = "users:";
        for(User user: users){
            holder += "\n" + user.name;
        }
        return  holder;
    }

    @NonNull
    @Override
    public UserRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_select_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerAdapter.ViewHolder holder, int position) {
        holder.unsetUser();
        holder.setUser(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
