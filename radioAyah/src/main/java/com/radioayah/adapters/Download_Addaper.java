package com.radioayah.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cybus.radioayah.R;
import com.radioayah.MainActivity;
import com.radioayah.data.Download;
import com.radioayah.util.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Download_Addaper extends ArrayAdapter<Download>
{

    public Context context;
    ArrayList<Download> downloads;


    public Download_Addaper(Context context,int resource, ArrayList<Download> downloads) {
        super(context,resource);
        this.context = context;
        this.downloads = downloads;
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

        CircularImageView img = (CircularImageView) row.findViewById(R.id.explore_list_circular_image);
        TextView qariname = (TextView) row.findViewById(R.id.lv_qari_name);
        TextView trackname = (TextView) row.findViewById(R.id.lv_surah_name);
        TextView likes = (TextView) row.findViewById(R.id.lv_likes_explore);
        TextView listens = (TextView) row.findViewById(R.id.lv_play_no_of_times);

        Download download =  downloads.get(position);

        qariname.setText(download.getFname()+" "+download.getLname());
        trackname.setText(download.getName());
        likes.setText(download.getLikes());
        listens.setText(download.getListens());


        String url = MainActivity.currentSession.admin_base_url
                + download.getImage();

        Picasso.with(context).load(url)
                .placeholder(R.drawable.track_icon).into(img);



        return row;

    }
}
