package com.radioayah;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.cybus.radioayah.R;
import com.radioayah.data.Parah;
import com.radioayah.data.Reciters;
import com.radioayah.data.Surah;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.DB;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdvancedSearch extends Activity {
    final Dialog dialog;
    ArrayList<Parah> parah_data;
    ArrayList<Surah> surah_data;
    String[] parah_str;
    String[] surah_str;
    ArrayList<Reciters> reciters;
    String qari_id, searchType, surah_id, juz_id, ayah_to_id, ayah_from_id = "";
    private String[] qaris;

    public AdvancedSearch(final Context context, final FragmentManager mng) {
        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.advanced_search_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        final DB db = new DB(context);
        try {
            ASyncRequest obj = new ASyncRequest(context, "getQarisForUpload");
            List<BasicNameValuePair> params1 = new ArrayList<BasicNameValuePair>();
            String response1 = obj.execute(params1).get();
            reciters = new ArrayList<Reciters>();
            if (response1.contains("Exception")) {

            } else {
                if (StringValidator.isJSONValid(response1)) {
                    JSONArray arr = new JSONArray(response1);
                    qaris = new String[arr.length() + 1];
                    qaris[0] = "Select Qari";
                    reciters.add(new Reciters());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o1 = arr.getJSONObject(i);
                        Reciters r = new Reciters();
                        r.setFname(o1.getString("fname"));
                        r.setId(o1.getString("id"));
                        r.setLname(o1.getString("lname"));
                        qaris[i + 1] = o1.getString("fname") + " "
                                + o1.getString("lname");
                        reciters.add(r);
                    }
                } else {
                    qaris = new String[1];
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
        final Spinner sp1 = (Spinner) dialog
                .findViewById(R.id.qari_name_search);
        ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, qaris);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(a);
        sp1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected = sp1.getSelectedItemPosition();
                qari_id = reciters.get(selected).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                qari_id = "";
            }
        });
        final Spinner sp = (Spinner) dialog
                .findViewById(R.id.select_surah_spinner);
        sp.setEnabled(false);
        final RadioButton surah_rb = (RadioButton) dialog
                .findViewById(R.id.rg_surah);
        final RadioButton ayah_rb = (RadioButton) dialog
                .findViewById(R.id.rg_quran);
        final RadioButton juz_rb = (RadioButton) dialog
                .findViewById(R.id.rg_juz);
        final Spinner sp_to = (Spinner) dialog
                .findViewById(R.id.select_to_spinner);
        final Spinner sp_from = (Spinner) dialog
                .findViewById(R.id.select_from_spinner);
        RadioGroup rg = (RadioGroup) dialog
                .findViewById(R.id.ls_select_surah_group);
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                sp.setEnabled(true);
                if (checkedId == R.id.rg_juz) {
                    surah_rb.setChecked(false);
                    ayah_rb.setChecked(false);
                    juz_rb.setChecked(true);
                    sp_to.setVisibility(View.GONE);
                    sp_from.setVisibility(View.GONE);
                    sp.setVisibility(View.VISIBLE);
                    if (parah_data == null || parah_data.size() == 0) {
                        parah_data = db.getAllParahDB("");
                    }
                    parah_str = new String[parah_data.size()];
                    for (int i = 0; i < parah_data.size(); i++) {
                        parah_str[i] = parah_data.get(i).name;
                    }
                    ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_item, parah_str);
                    a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(a);
                    sp.setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position, long id) {
                            juz_id = parah_data.get(sp.getSelectedItemPosition()).id;
                            searchType = "4";
                            surah_id = "";
                            ayah_to_id = "";
                            ayah_from_id = "";
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub
                            juz_id = "";
                        }
                    });
                } else if (checkedId == R.id.rg_surah) {
                    surah_rb.setChecked(true);
                    ayah_rb.setChecked(false);
                    juz_rb.setChecked(false);
                    sp_to.setVisibility(View.GONE);
                    sp_from.setVisibility(View.GONE);
                    sp.setVisibility(View.VISIBLE);
                    if (surah_data == null || surah_data.size() == 0) {
                        surah_data = db.getAllSurahDB("");
                    }
                    surah_str = new String[surah_data.size()];
                    for (int i = 0; i < surah_data.size(); i++) {
                        surah_str[i] = surah_data.get(i).Name;
                    }
                    ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_item, surah_str);
                    a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(a);
                    sp.setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position, long id) {
                            surah_id = surah_data.get(sp.getSelectedItemPosition()).id;
                            searchType = "1";
                            juz_id = "";
                            ayah_to_id = "";
                            ayah_from_id = "";
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub
                            surah_id = "";
                        }
                    });
                } else if (checkedId == R.id.rg_quran) {
                    surah_rb.setChecked(false);
                    ayah_rb.setChecked(true);
                    juz_rb.setChecked(false);
                    sp.setVisibility(View.VISIBLE);
                    if (surah_data == null || surah_data.size() == 0) {
                        surah_data = db.getAllSurahDB("");
                    }
                    surah_str = new String[surah_data.size()];
                    for (int i = 0; i < surah_data.size(); i++) {
                        surah_str[i] = surah_data.get(i).Name;
                    }
                    ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_item, surah_str);
                    a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(a);
                    searchType = "2";
                    sp.setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position1, long id) {
                            // TODO Auto-generated method stub
                            surah_id = surah_data.get(sp.getSelectedItemPosition()).id;
                            String[] ayah_str;
                            searchType = "2";
                            final ArrayList<Surah> ayah_data;
                            int p = sp.getSelectedItemPosition();
                            String surahid = surah_data.get(p).id;
                            List<BasicNameValuePair> params1 = new ArrayList<BasicNameValuePair>();
                            ASyncRequest o1 = new ASyncRequest(context,
                                    "getAyahh/" + surahid);
                            String response;
                            try {
                                response = o1.execute(params1).get();
                                if (StringValidator.isJSONValid(response)) {
                                    JSONObject a = new JSONObject(response);
                                    int totalayah = Integer.parseInt(a
                                            .getString("total_ayah")) + 1;

                                    ayah_str = new String[totalayah];
                                    ayah_data = new ArrayList<Surah>();
                                    ayah_str[0] = "Select Verse";
                                    Surah s = new Surah();
                                    s.id = "-1";
                                    s.Name = "Select";
                                    ayah_data.add(s);

                                    for (int i = 1; i < totalayah; i++) {
                                        String label = "Verse "
                                                + new StringBuilder().append(i)
                                                .toString();
                                        s = new Surah();
                                        s.id = new StringBuilder().append(i)
                                                .toString();
                                        s.Name = label;
                                        ayah_str[i] = s.Name;
                                        ayah_data.add(s);
                                    }

                                    ArrayAdapter<String> a1 = new ArrayAdapter<String>(
                                            context,
                                            android.R.layout.simple_spinner_item,
                                            ayah_str);
                                    a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp_from.setAdapter(a1);
                                    sp_to.setAdapter(a1);
                                    sp_to.setOnItemSelectedListener(new OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(
                                                AdapterView<?> parent,
                                                View view, int position, long id) {
                                            ayah_to_id = ayah_data.get(sp_to.getSelectedItemPosition()).id;
                                        }

                                        @Override
                                        public void onNothingSelected(
                                                AdapterView<?> parent) {
                                            // TODO Auto-generated method
                                            // stub

                                        }
                                    });
                                    sp_from.setOnItemSelectedListener(new OnItemSelectedListener() {

                                        @Override
                                        public void onItemSelected(
                                                AdapterView<?> parent,
                                                View view, int position, long id) {
                                            int pos = sp_from.getSelectedItemPosition();
                                            ayah_from_id = ayah_data.get(pos).id;
                                        }

                                        @Override
                                        public void onNothingSelected(
                                                AdapterView<?> parent) {
                                            // TODO Auto-generated method
                                            // stub

                                        }
                                    });
                                    sp_to.setVisibility(View.VISIBLE);
                                    sp_from.setVisibility(View.VISIBLE);
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

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub
                            surah_id = "";
                        }
                    });
                } else if (R.id.rg_quran1 == checkedId) {
                    sp_to.setVisibility(View.GONE);
                    sp_from.setVisibility(View.GONE);
                    sp.setVisibility(View.GONE);
                    searchType = "3";
                    surah_id = "";
                    juz_id = "";
                    ayah_to_id = "";
                    ayah_from_id = "";
                }
            }
        });
        Button b = (Button) dialog.findViewById(R.id.add_new_task);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();

                Fragment f = new ExploreFragment();
                Bundle  params = new Bundle();
                params.putString("sType", searchType);
                params.putString("sSurah", surah_id);
                params.putString("sQari", qari_id);
                params.putString("sJuz", juz_id);
                params.putString("sSurah", surah_id);
                params.putString("sAyahSurah", surah_id);
                params.putString("sAyah_from", ayah_from_id);
                params.putString("sAyah_to", ayah_to_id);
                params.putString("search" , "adv");
                f.setArguments(params);
                mng.beginTransaction().replace(R.id.content_frame, f)
                        .addToBackStack(null).commit();
            }
        });
        dialog.show();
    }

    public void onCreateDialog(Bundle savedInstanceState) {
    }
}

// quran  = 3 , juz = 4, surah = 1 , verse = 2