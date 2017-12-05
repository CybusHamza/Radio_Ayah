package com.radioayah.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybus.radioayah.R;
import com.radioayah.MainActivity;
import com.radioayah.TrackPlayFragment;
import com.radioayah.data.Download;
import com.radioayah.data.Track;
import com.radioayah.util.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Download_Addaper extends ArrayAdapter<Download>
{

    public Context context;
    ArrayList<Track> downloads;
    FragmentManager mng;

    public Download_Addaper(Context context,int resource,FragmentManager m, ArrayList<Track> downloads) {
        super(context,resource);
        this.context = context;
        this.downloads = downloads;
        mng = m;
    }


    @Override
    public int getCount() {
        return downloads.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater
                    .inflate(R.layout.explore_listview_row, parent, false);
        } else {
            row = convertView;
        }
        ImageView i = (ImageView) row.findViewById(R.id.lv_options);
        i.setVisibility(View.INVISIBLE);

        CircularImageView img = (CircularImageView) row.findViewById(R.id.explore_list_circular_image);
        TextView qariname = (TextView) row.findViewById(R.id.lv_qari_name);
        TextView trackname = (TextView) row.findViewById(R.id.lv_surah_name);
        TextView likes = (TextView) row.findViewById(R.id.lv_likes_explore);
        TextView listens = (TextView) row.findViewById(R.id.lv_play_no_of_times);
        TextView download = (TextView) row.findViewById(R.id.tb_download);

        final Track temp =  downloads.get(position);

        qariname.setText(temp.getFname()+" "+temp.getLname());
        trackname.setText(temp.getName());
        likes.setText(temp.getLikes());
        listens.setText(temp.getListens());
        download.setText(temp.getDownloads());


        String url = MainActivity.currentSession.admin_base_url
                + temp.getAimage();

        Picasso.with(context).load(url)
                .placeholder(R.drawable.track_icon).into(img);


        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // TODO Auto-generated method
                Bundle b = new Bundle();
                b.putString("admin_url", temp.getAdmin_url());
                b.putString("like", temp.getLike());
                b.putString("track_type", temp.getTrack_type());
                b.putString("listens", temp.getListens());
                b.putString("id", temp.getId());
                b.putString("name", temp.getName());
                b.putString("path", temp.getPath());
                b.putString("surah_id", temp.getSurah_id());
                b.putString("fname", temp.getFname());
                b.putString("lname", temp.getLname());
                b.putString("aimage", temp.getAimage());
                b.putString("image", temp.getImage());
                b.putString("isdownloadable", temp.getIsdownloadable());
                b.putString("likes", temp.getLikes());
                b.putString("downloads", temp.getDownloads());
                b.putString("isOffline", "true");
                Fragment f = new TrackPlayFragment();
                f.setArguments(b);
                mng.beginTransaction().replace(R.id.content_frame, f)
                        .addToBackStack(null).commit();
            }
        });

        return row;

    }
}
