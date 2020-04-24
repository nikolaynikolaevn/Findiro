package com.flamevision.findiro.UserAndGroups;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    private ListView listView;

    public SelectGroupFragment(@NonNull GroupReceiver groupReceiver, @NonNull List<Group> groups) {
        this.groupReceiver = groupReceiver;
        this.groups = groups;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_group, container, false);

        listView = view.findViewById(R.id.SelectGroupListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if(item != null){
                    if(item instanceof User){
                        Group group = (Group) item;
                        groupReceiver.GroupSelected(group);
                    }
                }
            }
        });

        listView.setAdapter(new CustomGroupListAdapter(getContext(), groups));

        return view;
    }
}
