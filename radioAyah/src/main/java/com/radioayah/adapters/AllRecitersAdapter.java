package com.radioayah.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cybus.radioayah.R;
import com.radioayah.MainActivity;
import com.radioayah.QariDetailFragment;
import com.radioayah.data.Reciters;
import com.radioayah.util.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllRecitersAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    String[] result;
    Context context;
    int[] imageId;
    ArrayList<Reciters> records;
    FragmentManager mng;

    public AllRecitersAdapter(Context mainActivity, ArrayList<Reciters> rec,
                              FragmentManager m) {
        // TODO Auto-generated constructor stub
        context = mainActivity;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        records = rec;
        mng = m;
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
        rowView = inflater.inflate(R.layout.reciters_row, null);
        TextView tv = (TextView) rowView.findViewById(R.id.grid_reciter_name);
        CircularImageView img = (CircularImageView) rowView
                .findViewById(R.id.reciter_image);
        String url = MainActivity.currentSession.admin_base_url
                + records.get(position).getImage();
        Picasso.with(context).load(url).placeholder(R.drawable.silhouttee)
                .into(img);
        tv.setText(records.get(position).getFname() + " "
                + records.get(position).getLname());
        tv = (TextView) rowView.findViewById(R.id.grid_reciter_country);
        tv.setText(records.get(position).getPrintable_name());
        // img.setImageResource(R.drawable.qari_pic);

        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new QariDetailFragment();
                Bundle b = new Bundle();
                b.putString("qari_id", records.get(position).getId());
                b.putString("bio", records.get(position).getBio());
                b.putString("name", records.get(position).getFname() + " "
                        + records.get(position).getLname());

                f.setArguments(b);

                mng.beginTransaction().replace(R.id.content_frame, f)
                        .addToBackStack(null).commit();
            }
        });

        return rowView;
    }
}
