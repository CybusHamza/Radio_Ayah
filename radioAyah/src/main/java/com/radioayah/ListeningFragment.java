package com.radioayah;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.data.Parah;
import com.radioayah.data.Reciters;
import com.radioayah.data.Surah;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.MyTimePickerDialog;
import com.radioayah.util.ProgressDialog;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;


public class ListeningFragment extends Fragment{
    Context context;
    ArrayList<Surah> surahs;
    ArrayList<Parah> parahs;
    String[] surah_str, parah_str;
    ArrayList<Surah> ayah_data;
    String[] ayah_str;
    ArrayList<Reciters> reciters;
    String[] timezones;
    String[] timezone_diff;
    FragmentManager mng;
    private String[] qaris;
    public boolean first_time = true;
    EditText time_edittext = null;
    public ListeningFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();
        first_time = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_listening,
                container, false);
        ((Explore) getActivity()).setActionBarTitle("Listening Schedule");
        ProgressDialog.showDialog(context);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                try {
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    ASyncRequest o = new ASyncRequest(context, "getAllSurahs");
                    String response = o.execute(params).get();
                    if (StringValidator.isJSONValid(response)) {
                        surahs = new ArrayList<Surah>();
                        Surah t = new Surah();
                        t.id = "-1";
                        t.Name = "Select Surah";
                        surahs.add(t);
                        JSONArray json = new JSONArray(response);
                        surah_str = new String[json.length() + 1];
                        surah_str[0] = "Select Surah";
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            t = new Surah();
                            t.id = obj.getString("id");
                            t.Name = obj.getString("id") + " " +obj.getString("name");
                            surahs.add(t);
                            surah_str[i + 1] = t.Name;
                        }
                    }
                    params = new ArrayList<BasicNameValuePair>();
                    o = new ASyncRequest(context, "getTimezones");
                    response = o.execute(params).get();
                    if (StringValidator.isJSONValid(response)) {
                        JSONArray json = new JSONArray(response);
                        timezone_diff = new String[json.length()];
                        timezones = new String[json.length()];
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            timezone_diff[i] = obj.getString("difference");
                            timezones[i] = obj.getString("zone");
                        }
                    }
                    o = new ASyncRequest(context, "getAllParahs");
                    response = o.execute(params).get();
                    if (StringValidator.isJSONValid(response)) {
                        parahs = new ArrayList<Parah>();
                        Parah t = new Parah();
                        t.id = "-1";
                        t.name = "Select Juzz";
                        parahs.add(t);
                        JSONArray json = new JSONArray(response);
                        parah_str = new String[json.length() + 1];
                        parah_str[0] = "Select Juzz";
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            t = new Parah();
                            t.id = obj.getString("id");
                            t.name = obj.getString("Name");
                            parahs.add(t);
                            parah_str[i + 1] = t.name;
                        }
                    }

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
                Button b = (Button) rootView.findViewById(R.id.manage_schedules);
                b.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Fragment f = new ManageScheduleFragment();
                        mng.popBackStack();
                        mng.beginTransaction().replace(R.id.content_frame, f)
                                .addToBackStack(null).commit();
                    }
                });
                Spinner sp = (Spinner) rootView
                        .findViewById(R.id.ls_select_qari_spinner);
                ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, qaris);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(a);
                sp.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        // TODO Auto-generated method stub
                        Spinner sp1 = (Spinner) rootView
                                .findViewById(R.id.ls_select_qari_spinner);
                        Spinner sp2 = (Spinner) rootView.findViewById(R.id.select_surah_spinner);

                        int pos = sp2.getSelectedItemPosition();
                        if (sp1.getSelectedItemPosition() == 0 &&  pos > 0) {
                            Toast.makeText(context, "Please Select A Qari First.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        String qariid = reciters.get(sp1.getSelectedItemPosition())
                                .getId();
                        sp1 = (Spinner) rootView
                                .findViewById(R.id.select_surah_spinner);
                        if (sp1.getSelectedItemPosition() > 0 && surahs.size() > 1) {
                            int p = sp1.getSelectedItemPosition();
                            String surahid = surahs.get(p).id;
                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                            ASyncRequest o = new ASyncRequest(context, "getAyahForLS");
                            params.add(new BasicNameValuePair("qari", qariid));
                            params.add(new BasicNameValuePair("surah", surahid));
                            String response;
                            try {
                                response = o.execute(params).get();
                                if (response.equals("false")) {
                                    RadioGroup rg = (RadioGroup) rootView
                                            .findViewById(R.id.ls_select_surah_group);
                                    if (rg.getCheckedRadioButtonId() == R.id.rg_surah) {
                                        Toast.makeText(context,
                                                "No Verses exists for this qari",
                                                Toast.LENGTH_LONG).show();
                                    }

                                    // no data exist
                                } else if (StringValidator.isJSONValid(response)) {
                                    JSONArray a = new JSONArray(response);
                                    ayah_str = new String[a.length() + 1];
                                    ayah_data = new ArrayList<Surah>();
                                    ayah_str[0] = "Select Verse";
                                    Surah s = new Surah();
                                    s.id = "-1";
                                    s.Name = "Select";
                                    ayah_data.add(s);
                                    for (int i = 0; i < a.length(); i++) {
                                        JSONObject o1 = a.getJSONObject(i);
                                        s = new Surah();
                                        s.id = o1.getString("ayah_id");
                                        s.Name = "Verse " + o1.getString("ayah_id");
                                        ayah_str[i + 1] = "Verse "
                                                + o1.getString("ayah_id");
                                        ayah_data.add(s);
                                    }
                                    Spinner ss = (Spinner) rootView
                                            .findViewById(R.id.select_from_spinner);
                                    ArrayAdapter<String> a1 = new ArrayAdapter<String>(
                                            context,
                                            android.R.layout.simple_spinner_item,
                                            ayah_str);
                                    a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    ss.setAdapter(a1);

                                    ss = (Spinner) rootView
                                            .findViewById(R.id.select_to_spinner);
                                    ss.setAdapter(a1);
                                    RadioGroup rg = (RadioGroup) rootView
                                            .findViewById(R.id.ls_select_surah_group);
                                    if (rg.getCheckedRadioButtonId() == R.id.rg_surah) {
                                        LinearLayout l = (LinearLayout) rootView
                                                .findViewById(R.id.from_to_holder);
                                        l.setVisibility(View.VISIBLE);
                                    } else {
                                        LinearLayout l = (LinearLayout) rootView
                                                .findViewById(R.id.from_to_holder);
                                        l.setVisibility(View.GONE);
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
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }
                });
                RadioGroup rg = (RadioGroup) rootView
                        .findViewById(R.id.ls_select_surah_group);
                Spinner s = (Spinner) rootView.findViewById(R.id.select_surah_spinner);
                s.setVisibility(View.GONE);
                LinearLayout l = (LinearLayout) rootView
                        .findViewById(R.id.from_to_holder);
                l.setVisibility(View.GONE);
                rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub
                        if (checkedId == R.id.rg_quran) {
                            Spinner s = (Spinner) rootView
                                    .findViewById(R.id.select_surah_spinner);
                            s.setVisibility(View.GONE);
                            LinearLayout l = (LinearLayout) rootView
                                    .findViewById(R.id.from_to_holder);
                            l.setVisibility(View.GONE);
                        }
                        if (checkedId == R.id.rg_juz) {
                            Spinner s = (Spinner) rootView
                                    .findViewById(R.id.select_surah_spinner);
                            s.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                                    android.R.layout.simple_spinner_item, parah_str);
                            a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            s.setAdapter(a);
                            LinearLayout l = (LinearLayout) rootView
                                    .findViewById(R.id.from_to_holder);
                            l.setVisibility(View.GONE);
                        }
                        if (checkedId == R.id.rg_surah) {

                            Spinner s = (Spinner) rootView
                                    .findViewById(R.id.select_surah_spinner);
                            s.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                                    android.R.layout.simple_spinner_item, surah_str);
                            a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            s.setAdapter(a);
                            s.setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent,
                                                           View view, int position, long id) {
                                    Spinner sp1 = (Spinner) rootView
                                            .findViewById(R.id.ls_select_qari_spinner);
                                    Spinner sp2 = (Spinner) rootView
                                            .findViewById(R.id.select_surah_spinner);

                                    int pos = sp2.getSelectedItemPosition();
                                    if (sp1.getSelectedItemPosition() == 0 &&  pos > 0) {
                                        Toast.makeText(context, "Please Select A Qari First.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    String qariid = reciters.get(
                                            sp1.getSelectedItemPosition()).getId();
                                    sp1 = (Spinner) rootView
                                            .findViewById(R.id.select_surah_spinner);
                                    if (sp1.getSelectedItemPosition() > 0) {
                                        int p = sp1.getSelectedItemPosition();
                                        String surahid = surahs.get(p).id;
                                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                        ASyncRequest o = new ASyncRequest(context,
                                                "getAyahForLS");
                                        params.add(new BasicNameValuePair("qari",
                                                qariid));
                                        params.add(new BasicNameValuePair("surah",
                                                surahid));
                                        String response;
                                        try {
                                            response = o.execute(params).get();
                                            if (response.equals("false")) {
                                                RadioGroup rg = (RadioGroup) rootView
                                                        .findViewById(R.id.ls_select_surah_group);
                                                if (rg.getCheckedRadioButtonId() == R.id.rg_surah) {
                                                    Toast.makeText(
                                                            context,
                                                            "No Verse exists for this qari",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                // no data exist
                                            } else if (StringValidator
                                                    .isJSONValid(response)) {
                                                JSONArray a = new JSONArray(response);
                                                ayah_str = new String[a.length() + 1];
                                                ayah_data = new ArrayList<Surah>();
                                                ayah_str[0] = "Select Verse";
                                                Surah s = new Surah();
                                                s.id = "-1";
                                                s.Name = "Select";
                                                ayah_data.add(s);
                                                for (int i = 0; i < a.length(); i++) {
                                                    JSONObject o1 = a.getJSONObject(i);
                                                    s = new Surah();
                                                    s.id = o1.getString("ayah_id");
                                                    s.Name = "Verse "
                                                            + o1.getString("ayah_id");
                                                    ayah_str[i + 1] = "Verse "
                                                            + o1.getString("ayah_id");
                                                    ayah_data.add(s);
                                                }
                                                Spinner ss = (Spinner) rootView
                                                        .findViewById(R.id.select_from_spinner);
                                                ArrayAdapter<String> a1 = new ArrayAdapter<String>(
                                                        context,
                                                        android.R.layout.simple_spinner_item,
                                                        ayah_str);
                                                a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                ss.setAdapter(a1);

                                                ss = (Spinner) rootView
                                                        .findViewById(R.id.select_to_spinner);
                                                ss.setAdapter(a1);
                                                RadioGroup rg = (RadioGroup) rootView
                                                        .findViewById(R.id.ls_select_surah_group);
                                                if (rg.getCheckedRadioButtonId() == R.id.rg_surah) {
                                                    LinearLayout l = (LinearLayout) rootView
                                                            .findViewById(R.id.from_to_holder);
                                                    l.setVisibility(View.VISIBLE);
                                                } else {
                                                    LinearLayout l = (LinearLayout) rootView
                                                            .findViewById(R.id.from_to_holder);
                                                    l.setVisibility(View.GONE);
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
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        }
                    }
                });
                sp = (Spinner) rootView.findViewById(R.id.select_time_zone);
                String temp[] = new String[]{"AM", "PM"};
                a = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, temp);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(a);
                Button b1 = (Button) rootView.findViewById(R.id.calculatetime);
                b1.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String ayahfrom = "";
                        String cTime = "", ayahto = "", durationval = "", name = "", parah = "", part = "", qari = "", stime = "", surah = "", time = "", zone = " ";
                        EditText e = (EditText) rootView
                                .findViewById(R.id.et_schedule_duration);
                        durationval = e.getText().toString();
                        if (!StringValidator.lengthValidator(context, durationval, 0,
                                5, "Duration")) {
                            return;
                        }

                        e = (EditText) rootView.findViewById(R.id.et_schedule_name);
                        name = e.getText().toString();
                        if (!StringValidator.lengthValidator(context, name, 0, 40,
                                "Schedule Name")) {
                            return;
                        }
                        e = (EditText) rootView
                                .findViewById(R.id.et_schedule_start_time);
                        time = "";
                        time = e.getText().toString();
                        if (!StringValidator.lengthValidator(context, time, 8, 8,
                                "Time")) {
                            return;
                        }
                        if (!StringValidator.ValidateTimeTwelveHours(context, time)) {
                            return;
                        }
                        Spinner sp = (Spinner) rootView
                                .findViewById(R.id.select_time_zone);
                        String ampm = "AM";
                        if (sp.getSelectedItemPosition() == 1) {
                            ampm = "PM";
                        }
                        final ArrayList<BasicNameValuePair> params;
                        if (!time.isEmpty()) {
                            StringTokenizer st = new StringTokenizer(time, ":");
                            int time_hours = 0, time_minutes = 0, time_seconds = 0;
                            if (st.hasMoreTokens()) {
                                time_hours = Integer.parseInt(st.nextToken());
                            }
                            if (st.hasMoreTokens()) {
                                time_minutes = Integer.parseInt(st.nextToken());
                            }
                            if (st.hasMoreTokens()) {
                                time_seconds = Integer.parseInt(st.nextToken());
                            }
                            if (ampm.equals("PM") && time_hours < 12) {
                                time_hours = time_hours + 12;
                            }
                            if (ampm.equals("AM") && time_hours == 12) {
                                time_hours = time_hours - 12;
                            }
                            StringBuilder sb1 = new StringBuilder();
                            if (time_hours < 10) {
                                sb1.append("0").append(time_hours).append(":");
                            } else {
                                sb1.append(time_hours).append(":");
                            }
                            if (time_minutes < 10) {
                                sb1.append("0").append(time_minutes).append(":");
                            } else {
                                sb1.append(time_minutes).append(":");
                            }
                            if (time_seconds < 10) {
                                sb1.append("0").append(time_seconds);
                            } else {
                                sb1.append(time_seconds);
                            }
                            cTime = sb1.toString();
                            Calendar c = Calendar.getInstance();
                            int c_sec = c.get(Calendar.SECOND);
                            int c_min = c.get(Calendar.MINUTE);
                            int c_hour = c.get(Calendar.HOUR_OF_DAY);
                            int h = time_hours - c_hour;
                            int m = time_minutes - c_min;
                            int s = time_seconds - c_sec;
                            if (h < 0) {
                                h = 24 + (h);
                            }
                            if (m < 0) {
                                m = 60 + (m);
                                h = h - 1;
                            }
                            if (s < 0) {
                                s = 60 + (s);
                                m = m - 1;
                            }
                            ASyncRequest serv = new ASyncRequest(context,
                                    "getServerTime");
                            params = new ArrayList<BasicNameValuePair>();
                            String serverTime = null;
                            try {
                                serverTime = serv.execute(params).get();
                            } catch (InterruptedException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (ExecutionException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            st = new StringTokenizer(serverTime, ":");
                            int sH = 0, sM = 0, sS = 0;
                            if (st.hasMoreTokens()) {
                                sH = Integer.parseInt(st.nextToken());
                            }
                            if (st.hasMoreTokens()) {
                                sM = Integer.parseInt(st.nextToken());
                            }
                            if (st.hasMoreTokens()) {
                                sS = Integer.parseInt(st.nextToken());
                            }
                            sH = sH + h;
                            sM = sM + m;
                            sS = sS + s;
                            if (sH >= 24) {
                                sH = sH % 24;
                            }
                            if (sM >= 60) {
                                sM = sM % 60;
                                sH = sH + 1;
                            }
                            if (sS >= 60) {
                                sS = sS % 60;
                                sM = sM + 1;
                            }
                            StringBuilder sb = new StringBuilder();
                            if (sH < 10) {
                                sb.append("0").append(sH).append(":");
                            } else {
                                sb.append(sH).append(":");
                            }
                            if (sM < 10) {
                                sb.append("0").append(sM).append(":");
                            } else {
                                sb.append(sM).append(":");
                            }
                            if (sS < 10) {
                                sb.append("0").append(sS);
                            } else {
                                sb.append(sS);
                            }
                            stime = sb.toString();
                        }
                        sp = (Spinner) rootView
                                .findViewById(R.id.ls_select_qari_spinner);
                        if (sp.getSelectedItemPosition() == 0) {
                            Toast.makeText(context, "Please Select A Qari First.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        qari = reciters.get(sp.getSelectedItemPosition()).getId();

                        RadioGroup rg = (RadioGroup) rootView
                                .findViewById(R.id.ls_select_surah_group);

                        if (rg.getCheckedRadioButtonId() == R.id.rg_surah) {
                            part = "2";
                            sp = (Spinner) rootView
                                    .findViewById(R.id.select_surah_spinner);
                            if (sp.getSelectedItemPosition() == 0) {
                                new GenericDialogBox(context, "Please select Surah.",
                                        "", "Alert");
                                return;
                            }
                            surah = surahs.get(sp.getSelectedItemPosition()).id;
                            Spinner ss = (Spinner) rootView
                                    .findViewById(R.id.select_from_spinner);
                            int pos = ss.getSelectedItemPosition();
                            if (pos == 0) {
                                new GenericDialogBox(context,
                                        "Please select Verse Range.", "", "Alert");
                                return;
                            }
                            if (pos == -1) {
                                new GenericDialogBox(
                                        context,
                                        "No Recitations available for this qari. Please select another qari.",
                                        "", "Alert");
                                return;
                            }
                            ayahfrom = ayah_data.get(ss.getSelectedItemPosition()).id;

                            ss = (Spinner) rootView
                                    .findViewById(R.id.select_to_spinner);
                            pos = ss.getSelectedItemPosition();
                            if (pos == 0) {
                                new GenericDialogBox(context,
                                        "Please select Verse Range.", "", "Alert");
                                return;
                            }
                            if (pos == -1) {
                                new GenericDialogBox(
                                        context,
                                        "No Recitations available for this qari. Please select another qari.",
                                        "", "Alert");
                                return;
                            }
                            ayahto = ayah_data.get(ss.getSelectedItemPosition()).id;
                        } else if (rg.getCheckedRadioButtonId() == R.id.rg_juz) {
                            part = "3";
                            sp = (Spinner) rootView
                                    .findViewById(R.id.select_surah_spinner);
                            if (sp.getSelectedItemPosition() == 0) {
                                new GenericDialogBox(context,
                                        "Please select Parah/Juz.", "", "Alert");
                                return;
                            }
                            parah = parahs.get(sp.getSelectedItemPosition()).id;
                        } else {
                            part = "4";
                        }
                        final ASyncRequest obj = new ASyncRequest(context, "getTimes");
                        final List<BasicNameValuePair> params1 = new ArrayList<BasicNameValuePair>();
                        params1.add(new BasicNameValuePair("ayahFrom", ayahfrom));
                        params1.add(new BasicNameValuePair("ayahTo", ayahto));
                        params1.add(new BasicNameValuePair("cTime", cTime));
                        params1.add(new BasicNameValuePair("duration", "Days"));
                        params1.add(new BasicNameValuePair("durationval", durationval));
                        params1.add(new BasicNameValuePair("name", name));
                        params1.add(new BasicNameValuePair("parah", parah));
                        params1.add(new BasicNameValuePair("part", part));
                        params1.add(new BasicNameValuePair("qari", qari));
                        params1.add(new BasicNameValuePair("sTime", stime));
                        params1.add(new BasicNameValuePair("surah", surah));
                        params1.add(new BasicNameValuePair("time", time));
                        params1.add(new BasicNameValuePair("zone", zone));

                        try {
                            String response = obj.execute(params1).get();
                            if (response.isEmpty() || response.contains("Exception")) {

                            } else if (!response.isEmpty()) {
                                if (response.equals("-2%")) {
                                    new GenericDialogBox(context,
                                            "No record found for selected qari.", "",
                                            "Alert!");
                                    return;
                                }
                                StringTokenizer st = new StringTokenizer(response, "%");
                                final String totaltime = st.nextToken();
                                String totalayah1 = st.nextToken();
                                final String totaldays = st.nextToken();
                                double totala = Double.parseDouble(totalayah1);
                                totala = Math.ceil(totala);
                                final String totalayah = new StringBuilder().append(
                                        totala).toString();
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        getActivity());
                                final Spinner select_time_zone = (Spinner) rootView.findViewById(R.id.select_time_zone);
                                // 2. Chain together various setter methods to set the
                                // dialog characteristics
                                builder.setMessage(
                                        "Your schedule will complete in "
                                                + totaldays
                                                + " days reciting approx. "
                                                + totalayah
                                                + " ayats daily in approx."
                                                + totaltime
                                                + ". Do you want to continue.")
                                        .setTitle("Message");
                                builder.setPositiveButton(R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                String reminder = "";
                                                RadioButton rb = (RadioButton) rootView
                                                        .findViewById(R.id.rg_off);
                                                if (rb.isChecked() == true) {
                                                    reminder = "0";
                                                }
                                                rb = (RadioButton) rootView
                                                        .findViewById(R.id.rg_on);
                                                if (rb.isChecked() == true) {
                                                    reminder = "1";
                                                }
                                                params1.add(new BasicNameValuePair(
                                                        "totalayats", totalayah));
                                                params1.add(new BasicNameValuePair(
                                                        "totaldays", totaldays));
                                                params1.add(new BasicNameValuePair(
                                                        "totaltime", totaltime));
                                                params1.add(new BasicNameValuePair(
                                                        "reminder", reminder));
                                                params1.add(new BasicNameValuePair(
                                                        "tType", select_time_zone.getSelectedItem().toString()));
                                                ASyncRequest obj1 = new ASyncRequest(
                                                        context, "createSchedule");
                                                try {
                                                    String response = obj1.execute(
                                                            params1).get();
                                                    if (response.contains("Exception")) {
                                                        new GenericDialogBox(
                                                                context,
                                                                "Schedule cannot be saved.",
                                                                "", "Alert!");
                                                    } else if (response.equals("1")) {
                                                        new GenericDialogBox(context,
                                                                "Schedule saved.", "",
                                                                "Alert");
                                                        mng.popBackStack();
                                                        Fragment f = new ManageScheduleFragment();
                                                        mng.beginTransaction()
                                                                .replace(
                                                                        R.id.content_frame,
                                                                        f)
                                                                .addToBackStack(null)
                                                                .commit();
                                                    }
                                                } catch (InterruptedException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                } catch (ExecutionException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                builder.setNegativeButton(R.string.no,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                            }
                                        });
                                builder.create();
                                builder.show();
                            }
                        } catch (ExecutionException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                    }
                });
                ProgressDialog.dismissDialog();
            }
        };
        handler.postDelayed(runnable, 400);
        final Calendar now = Calendar.getInstance();
        final Spinner select_time_zone = (Spinner) rootView.findViewById(R.id.select_time_zone);
        select_time_zone.setEnabled(false);
        final EditText et = (EditText) rootView.findViewById(R.id.et_schedule_start_time);
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTimePickerDialog dialog = new MyTimePickerDialog(context, new MyTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(com.radioayah.util.TimePicker view, int hourOfDay, int minute, int seconds) {
                        String hours, minutes, second;
                        StringBuilder sb = new StringBuilder();
                        if (hourOfDay > 12) {
                            if(hourOfDay - 12 < 10)
                                sb.append("0").append(hourOfDay - 12);
                            else
                              sb.append(hourOfDay - 12);
                        }
                        else if (hourOfDay < 10){
                            if (hourOfDay == 0) {
                                sb.append("12");
                            } else {
                                sb.append("0").append(hourOfDay);
                            }
                        }
                        else
                            sb.append(hourOfDay);
                        sb.append(":");
                        if (minute < 10)
                            sb.append("0").append(minute);
                        else
                            sb.append(minute);
                        sb.append(":");
                        if (seconds < 10)
                            sb.append("0").append(seconds);
                        else
                            sb.append(seconds);
                        if (hourOfDay >= 12)
                            select_time_zone.setSelection(1);
                        else
                            select_time_zone.setSelection(0);
                        et.setText(sb.toString());
                    }
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), false);
                dialog.show();
            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MyTimePickerDialog dialog = new MyTimePickerDialog(context, new MyTimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(com.radioayah.util.TimePicker view, int hourOfDay, int minute, int seconds) {
                            String hours, minutes, second;
                            StringBuilder sb = new StringBuilder();
                            if (hourOfDay > 12) {
                                if(hourOfDay - 12 < 10)
                                    sb.append("0").append(hourOfDay - 12);
                                else
                                    sb.append(hourOfDay - 12);
                            }
                            else if (hourOfDay < 10){
                                if (hourOfDay == 0) {
                                    sb.append("12");
                                } else {
                                    sb.append("0").append(hourOfDay);
                                }
                            }
                            else
                                sb.append(hourOfDay);
                            sb.append(":");
                            if (minute < 10)
                                sb.append("0").append(minute);
                            else
                                sb.append(minute);
                            sb.append(":");
                            if (seconds < 10)
                                sb.append("0").append(seconds);
                            else
                                sb.append(seconds);
                            if (hourOfDay >= 12)
                                select_time_zone.setSelection(1);
                            else
                                select_time_zone.setSelection(0);
                            et.setText(sb.toString());
                        }
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), false);
                    dialog.show();
                }
            }
        });
        first_time = false;
        return rootView;
    }
}