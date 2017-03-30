package com.radioayah;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.adapters.AddJuzDialogAdapter;
import com.radioayah.data.Parah;
import com.radioayah.data.Surah;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SelectJuzzDialog extends Activity {
    final Dialog dialog;

    String[] parahs_name;

    public SelectJuzzDialog(final Context context, String type) {
        dialog = new Dialog(context);
        // TODO Auto-generated constructor stub
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle(title);
        dialog.setContentView(R.layout.select_juz_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        if (type.equals("parah")) {
            ArrayList<Parah> parahs = new ArrayList<Parah>();
            ASyncRequest obj = new ASyncRequest(context, "getParah");
            try {
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                String res = obj.execute(params).get();
                if (StringValidator.isJSONValid(res)) {

                    JSONArray arr = new JSONArray(res);
                    parahs_name = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        Parah r = new Parah();
                        r.id = o.getString("id");
                        r.name = o.getString("parah_name");
                        parahs_name[i] = r.name;
                        parahs.add(r);
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ListView lv = (ListView) dialog.findViewById(R.id.lv_add_juzz);
            AddJuzDialogAdapter adp = new AddJuzDialogAdapter(context, parahs,
                    null, "1");
            lv.setAdapter(adp);
        } else if (type.equals("surah") || type.equals("ayah")) {
            Button bv = (Button) dialog.findViewById(R.id.jsdfkjjfks);
            bv.setText("Select Surah");
            ArrayList<Surah> surahs = new ArrayList<Surah>();
            ASyncRequest obj = new ASyncRequest(context, "getSurah");
            try {
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                String res = obj.execute(params).get();
                if (StringValidator.isJSONValid(res)) {

                    JSONArray arr = new JSONArray(res);
                    parahs_name = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        Surah r = new Surah();
                        r.id = o.getString("id");
                        r.Name = o.getString("name");
                        parahs_name[i] = r.id + " " + r.Name;
                        surahs.add(r);
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (type.equals("surah")) {
                ListView lv = (ListView) dialog.findViewById(R.id.lv_add_juzz);
                AddJuzDialogAdapter adp = new AddJuzDialogAdapter(context,
                        null, surahs, "2");
                lv.setAdapter(adp);
            } else if (type.equals("ayah")) {

                ListView lv = (ListView) dialog.findViewById(R.id.lv_add_juzz);
                AddJuzDialogAdapter adp = new AddJuzDialogAdapter(context,
                        null, surahs, "3");
                lv.setAdapter(adp);
            }
        }
        Button b = (Button) dialog.findViewById(R.id.cancel_button);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                UploadFragment.selected = new ArrayList<String>();
                for (int i = 0; i < UploadFragment.selected.size(); i++) {
                    UploadFragment.selected.remove(i);
                }
                dialog.dismiss();
            }
        });
        b = (Button) dialog.findViewById(R.id.done_button);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                 if(UploadFragment.selected.size()==0)
                 {
                     Toast.makeText(context,"Please Select item from list", Toast.LENGTH_SHORT).show();

                 }
                else
                 {
                     dialog.dismiss();
                 }


            }
        });

        dialog.show();
    }

    public void onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    }
}
