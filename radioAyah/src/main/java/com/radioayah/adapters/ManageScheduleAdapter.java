package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;

import com.google.android.gms.plus.PlusShare;
import com.radioayah.EditScheduleFragment;
import com.radioayah.Explore;
import com.radioayah.ManageScheduleFragment;
import com.cybus.radioayah.R;
import com.radioayah.SchedulePlayDialog;
import com.radioayah.data.Playlist;
import com.radioayah.data.Schedule;
import com.radioayah.data.Session;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ManageScheduleAdapter extends ArrayAdapter<Playlist> {
    public Context context;
    ArrayList<Schedule> rec;
    boolean showOptions = true;
    FragmentManager mng;

    public ManageScheduleAdapter(Context context, int textViewResourceId,
                                 ArrayList<Schedule> records, boolean show, FragmentManager m) {
        super(context, textViewResourceId);
        rec = new ArrayList<Schedule>();
        rec = records;
        showOptions = show;
        this.context = context;
        mng = m;
        // this.records = records;
    }

    @Override
    public int getCount() {
        return rec.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.manage_schedule_row, parent, false);
        } else {
            row = convertView;
        }
        final Schedule temp = rec.get(position);
        if (temp != null) {
            TextView t = (TextView) row.findViewById(R.id.tv_playlist_name);
            t.setText(temp.getName());

            ImageView i = (ImageView) row.findViewById(R.id.schedule_delete_button);
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are You sure you want to delete this Schedule").setTitle("Alert");
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int id) {

                            // TODO Auto-generated method stub
                            ASyncRequest obj = new ASyncRequest(context, "deleteSchedule/");
                            try {
                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                params.add(new BasicNameValuePair("id", temp.getId()));
                                String res = obj.execute(params).get();
                                if (res.equals("1")) {
                                    Toast.makeText(context, "Schedule Deleted.", Toast.LENGTH_LONG).show();
                                    Fragment f = new ManageScheduleFragment();
                                    mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
                                }
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int id) {
                        }
                    });
                    builder.create();
                    builder.show();



                }
            });
            i = (ImageView) row.findViewById(R.id.schedule_play_button);
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Fragment f = new SchedulePlayDialog();
                    Bundle b = new Bundle();
                    b.putString("id", temp.getId());
                    f.setArguments(b);
                    mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
                }
            });
            i = (ImageView) row.findViewById(R.id.schedule_edit_button);
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Fragment f = new EditScheduleFragment();
                    Bundle b = new Bundle();
                    b.putString("id", temp.getId());
                    f.setArguments(b);
                    mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
                }
            });
            i = (ImageView) row.findViewById(R.id.schedule_share_button);
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    ASyncRequest obj = new ASyncRequest(context, "Schedule1/" + temp.getId());
                    try {
                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        String res = obj.execute(params).get();
                        if (StringValidator.isJSONValid(res)) {
                            JSONObject o = new JSONObject(res);
                            final String name = o.getString("name");
                            final String desc = o.getString("description");
                            JSONObject inner = o.getJSONObject("se");
                            inner.getString("client_time");
                            inner.getString("duration");
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Share On")
                                    .setItems(new String[]{"Facebook", "Google +"}, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                ShareLinkContent content = new ShareLinkContent.Builder().setContentTitle(name)
                                                        .setContentDescription(desc)
                                                        .setContentUrl(Uri.parse(Session.base_url_schedule + temp.getId())).build();
                                                Explore.shareDialog.show(content);
                                            } else {
                                                Intent shareIntent = new PlusShare.Builder(context).setType("text/plain")
                                                        .setText(name + "-" + desc)
                                                        .setContentUrl(Uri.parse(Session.base_url_schedule + temp.getId())).getIntent();
                                                ((Activity) context).startActivityForResult(shareIntent, 0);
                                            }
                                        }
                                    });
                            builder.show();
                        }
                    } catch (
                            InterruptedException e
                            )

                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (
                            ExecutionException e
                            ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (
                            JSONException e
                            )

                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
        return row;
    }
}