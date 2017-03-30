package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.radioayah.InviteUserAddTask;
import com.radioayah.InviteUsersFragment;
import com.cybus.radioayah.R;
import com.radioayah.data.Project;
import com.radioayah.data.UserData;
import com.radioayah.util.ASyncRequest;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InviteUsersNameAdapter extends ArrayAdapter<UserData> {
    public Context context;
    ArrayList<UserData> rec;
    boolean showOptions = true;
    FragmentManager mng;
    boolean showdel = false;
    String proj_id = "";
    HashMap<UserData, List<Project>> data;

    public InviteUsersNameAdapter(Context context, int textViewResourceId,
                                  ArrayList<UserData> records, FragmentManager m, String project_id,
                                  HashMap<UserData, List<Project>> data) {
        super(context, textViewResourceId);
        rec = new ArrayList<UserData>();
        rec = records;
        this.context = context;
        mng = m;
        proj_id = project_id;
        this.data = data;
    }

    @Override
    public int getCount() {
        return rec.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.user_row, parent, false);
        final UserData user_data = rec.get(position);
        TextView lblListHeader = (TextView) row
                .findViewById(R.id.user_name_here);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(user_data.getFirst_name());
        ImageView img = (ImageView) row.findViewById(R.id.add_task);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Project a = new Project();
                a.setProjectid(proj_id);
                a.setUserid(user_data.getEmail());
                List tmp = data.get(user_data);
                if (tmp != null && tmp.size() < 0) {
                    String taskid = new StringBuilder().append(
                            data.get(user_data).size() + 1).toString();
                    a.setTaskid(taskid);
                    tmp.add(a);
                    data.put(user_data, tmp);
                }
                Fragment f = new InviteUserAddTask(user_data, data);
                mng.beginTransaction().replace(R.id.content_frame, f)
                        .addToBackStack(null).commit();
            }
        });
        img = (ImageView) row.findViewById(R.id.remove_task);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want delete this user from project.")
                        .setTitle("Alert");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                    ASyncRequest o = new ASyncRequest(context,
                                            "removeUser/"
                                                    + user_data.getEmail());
                                    params.add(new BasicNameValuePair(
                                            "project_id",
                                            InviteUsersFragment.project_id));
                                    rec.remove(user_data);
                                    data.remove(user_data);
                                    o.execute(params).get();
                                    Toast.makeText(context,
                                            "User Deleted From Project",
                                            Toast.LENGTH_LONG).show();
                                    notifyDataSetChanged();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
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
        return row;
    }
}