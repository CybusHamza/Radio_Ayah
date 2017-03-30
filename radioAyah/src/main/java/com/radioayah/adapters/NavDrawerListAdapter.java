package com.radioayah.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.radioayah.MainActivity;
import com.cybus.radioayah.R;
import com.radioayah.util.CircularImageView;
import com.squareup.picasso.Picasso;

public class NavDrawerListAdapter extends ArrayAdapter<String> {
    public Context context;
    public String[] records = null;
    Typeface face;

    public NavDrawerListAdapter(Context context, int textViewResourceId,
                                String[] records) {
        super(context, textViewResourceId, records);
        this.records = records;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return (records == null) ? 0 : records.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if (position == 0) {
            row = inflater.inflate(R.layout.nav_row_first, parent, false);

        } else {
            row = inflater.inflate(R.layout.drawer_list_item, parent, false);
        }

        if (position == 0) {
            if (MainActivity.currentSession.isGoogleLogin == true) {
                CircularImageView c = (CircularImageView) row
                        .findViewById(R.id.nav_item_imageView);
                String url = MainActivity.currentSession.data.getUpload_picture();
                if(url == null || url.equals(" "))
                {
                    Picasso.with(context).load(R.drawable.silhouttee).into(c);
                }
                else {
                    Picasso.with(context).load(url)
                            .placeholder(R.drawable.silhouttee).into(c);
                }
                TextView textView = (TextView) row
                        .findViewById(R.id.username_in_nav);
                textView.setText(MainActivity.currentSession.data
                        .getFirst_name()
                        + " "
                        + MainActivity.currentSession.data.getLast_name());
                MainActivity.currentSession.isFacebookLogin = false;
                return row;
            } else if (MainActivity.currentSession.isFacebookLogin) {
                CircularImageView c = (CircularImageView) row
                        .findViewById(R.id.nav_item_imageView);
                String fb_url = MainActivity.currentSession.data
                        .getUpload_picture();
                if(fb_url == null || fb_url.isEmpty())
                {
                    Picasso.with(context).load(R.drawable.silhouttee).into(c);
                }
                else {
                    Picasso.with(context).load(fb_url)
                            .placeholder(R.drawable.silhouttee).into(c);
                }
                TextView textView = (TextView) row
                        .findViewById(R.id.username_in_nav);
                textView.setText(MainActivity.currentSession.data
                        .getFirst_name()
                        + " "
                        + MainActivity.currentSession.data.getLast_name());
                MainActivity.currentSession.isFacebookLogin = true;
                return row;
            } else {
                CircularImageView c = (CircularImageView) row
                        .findViewById(R.id.nav_item_imageView);
                String url = MainActivity.currentSession.admin_base_url
                        + MainActivity.currentSession.data.getUpload_picture();

                if(url == null || url.isEmpty())
                {
                    Picasso.with(context).load(R.drawable.silhouttee).into(c);
                }
                else {
                    Picasso.with(context).load(url)
                            .placeholder(R.drawable.silhouttee).into(c);
                }
                TextView textView = (TextView) row
                        .findViewById(R.id.username_in_nav);
                textView.setText(MainActivity.currentSession.data
                        .getFirst_name()
                        + " "
                        + MainActivity.currentSession.data.getLast_name());
                MainActivity.currentSession.isFacebookLogin = false;
                return row;
            }
        }

        String o = records[position];
        if (o != null && position != 0) {
            TextView tt = (TextView) row.findViewById(R.id.textView1111);
            tt.setText(o);

            ImageView img = (ImageView) row.findViewById(R.id.imageView111);
            switch (position) {
                case 1:
                    img.setImageResource(R.drawable.icon2);
                    break;
                case 2:
                    img.setImageResource(R.drawable.icon3);
                    break;
                case 3:
                    img.setImageResource(R.drawable.icon7);
                    break;
                case 4:
                    img.setImageResource(R.drawable.icon6);
                    break;
                case 5:
                    img.setImageResource(R.drawable.icon5);
                    break;
                case 6:
                    img.setImageResource(R.drawable.icon4);
                    break;
                case 7:
                    img.setImageResource(R.drawable.icon1);
                    break;
                case 8:
                    img.setImageResource(R.drawable.live_radio);
                    break;
                case 9:
                    img.setImageResource(R.drawable.download_icon);
                    break;
                case 10:
                    img.setImageResource(R.drawable.change);
                    break;
                case 11:
                    img.setImageResource(R.drawable.logout);
                    break;
            }
        }
        return row;
    }

    @Override
    public String getItem(int position) {
        return records[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}