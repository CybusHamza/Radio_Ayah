package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.radioayah.AddSurahAyahDialog;
import com.radioayah.MainActivity;
import com.cybus.radioayah.R;
import com.radioayah.data.Project;
import com.radioayah.util.ASyncRequest;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ManageProjectDetailAdapter extends ArrayAdapter<Project> {
    public Context context;
    ArrayList<Project> rec;
    Project p;
    FragmentManager mng;
    String[] status = new String[]{"Not Started", "In Progress", "Completed"};
    String[] cant_edit = new String[]{"No Task Assigned"};
    String type = "";
    ArrayList<String> tasks = new ArrayList<String>();

    public ManageProjectDetailAdapter(Context context, int textViewResourceId, Project proj, FragmentManager m, String t) {
        super(context, textViewResourceId);
        p = proj;
        rec = proj.getMembers();
        this.context = context;
        mng = m;
        type = t;

    }

    @Override
    public int getCount() {
        return rec.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.manage_project_detail_row, parent,
                false);
        RadioGroup group = (RadioGroup) row
                .findViewById(R.id.ls_select_surah_group);
        TextView t = (TextView) row.findViewById(R.id.project_type);

        String heading =   t.getText().toString();

        if(rec.get(position).getTypename().equals("Ayah"))
        {
            t.setText("Verse");
        }
        else
        t.setText(rec.get(position).getTypename());
        if (rec.get(position).getType().equals("3")) {
            String type = "";// rec.get(position).getTypename();
            if (rec.get(position).getTypename().contains("No Task Assigned By Admin")) {
                type += "No Task Assigned By Admin.";

            } else {
                type += rec.get(position).getTypename() + " Verse " + rec.get(position).getAyahfrom() + " To Verse " + rec.get(position).getAyahto();
            }
            t.setText(type);
        }
        t = (TextView) row.findViewById(R.id.project_surah_name);
        t.setText(rec.get(position).getMember_name());

        if (rec.get(position).getType().equals("-1")) {

            group.setEnabled(false);
            for (int i = 0; i < group.getChildCount(); i++) {
                group.getChildAt(i).setEnabled(false);
            }
            ImageView img = (ImageView) row.findViewById(R.id.task_remove_button);
            img.setVisibility(View.GONE);
        } else {
            group.setEnabled(true);
            if (rec.get(position).getType().equals("0")
                    || rec.get(position).getTypename()
                    .contains("No Task Assigned By Admin")) {
                group.setEnabled(false);
                for (int i = 0; i < group.getChildCount(); i++) {
                    group.getChildAt(i).setEnabled(false);
                }
            } else {
                ArrayAdapter<String> a = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, status);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                group.setEnabled(false);
                for (int i = 0; i < group.getChildCount(); i++) {
                    group.getChildAt(i).setEnabled(false);
                }
                RadioButton rb_started = (RadioButton) row.findViewById(R.id.rg_start);
                RadioButton rb_inprogress = (RadioButton) row.findViewById(R.id.rg_progress);
                RadioButton rb_completed = (RadioButton) row.findViewById(R.id.rg_completed);
                if (rec.get(position).getStatus().equals("1")) {
                    rb_started.setChecked(true);
                } else if (rec.get(position).getStatus().equals("2")) {
                    rb_inprogress.setChecked(true);
                } else if (rec.get(position).getStatus().equals("3")) {
                    rb_completed.setChecked(true);
                    rb_completed.setEnabled(false);
                    rb_inprogress.setEnabled(false);
                    rb_started.setEnabled(false);
                    ImageView img = (ImageView) row.findViewById(R.id.task_remove_button);
                    img.setVisibility(View.GONE);
                } else {
                    rb_completed.setChecked(false);
                    rb_inprogress.setChecked(false);
                    rb_started.setChecked(false);
                    TextView t1 = (TextView) row.findViewById(R.id.project_type);
                    t1.setText(t1.getText().toString() + " (Rejected)");
                    ImageView img = (ImageView) row.findViewById(R.id.task_remove_button);
                    img.setVisibility(View.GONE);
                }
                if (rec.get(position).getCanchangestatus().equals("0")) {
                    group.setEnabled(false);
                    for (int i = 0; i < group.getChildCount(); i++) {
                        group.getChildAt(i).setEnabled(false);
                    }
                } else if (!rec.get(position).getStatus().equals("3")) {
                    group.setEnabled(true);
                    for (int i = 0; i < group.getChildCount(); i++) {
                        group.getChildAt(i).setEnabled(true);
                    }
                }

            }
        }

        ImageView img = (ImageView) row.findViewById(R.id.task_remove_button);
        if (rec.get(position).getCanremove().equals("1")) {
            img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (type.contains("Accept")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to reject this task?").setTitle("Alert");
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        ASyncRequest obj = new ASyncRequest(
                                                context, "rejectTask/");
                                        try {
                                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                            params.add(new BasicNameValuePair("project_id", rec.get(position).getProjectid()));
                                            params.add(new BasicNameValuePair("task_no", rec.get(position).getTaskid()));
                                            params.add(new BasicNameValuePair("uid", MainActivity.currentSession.data.getUserid()));
                                            obj.execute(params).get();
                                            rec.remove(position);
                                            Toast.makeText(context, "Task Rejected.", Toast.LENGTH_LONG).show();
                                            notifyDataSetChanged();
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch
                                            // block
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            // TODO Auto-generated catch
                                            // block
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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to remove this task?").setTitle("Alert");
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        String proj_id = rec.get(position).getProjectid();
                                        ASyncRequest obj = new ASyncRequest(context, "removeInvitedUser/");
                                        try {
                                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                            params.add(new BasicNameValuePair("project_id", proj_id));
                                            params.add(new BasicNameValuePair("task_no", rec.get(position).getTaskid()));
                                            params.add(new BasicNameValuePair("uid", rec.get(position).getUserid()));
                                            obj.execute(params).get();
                                            rec.remove(position);
                                            Toast.makeText(context, "Task Removed.", Toast.LENGTH_LONG).show();
                                            notifyDataSetChanged();
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch
                                            // block
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            // TODO Auto-generated catch
                                            // block
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
                }
            });
        } else if (rec.get(position).getCanremove().equals("-1")) {
            img.setVisibility(View.GONE);
        }
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                String t = "";
                if (checkedId == R.id.rg_start) {
                    t = "1";
                } else if (checkedId == R.id.rg_progress) {
                    t = "2";
                } else if (checkedId == R.id.rg_completed) {
                    t = "3";
                }
                ASyncRequest obj = new ASyncRequest(context, "taskStatus/" + t);
                try {
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("project_id", rec.get(position).getProjectid()));
                    params.add(new BasicNameValuePair("task_no", rec.get(position).getTaskid()));
                    params.add(new BasicNameValuePair("uid", rec.get(position).getUserid()));
                    params.add(new BasicNameValuePair("is_admin", "1"));
                    String res = obj.execute(params).get();
                    Toast.makeText(context, res + "Status Changed.", Toast.LENGTH_LONG).show();
                    if (checkedId == R.id.rg_start) {
                        rec.get(position).setStatus("1");
                    } else if (checkedId == R.id.rg_progress) {
                        rec.get(position).setStatus("2");
                    } else if (checkedId == R.id.rg_completed) {
                        rec.get(position).setStatus("3");
                    }
                    notifyDataSetChanged();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        if (rec.get(position).getTypename().contains("Add Task")) {
            img.setVisibility(View.INVISIBLE);
            group.setEnabled(true);
            for (int i = 0; i < group.getChildCount(); i++) {
                group.getChildAt(i).setEnabled(false);
            }

            t = (TextView) row.findViewById(R.id.project_type);
                t.setTextColor(Color.parseColor("#FFFFFF"));
                t.setBackgroundResource(R.drawable.login_button_green_curved);


            t.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new AddSurahAyahDialog(context,mng, rec.get(position).getProjectid(), rec.get(position).getUserid(), rec
                            .get(position).getTaskid());
                }
            });
        }
        return row;
    }
}