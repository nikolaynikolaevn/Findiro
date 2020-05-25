package com.flamevision.findiro.UserAndGroup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.flamevision.findiro.R;

import java.util.ArrayList;
import java.util.List;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder implements GroupReference.GroupReferenceUpdate, UserReference.UserReferenceUpdate {

        private TextView tvMembers;
        private TextView tvGroupCreator;
        private TextView tvName;
        private ConstraintLayout layout;
        private Group group;
        private UserReference groupCreatorUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.selectGroupName);
            tvGroupCreator = itemView.findViewById(R.id.selectGroupCreator);
            tvMembers = itemView.findViewById(R.id.selectGroupMemberAmount);
            layout = itemView.findViewById(R.id.selectGroupLayout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(group != null){
                        Log.e(log, "Group has been selected: " + group.name);
                        if(groupReceiver != null){
                            groupReceiver.GroupSelected(group);
                        }
                    }
                }
            });
        }

        public void setGroup(Group group){
            if(group != null){
                Log.e(log, "Group has been set: " + group.name);
                this.group = group;
                tvName.setText("Loading name...");
                tvMembers.setText("Loading members...");
                tvGroupCreator.setText("Loading group creator...");
                if(group.groupCreator != null){
                    groupCreatorUser = new UserReference(group.groupCreator, this, false);
                }
                if(group instanceof GroupReference){
                    ((GroupReference) group).AddListener(this);
                }
                showGroup();
            }
        }
        private void unsetGroup(){
            if(group != null){
                if(group instanceof GroupReference){
                    ((GroupReference) group).RemoveListener(this);
                }
            }
            if(groupCreatorUser != null){
                groupCreatorUser.RemoveListener(this);
                tvGroupCreator.setText("");
            }
            group = null;
            groupCreatorUser = null;
        }

        private void showGroup(){
            tvName.setText(group.name);
            tvMembers.setText("Members: " + group.members.size());
        }

        @Override
        public void GroupValuesUpdated(@NonNull Group oldGroup, @NonNull GroupReference newGroup) {
            if(group.groupCreator != null && groupCreatorUser == null){
                groupCreatorUser = new UserReference(group.groupCreator, this, false);
            }
            showGroup();
        }

        @Override
        public void UserValuesUpdated(@NonNull User oldUser, @NonNull UserReference newUser) {
            //group creator user update
            tvGroupCreator.setText(newUser.name);
        }
    }

    private static final String log = "UserRecyclerAdapter";

    private List<Group> groups = new ArrayList<>();
    private SelectGroupFragment.GroupReceiver groupReceiver;
    private Context context;

    public GroupRecyclerAdapter(@NonNull Context context, @NonNull List<Group> groups, SelectGroupFragment.GroupReceiver groupReceiver){
        this.groups = groups;
        this.context = context;
        this.groupReceiver = groupReceiver;
        Log.e(log, "RecyclerAdapter created\n" + toString());
    }

    @NonNull
    @Override
    public String toString() {
        String holder = "groups:";
        for(Group group: groups){
            holder += "\n" + group.name;
        }
        return  holder;
    }

    @NonNull
    @Override
    public GroupRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_select_group, parent, false);
        return new GroupRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupRecyclerAdapter.ViewHolder holder, int position) {
        holder.unsetGroup();
        holder.setGroup(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

}
