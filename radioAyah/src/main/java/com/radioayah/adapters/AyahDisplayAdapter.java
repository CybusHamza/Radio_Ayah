package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.radioayah.MainActivity;
import com.cybus.radioayah.R;
import com.radioayah.data.Comments;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.CircularImageView;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noor Ahmed on 10/1/2015.
 */
public class AyahDisplayAdapter extends ArrayAdapter<String> {
        public Context context;
        ArrayList<String> rec;
        FragmentManager mng;

        public AyahDisplayAdapter(Context context, int textViewResourceId,
                                  ArrayList<String> records) {
            super(context, textViewResourceId);
            rec = new ArrayList<String>();
            rec = records;
            this.context = context;
        }

        @Override
        public int getCount() {
            return rec.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View row = inflater.inflate(R.layout.schedule_play_row, parent,
                    false);
            if(row != null)
            {
                String current_ayah = rec.get(position);
                TextView tv = (TextView) row.findViewById(R.id.ayah_schedule_dialog);
                tv.setText(current_ayah);
                tv.setLineSpacing(1, 1.2f);
            }
            return row;
        }
    }

