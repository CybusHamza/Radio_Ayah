package com.radioayah;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.data.Surah;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SelectAyahDialog extends Activity {
    final Dialog dialog;

    String[] ayah_name;

    public SelectAyahDialog(final Context context, final String surah_id) {
        dialog = new Dialog(context);
        // TODO Auto-generated constructor stub
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle(title);
        dialog.setContentView(R.layout.select_surah_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final ArrayList<Surah> ayah_data = new ArrayList<Surah>();
        ASyncRequest obj = new ASyncRequest(context, "getAyahh/" + surah_id);
        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String res = obj.execute(params).get();
            if (StringValidator.isJSONValid(res)) {
                JSONObject a = new JSONObject(res);
                int totalayah = Integer.parseInt(a.getString("total_ayah")) + 1;

                ayah_name = new String[totalayah];
                ayah_name[0] = "Select Verse";
                Surah s = new Surah();
                s.id = "-1";
                s.Name = "Select";
                ayah_data.add(s);

                for (int i = 1; i < totalayah; i++) {
                    String label = "Verse "
                            + new StringBuilder().append(i).toString();
                    s = new Surah();
                    s.id = new StringBuilder().append(i).toString();
                    s.Name = label;
                    ayah_name[i] = s.Name;
                    ayah_data.add(s );

                }
                Spinner ss = (Spinner) dialog
                        .findViewById(R.id.select_from_spinner);
                ArrayAdapter<String> a1 = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, ayah_name);
                a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ss.setAdapter(a1);

                ss = (Spinner) dialog.findViewById(R.id.select_to_spinner);
                ss.setAdapter(a1);
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
        Button b = (Button) dialog.findViewById(R.id.done_button_dialog);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                UploadFragment.selected.add(surah_id);
                Spinner sp = (Spinner) dialog
                        .findViewById(R.id.select_to_spinner);
                Spinner sp1 = (Spinner) dialog.findViewById(R.id.select_from_spinner);

                int from=0,to=0;

                to =sp.getSelectedItemPosition();
                from=sp1.getSelectedItemPosition();


                if (from == 0 && to == 0)
                {
                    Toast.makeText(context, "Please select verse", Toast.LENGTH_SHORT).show();
                }
               else if(from>to)
                {
                    Toast.makeText(context, "Please select correct order of verse", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    UploadFragment.selected_ayah_from.add(ayah_data.get(sp
                            .getSelectedItemPosition()).id);

                    UploadFragment.selected_ayah_to.add(ayah_data.get(sp1
                            .getSelectedItemPosition()).id);

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
