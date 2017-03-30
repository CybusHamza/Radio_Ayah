package com.radioayah;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.data.Parah;
import com.radioayah.data.Surah;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.DB;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddSurahAyahDialog extends Activity {
    public static Bundle c;
    final Dialog dialog;
    ArrayList<Parah> parah_data;
    ArrayList<Surah> surah_data;
    String[] parah_str;
    String[] surah_str;

    public AddSurahAyahDialog(final Context context, final FragmentManager mng, final String project_id,
                              final String uid, final String task_id) {
        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_add_user_task);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        final DB db = new DB(context);
        Button b = (Button) dialog.findViewById(R.id.dialog_heading);
        b.setVisibility(View.VISIBLE);
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
        surah_rb.setChecked(false);
        ayah_rb.setChecked(false);
        juz_rb.setChecked(false);
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
                            try {
                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                ASyncRequest o = new ASyncRequest(context,
                                        "taskType/1");
                                params.add(new BasicNameValuePair("project_id",
                                        project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("uid", uid));
                                o.execute(params).get();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                            ASyncRequest o = new ASyncRequest(context,
                                    "addParah/" + uid);
                            params.add(new BasicNameValuePair("project_id",
                                    project_id));
                            params.add(new BasicNameValuePair("task_no",
                                    task_id));
                            params.add(new BasicNameValuePair("parah",
                                    parah_data.get(position).id));
                            try {
                                o.execute(params).get();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub

                        }
                    });
                } else if (checkedId == R.id.rg_surah) {
                    surah_rb.setChecked(true);
                    ayah_rb.setChecked(false);
                    juz_rb.setChecked(false);
                    sp_to.setVisibility(View.GONE);
                    sp_from.setVisibility(View.GONE);
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
                            try {

                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                ASyncRequest o = new ASyncRequest(context,
                                        "taskType/2");
                                params.add(new BasicNameValuePair("project_id",
                                        project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("uid", uid));
                                o.execute(params).get();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                            ASyncRequest o = new ASyncRequest(context,
                                    "addSurah/" + uid);
                            params.add(new BasicNameValuePair("project_id",
                                    project_id));
                            params.add(new BasicNameValuePair("task_no",
                                    task_id));
                            params.add(new BasicNameValuePair("surah",
                                    surah_data.get(position).id));
                            try {
                                o.execute(params).get();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub

                        }
                    });
                } else if (checkedId == R.id.rg_quran) {
                    surah_rb.setChecked(false);
                    ayah_rb.setChecked(true);
                    juz_rb.setChecked(false);
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
                                                   View view, int position1, long id) {
                            // TODO Auto-generated method stub
                            try {
                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                ASyncRequest o = new ASyncRequest(context,
                                        "taskType/3");
                                params.add(new BasicNameValuePair("project_id",
                                        project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("uid", uid));
                                o.execute(params).get();
                                params = new ArrayList<BasicNameValuePair>();
                                o = new ASyncRequest(context, "addAyah/" + uid);
                                params.add(new BasicNameValuePair("project_id",
                                        InviteUsersFragment.project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("surah_ayah",
                                        surah_data.get(position1).id));
                                o.execute(params).get();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            String[] ayah_str;
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
                                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                            ASyncRequest o = new ASyncRequest(
                                                    context, "addAyahTo/" + uid);
                                            params.add(new BasicNameValuePair(
                                                    "project_id", project_id));
                                            params.add(new BasicNameValuePair(
                                                    "task_no", task_id));
                                            params.add(new BasicNameValuePair(
                                                    "surah_ayah_to", ayah_data
                                                    .get(position).id));
                                            try {
                                                o.execute(params).get();
                                            } catch (InterruptedException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            } catch (ExecutionException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }
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
                                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                            ASyncRequest o = new ASyncRequest(
                                                    context, "addAyahFrom/"
                                                    + uid);
                                            params.add(new BasicNameValuePair(
                                                    "project_id", project_id));
                                            params.add(new BasicNameValuePair(
                                                    "task_no", task_id));
                                            params.add(new BasicNameValuePair(
                                                    "surah_ayah_from",
                                                    ayah_data.get(position).id));
                                            try {
                                                o.execute(params).get();
                                            } catch (InterruptedException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            } catch (ExecutionException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }

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
                        }
                    });

                }
            }
        });
        b = (Button) dialog.findViewById(R.id.add_new_task);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Toast.makeText(context, "Task Updated.", Toast.LENGTH_LONG)
                        .show();
                mng.popBackStack();
            }
        });
        dialog.show();
    }

    public void onCreateDialog(Bundle savedInstanceState) {
    }
}