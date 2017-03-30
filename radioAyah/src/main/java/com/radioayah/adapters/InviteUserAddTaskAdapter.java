package com.radioayah.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.radioayah.InviteUsersFragment;
import com.cybus.radioayah.R;
import com.radioayah.data.Project;
import com.radioayah.data.UserData;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.DB;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InviteUserAddTaskAdapter extends ArrayAdapter<UserData> {
    public Context context;
    UserData user_data;
    FragmentManager mng;
    String proj_id = "";
    DB db;
    HashMap<UserData, List<Project>> data;

    public InviteUserAddTaskAdapter(Context context, int textViewResourceId,
                                    UserData user_data, FragmentManager m, String project_id,
                                    HashMap<UserData, List<Project>> data) {
        super(context, textViewResourceId);
        this.user_data = user_data;
        this.context = context;
        mng = m;
        proj_id = project_id;
        this.data = data;
        db = new DB(context);
    }

    @Override
    public int getCount() {
        return data.get(user_data).size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.user_detail_row, parent, false);
        TextView t = (TextView) row.findViewById(R.id.project_type);
        Project p = data.get(user_data).get(position);
        if (p == null || p.getType() == null) {
            return row;
        }
        if (p != null) {
            ImageView img = (ImageView) row
                    .findViewById(R.id.task_remove_button);
            img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (data.get(user_data).get(position).getTaskid()
                            .equals("1")) {
                        Toast.makeText(context,
                                "First Task Cannot be Deleted.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        ASyncRequest o = new ASyncRequest(context,
                                "removeAddedTask/");
                        params.add(new BasicNameValuePair("project_id",
                                InviteUsersFragment.project_id));
                        params.add(new BasicNameValuePair("task_no", data
                                .get(user_data).get(position).getTaskid()));
                        params.add(new BasicNameValuePair("uid", user_data
                                .getEmail()));
                        data.get(user_data).remove(position);
                        o.execute(params).get();
                        Toast.makeText(context, "Task Deleted From Project",
                                Toast.LENGTH_LONG).show();
                        notifyDataSetChanged();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        t.setText("Task Type :" + p.getType());
        if (p.getType().equals("Parah")) {
            t.setText("Task Type : Juzz");
        }
        if (p.getType().equals("Ayah")) {
            t.setText("Task Type : Verse");
        }
        t = (TextView) row.findViewById(R.id.project_from);
        t.setVisibility(View.GONE);
        t = (TextView) row.findViewById(R.id.project_to);
        t.setVisibility(View.GONE);
        t = (TextView) row.findViewById(R.id.project_surah_name);
        if (p.getType().equals("Parah")) {
            t.setText("Selected Juzz :" + p.getJuzzname());
        } else if (p.getType().equals("Surah")) {
            t.setText("Selected Surah :" + p.getSurahname());
        } else if (p.getType().equals("Ayah")) {
            t.setText("Selected Verse :" + p.getSurahname());
            t = (TextView) row.findViewById(R.id.project_from);
            t.setVisibility(View.VISIBLE);
            t.setText(p.getAyahfromname());
            t = (TextView) row.findViewById(R.id.project_to);
            t.setVisibility(View.VISIBLE);
            t.setText(p.getAyahtoname());
        }
        return row;
    }
}