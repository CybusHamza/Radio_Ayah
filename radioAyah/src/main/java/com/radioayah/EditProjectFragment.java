package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.data.Project;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EditProjectFragment extends Fragment {
    Context context;
    FragmentManager mng;
    Project p = null;


    public EditProjectFragment(Project temp) {
        p = temp;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_project_fragment,
                container, false);
        ((Explore) context).setActionBarTitle("Projects");
        TextView tv = (TextView) rootView.findViewById(R.id.add_project_heading);
        tv.setText("Update Project Details");
        Button b = (Button) rootView.findViewById(R.id.createProject);
        b.setText("Update Project");
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EditText e = (EditText) rootView
                        .findViewById(R.id.et_project_name);
                String projectname = e.getText().toString();
                e = (EditText) rootView.findViewById(R.id.et_project_desc);
                String projectdesc = e.getText().toString();
                if (StringValidator.lengthValidator(context, projectname, 0,
                        20, "Project Name") && StringValidator.lengthValidator(context, projectname, 0,
                        500, "Project Description")
                        ) {
                    ASyncRequest obj = new ASyncRequest(context,
                            "updateProject/" + p.getProjectid());
                    try {
                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        params.add(new BasicNameValuePair("pname", projectname));
                        params.add(new BasicNameValuePair("description",
                                projectdesc));
                        String res = obj.execute(params).get();
                        try {
                            if (!res.equals("-1") && Integer.parseInt(res) > 0) {
                                Toast.makeText(context, "Project Updated.",
                                        Toast.LENGTH_LONG).show();
                                Fragment f = new InviteUsersFragment(res);
                                mng.popBackStack();
                                mng.beginTransaction()
                                        .replace(R.id.content_frame, f)
                                        .addToBackStack(null).commit();
                            }
                        }
                        catch (NumberFormatException e4)
                        {
                            Toast.makeText(context, "Project Cannot be Updated.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (ExecutionException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        EditText e = (EditText) rootView
                .findViewById(R.id.et_project_name);
        e.setText(p.getName());
        e = (EditText) rootView.findViewById(R.id.et_project_desc);
        e.setText(p.getDesc());
        b = (Button) rootView.findViewById(R.id.manage_projects);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new ManageProjectsFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f)
                        .addToBackStack(null).commit();
            }
        });
        return rootView;
    }
}
