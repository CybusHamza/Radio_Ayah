package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.cybus.radioayah.R;
import com.radioayah.adapters.ManageProjectDetailAdapter;
import com.radioayah.data.Project;

public class ManageProjectDetailFragment extends Fragment {
    Context context;
    FragmentManager mng;
    Project proj;
    String type = "";

    public ManageProjectDetailFragment(Project p) {
        proj = p;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_manage_projects, container, false);
        ((Explore) getActivity()).setActionBarTitle("Manage Projects");
        type = getArguments().getString("type");
        ListView lv = (ListView) rootView.findViewById(R.id.manage_projects_list);
        ManageProjectDetailAdapter adp = new ManageProjectDetailAdapter(context, R.layout.manage_playlist_row, proj, mng, type);
        lv.setAdapter(adp);
        Button b = (Button) rootView.findViewById(R.id.add_new_project);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new AddProjectFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        b = (Button) rootView.findViewById(R.id.manage_projects);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new ManageProjectsFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        return rootView;
    }
}
