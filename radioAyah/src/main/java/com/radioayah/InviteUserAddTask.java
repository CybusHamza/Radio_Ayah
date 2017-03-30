package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.adapters.InviteUserAddTaskAdapter;
import com.radioayah.data.Parah;
import com.radioayah.data.Project;
import com.radioayah.data.Surah;
import com.radioayah.data.UserData;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.DB;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InviteUserAddTask extends Fragment {
    Context context;
    FragmentManager mng;
    String project_id = null;
    HashMap<UserData, List<Project>> data;
    UserData user;
    ArrayList<Parah> parah_data;
    ArrayList<Surah> surah_data;
    String[] parah_str;
    String[] surah_str;
    Project temp_project;
    private DB db;
    Spinner sp_to,sp_from;

    public InviteUserAddTask(UserData userdata,
                             HashMap<UserData, List<Project>> data) {
        this.data = data;
        user = userdata;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_user_task,
                container, false);
        ((Explore) context).setActionBarTitle("Add Users Tasks");
        db = new DB(context);
        final RadioButton surah_rb1 = (RadioButton) rootView
                .findViewById(R.id.rg_surah);
        final RadioButton ayah_rb1 = (RadioButton) rootView
                .findViewById(R.id.rg_quran);
        final RadioButton juz_rb1 = (RadioButton) rootView.findViewById(R.id.rg_juz);
        sp_to = (Spinner) rootView
                .findViewById(R.id.select_to_spinner);
        sp_from  = (Spinner) rootView
                .findViewById(R.id.select_from_spinner);

        surah_rb1.setChecked(false);
        ayah_rb1.setChecked(false);
        juz_rb1.setChecked(false);

        temp_project = new Project();
        temp_project.setType("");

        final TextView tv = (TextView) rootView
                .findViewById(R.id.header_add_user_task);
        tv.setText("Add Tasks For " + user.getFirst_name() + " ");
        ListView lv = (ListView) rootView
                .findViewById(R.id.user_task_list_view);
        InviteUserAddTaskAdapter adp = null;
        adp = new InviteUserAddTaskAdapter(context, R.layout.activity_explore,
                user, mng, project_id, data);
        lv.setAdapter(adp);
        Button b = (Button) rootView.findViewById(R.id.add_new_task);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                // TODO Auto-generated method stub
                String type = temp_project.getType();
                if (type == null|| type.equals("")) {

                    Toast.makeText(context, "Please Select A task to add.",
                            Toast.LENGTH_LONG).show();

                }

                else {




                    int from=0,to=0;

                    to =sp_to.getSelectedItemPosition();
                    from=sp_from.getSelectedItemPosition();



                    if(temp_project.getAyahto() != null || temp_project.getAyahfrom()!= null)
                    {
                        if (temp_project.getAyahto().equals("Select") || temp_project.getAyahfrom().equals("Select")) {

                            Toast.makeText(getActivity(), "Please Select Verse", Toast.LENGTH_SHORT).show();

                        }
                        else if(from>to)
                        {
                            Toast.makeText(context, "Please select correct order of verse", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            List<Project> tmp_list = data.get(user);
                            tmp_list.add(temp_project);
                            data.remove(user);
                            data.put(user, tmp_list);
                            temp_project = new Project();
                            ListView lv = (ListView) rootView
                                    .findViewById(R.id.user_task_list_view);
                            InviteUserAddTaskAdapter adp = null;
                            adp = new InviteUserAddTaskAdapter(context,
                                    R.layout.activity_explore, user, mng, project_id,
                                    data);
                            lv.setAdapter(adp);

                        }
                    }
                    else {
                        List<Project> tmp_list = data.get(user);
                        tmp_list.add(temp_project);
                        data.remove(user);
                        data.put(user, tmp_list);
                        temp_project = new Project();
                        ListView lv = (ListView) rootView
                                .findViewById(R.id.user_task_list_view);
                        InviteUserAddTaskAdapter adp = null;
                        adp = new InviteUserAddTaskAdapter(context,
                                R.layout.activity_explore, user, mng, project_id,
                                data);
                        lv.setAdapter(adp);

                    }



                    }


            }
        });
        final Spinner sp = (Spinner) rootView
                .findViewById(R.id.select_surah_spinner);
        sp.setEnabled(false);
        final RadioButton surah_rb = (RadioButton) rootView
                .findViewById(R.id.rg_surah);
        final RadioButton ayah_rb = (RadioButton) rootView
                .findViewById(R.id.rg_quran);
        final RadioButton juz_rb = (RadioButton) rootView
                .findViewById(R.id.rg_juz);

        RadioGroup rg = (RadioGroup) rootView
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
                            // TODO Auto-generated method stub
                            List tmp = data.get(user);
                            String task_id = new StringBuilder().append(
                                    tmp.size() + 1).toString();
                            try {
                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                ASyncRequest o = new ASyncRequest(context,
                                        "taskType/1");
                                params.add(new BasicNameValuePair("project_id",
                                        InviteUsersFragment.project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("uid", user
                                        .getEmail()));
                                temp_project = new Project();
                                temp_project.setType("Parah");
                                temp_project.setProjectid(project_id);
                                temp_project.setTaskid(task_id);
                                temp_project.setJuzid("-1");
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
                                    "addParah/" + user.getEmail());
                            params.add(new BasicNameValuePair("project_id",
                                    InviteUsersFragment.project_id));
                            params.add(new BasicNameValuePair("task_no",
                                    task_id));
                            params.add(new BasicNameValuePair("parah",
                                    parah_data.get(position).id));
                            try {
                                temp_project = new Project();
                                temp_project.setType("Parah");
                                temp_project.setProjectid(project_id);
                                temp_project.setTaskid(task_id);
                                temp_project.setJuzid(parah_data.get(position).id);
                                temp_project.setJuzzname(parah_data
                                        .get(position).name);
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
                            // TODO Auto-generated method stub
                            List tmp = data.get(user);
                            String task_id = new StringBuilder().append(
                                    tmp.size() + 1).toString();
                            try {

                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                ASyncRequest o = new ASyncRequest(context,
                                        "taskType/2");
                                params.add(new BasicNameValuePair("project_id",
                                        InviteUsersFragment.project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("uid", user
                                        .getEmail()));
                                temp_project = new Project();
                                temp_project.setType("Surah");
                                temp_project.setProjectid(project_id);
                                temp_project.setTaskid(task_id);
                                temp_project.setSurahid("-1");
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
                                    "addSurah/" + user.getEmail());
                            params.add(new BasicNameValuePair("project_id",
                                    InviteUsersFragment.project_id));
                            params.add(new BasicNameValuePair("task_no",
                                    task_id));
                            params.add(new BasicNameValuePair("surah",
                                    surah_data.get(position).id));
                            temp_project = new Project();
                            temp_project.setType("Surah");
                            temp_project.setProjectid(project_id);
                            temp_project.setTaskid(task_id);
                            temp_project.setSurahid(surah_data.get(position).id);
                            temp_project.setSurahname(surah_data.get(position).Name);
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
                    temp_project = new Project();
                    temp_project.setType("Parah");
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
                            List tmp = data.get(user);
                            String task_id = new StringBuilder().append(
                                    tmp.size() + 1).toString();
                            try {
                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                ASyncRequest o = new ASyncRequest(context,
                                        "taskType/3");
                                params.add(new BasicNameValuePair("project_id",
                                        InviteUsersFragment.project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("uid", user
                                        .getEmail()));
                                temp_project = new Project();
                                temp_project.setType("Ayah");
                                temp_project.setProjectid(project_id);
                                temp_project.setTaskid(task_id);
                                temp_project.setSurahid("-1");
                                o.execute(params).get();

                                params = new ArrayList<BasicNameValuePair>();
                                o = new ASyncRequest(context, "addAyah/"
                                        + user.getEmail());
                                params.add(new BasicNameValuePair("project_id",
                                        InviteUsersFragment.project_id));
                                params.add(new BasicNameValuePair("task_no",
                                        task_id));
                                params.add(new BasicNameValuePair("surah_ayah",
                                        surah_data.get(position1).id));
                                temp_project.setSurahid(surah_data
                                        .get(position1).id);
                                temp_project.setSurahname(surah_data
                                        .get(position1).Name);
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
                                            List tmp = data.get(user);
                                            String task_id = new StringBuilder()
                                                    .append(tmp.size() + 1)
                                                    .toString();
                                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                            ASyncRequest o = new ASyncRequest(
                                                    context, "addAyahTo/"
                                                    + user.getEmail());
                                            params.add(new BasicNameValuePair(
                                                    "project_id",
                                                    InviteUsersFragment.project_id));
                                            params.add(new BasicNameValuePair(
                                                    "task_no", task_id));
                                            params.add(new BasicNameValuePair(
                                                    "surah_ayah_to", ayah_data
                                                    .get(position).id));
                                            temp_project.setAyahto(ayah_data
                                                    .get(position).id);
                                            temp_project.setAyahto(ayah_data
                                                    .get(position).Name);
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
                                            List tmp = data.get(user);
                                            String task_id = new StringBuilder()
                                                    .append(tmp.size() + 1)
                                                    .toString();

                                            ASyncRequest o = new ASyncRequest(
                                                    context, "addAyahFrom/"
                                                    + user.getEmail());
                                            params.add(new BasicNameValuePair(
                                                    "project_id",
                                                    InviteUsersFragment.project_id));
                                            params.add(new BasicNameValuePair(
                                                    "task_no", task_id));
                                            params.add(new BasicNameValuePair(
                                                    "surah_ayah_from",
                                                    ayah_data.get(position).id));
                                            temp_project.setAyahfrom(ayah_data
                                                    .get(position).id);
                                            temp_project.setAyahfrom(ayah_data
                                                    .get(position).Name);
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
        return rootView;
    }
}
