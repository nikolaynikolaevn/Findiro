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

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectGroupFragment extends Fragment {

    public  interface GroupReceiver{
        void GroupSelected(Group group);
    }

    private List<Group> groups;
    private  GroupReceiver groupReceiver;

    private RecyclerView recyclerView;

    public SelectGroupFragment(GroupReceiver groupReceiver, @NonNull List<Group> groups) {
        this.groupReceiver = groupReceiver;
        this.groups = groups;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_group, container, false);

        recyclerView = view.findViewById(R.id.selectGroupRecyclerView);
        GroupRecyclerAdapter groupRecyclerAdapter = new GroupRecyclerAdapter(getContext(), groups, groupReceiver);
        recyclerView.setAdapter(groupRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        /*
        listView = view.findViewById(R.id.SelectGroupListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                Log.d("SELECT GROUP FRAGMENT", "ITEM CLICKED");
                if(item != null){
                    if(item instanceof Group){
                        Group group = (Group) item;
                        Log.d("SELECT GROUP FRAGMENT", "GROUP " + group.getName() + " HAS BEEN SELECTED");
                        if(groupReceiver != null) {
                            groupReceiver.GroupSelected(group);
                        }
                    }
                }
            }
        });

        listView.setAdapter(new CustomGroupListAdapter(getContext(), groups));

         */

        return view;
    }
}
