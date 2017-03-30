package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.radioayah.EditProjectFragment;
import com.radioayah.MainActivity;
import com.radioayah.ManageProjectDetailFragment;
import com.radioayah.ManageProjectsFragment;
import com.cybus.radioayah.R;
import com.radioayah.data.Project;
import com.radioayah.util.ASyncRequest;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ManageProjectsAdapter extends ArrayAdapter<Project> {
    public Context context;
    ArrayList<Project> rec;
    FragmentManager mng;

    public ManageProjectsAdapter(Context context, int textViewResourceId,
                                 ArrayList<Project> records, FragmentManager m) {
        super(context, textViewResourceId);
        rec = new ArrayList<Project>();
        rec = records;
        this.context = context;
        mng = m;
    }

    @Override
    public int getCount() {
        return rec.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.manage_projects_row, parent, false);
        final Project p = rec.get(position);
        TextView tv = (TextView) row.findViewById(R.id.manage_project_name);
        tv.setText(p.getName());
        tv = (TextView) row.findViewById(R.id.manage_project_desc);
        if (p.getDesc() != null && !p.getDesc().isEmpty()) {
            tv.setText(p.getDesc());
        } else {
            tv.setText("");
        }
        row.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bundle b = new Bundle();
                b.putString("type", p.getType());
                Fragment f = new ManageProjectDetailFragment(p);
                f.setArguments(b);
                mng.beginTransaction().replace(R.id.content_frame, f)
                        .addToBackStack(null).commit();
            }
        });
        if (!p.getType().contains("Edit")) {
            ImageView img = (ImageView) row.findViewById(R.id.accept_proposal);
            img.setVisibility(View.INVISIBLE);
            img = (ImageView) row.findViewById(R.id.reject_proposal);
            img.setImageResource(R.drawable.reject);
            img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setMessage("Do you want to reject this Project?")
                            .setTitle("Alert");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    String proj_id = rec.get(position)
                                            .getProjectid();
                                    ASyncRequest obj = new ASyncRequest(
                                            context, "rejectProject/");
                                    try {
                                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                        params.add(new BasicNameValuePair(
                                                "project_id", proj_id));
                                        params.add(new BasicNameValuePair(
                                                "uid",
                                                MainActivity.currentSession.data
                                                        .getUserid()));
                                        obj.execute(params).get();
                                        Toast.makeText(context,
                                                "Project Rejected.",
                                                Toast.LENGTH_LONG).show();
                                        rec.remove(position);
                                        notifyDataSetChanged();
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            });
                    builder.show();
                }
            });
        } else {
            //edit projects code

            ImageView img = (ImageView) row.findViewById(R.id.accept_proposal);
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment f = new EditProjectFragment(rec.get(position));
                    mng.popBackStack();
                    mng.beginTransaction().replace(R.id.content_frame, f)
                            .addToBackStack(null).commit();
                }
            });
            ////
            img = (ImageView) row.findViewById(R.id.reject_proposal);
            img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setMessage("Do you want to delete this Project?")
                            .setTitle("Alert");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    String proj_id = rec.get(position)
                                            .getProjectid();
                                    ASyncRequest obj = new ASyncRequest(
                                            context, "removeProject/" + proj_id);
                                    try {
                                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                        obj.execute(params).get();
                                        Toast.makeText(context,
                                                "Project Deleted.",
                                                Toast.LENGTH_LONG).show();
                                        rec.remove(position);
                                        notifyDataSetChanged();

                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                }
                            });
                    builder.show();
                }
            });
        }
        return row;
    }
}