package com.radioayah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.data.Playlist;
import com.radioayah.util.ASyncRequest;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddToPlaylistAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    String[] result;
    Context context;
    int[] imageId;
    String track_id = "";
    ArrayList<Playlist> records;

    public AddToPlaylistAdapter(Context mainActivity, ArrayList<Playlist> rec,
                                String id) {
        // TODO Auto-generated constructor stub
        context = mainActivity;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        records = rec;
        track_id = id;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.add_to_playlist_row, null);
        TextView tv = (TextView) rowView
                .findViewById(R.id.add_playlist_playlist_name);
        tv.setText(records.get(position).getName());
        final TextView tv_count = (TextView) rowView
                .findViewById(R.id.teack_count_playlis);
        tv_count.setText(records.get(position).getCount());
        final Button b = (Button) rowView
                .findViewById(R.id.addtoplaylistButton);
        if (!records.get(position).getIse().equals("false")) {
            b.setText("Remove");
        }

        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!records.get(position).getIse().equals("false")) {
                    ASyncRequest obj = new ASyncRequest(context, "RmPlEx");
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    try {
                        params.add(new BasicNameValuePair("track", track_id));
                        params.add(new BasicNameValuePair("playlist", records
                                .get(position).getId()));
                        String response = obj.execute(params).get();
                        if (response.contains("1")) {
                            Toast.makeText(context, "Removed.",
                                    Toast.LENGTH_LONG).show();
                            b.setText("Add To Playlist");
                            records.get(position).setIse("false");
                            int count = Integer.parseInt(tv_count.getText()
                                    .toString());
                            tv_count.setText(new StringBuilder().append(
                                    count - 1).toString());
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    ASyncRequest obj = new ASyncRequest(context, "AddPlEx");
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    try {
                        params.add(new BasicNameValuePair("track", track_id));
                        params.add(new BasicNameValuePair("playlist", records
                                .get(position).getId()));
                        String response = obj.execute(params).get();
                        if (response.contains("1")) {
                            Toast.makeText(context, "Added.", Toast.LENGTH_LONG)
                                    .show();
                            b.setText("Added");
                            records.get(position).setIse("213312");
                            int count = Integer.parseInt(tv_count.getText()
                                    .toString());
                            tv_count.setText(new StringBuilder().append(
                                    count + 1).toString());
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        return rowView;
    }
}
