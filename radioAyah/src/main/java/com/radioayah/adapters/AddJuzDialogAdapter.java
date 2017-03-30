package com.radioayah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


import com.cybus.radioayah.R;
import com.radioayah.SelectAyahDialog;
import com.radioayah.UploadFragment;
import com.radioayah.data.Parah;
import com.radioayah.data.Surah;

import java.util.ArrayList;

public class AddJuzDialogAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    String type;
    Context context;
    ArrayList<Parah> records;
    ArrayList<Surah> records1;

    public AddJuzDialogAdapter(Context mainActivity, ArrayList<Parah> rec,
                               ArrayList<Surah> rec1, String id) {
        // TODO Auto-generated constructor stub
        context = mainActivity;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        records1 = rec1;
        records = rec;
        type = id;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (type.equals("1")) {
            return records.size();
        } else {
            return records1.size();
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (type.equals("1")) {
            return records.get(position);
        } else {
            return records1.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.select_juz_row, null);
        CheckBox c = (CheckBox) rowView.findViewById(R.id.juz_select_checkbox);

        if (type.equals("1")) {
            c.setText(records.get(position).name);
            c.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        UploadFragment.selected.add(records.get(position).id);
                    } else {
                        UploadFragment.selected.remove(records.get(position).id);
                    }
                }
            });
        } else if (type.equals("2")) {
            c.setText(records1.get(position).id + " " +records1.get(position).Name);
            c.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        UploadFragment.selected.add(records1.get(position).id);
                    } else {
                        UploadFragment.selected.remove(records1.get(position).id);
                    }
                }
            });
        } else if (type.equals("3")) {
            if (UploadFragment.selected.contains(records1.get(position).id)) {
                c.setChecked(true);
            } else {
                c.setChecked(false);
            }
            c.setText(records1.get(position).id + " " +records1.get(position).Name);
            c.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        new SelectAyahDialog(context, records1.get(position).id);
                    } else {


                        int pos = UploadFragment.selected.indexOf(records1
                                .get(position).id);
                        UploadFragment.selected.remove(pos);

                            UploadFragment.selected_ayah_from.remove(pos);
                            UploadFragment.selected_ayah_to.remove(pos);





                    }
                }
            });
        }

        return rowView;
    }
}
