package com.radioayah;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.cybus.radioayah.R;
import com.radioayah.data.Parah;
import com.radioayah.data.Reciters;
import com.radioayah.data.Surah;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.MyTimePickerDialog;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class EditScheduleFragment extends Fragment {
    String main_id, qari_id, main_parah, main_surah, main_ayahto,
            main_ayahfrom, main_type;
    Context context;
    ArrayList<Surah> surahs;
    ArrayList<Parah> parahs;
    String[] surah_str, parah_str;
    ArrayList<Surah> ayah_data;
    String[] ayah_str;
    ArrayList<Reciters> reciters;
    String[] timezones;
    Button creatNew;
    String[] timezone_diff;
    FragmentManager mng;
    private String[] qaris;

    public EditScheduleFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_edit_listening, container, false);
        ((Explore) context).setActionBarTitle("Edit Listening Schedule");
        main_id = getArguments().getString("id");

        creatNew = (Button) rootView.findViewById(R.id.add_new_schedule);


        creatNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment f = new ListeningFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();

            }
        });

        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            ASyncRequest o = new ASyncRequest(context, "editSchedule/" + main_id);
            String response = o.execute(params).get();
            if (StringValidator.isJSONValid(response)) {
                JSONObject obj = new JSONObject(response);
                JSONObject schedule_details = obj.getJSONObject("se");
                JSONObject surah = null;
                main_type = schedule_details.getString("type");
                main_ayahfrom = schedule_details.getString("ayah_from");
                main_ayahto = schedule_details.getString("ayah_to");
                main_surah = schedule_details.getString("surah_id");
                /*
                if (schedule_details.getString("type").equals("2")) {
                    surah = obj.getJSONObject("surahs");
                }
                JSONObject parah = null;
                if (schedule_details.getString("type").equals("3")) {
                    parah = obj.getJSONObject("parahs");
                }*/
                JSONObject qari = obj.getJSONObject("qari");
                qari_id = qari.getString("id");
                EditText e = (EditText) rootView.findViewById(R.id.et_schedule_name);
                e.setText(schedule_details.getString("name"));
                qaris = new String[1];
                qaris[0] = qari.getString("fname") + qari.getString("lname");
                Spinner sp = (Spinner) rootView.findViewById(R.id.ls_select_qari_spinner);
                ArrayAdapter<String> a = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, qaris);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(a);
                sp.setEnabled(false);
                e = (EditText) rootView.findViewById(R.id.et_schedule_duration);
                e.setText(schedule_details.getString("duration"));
                e = (EditText) rootView.findViewById(R.id.et_schedule_start_time);
                e.setText(StringValidator.convertTwentyFourToTwelveHours(schedule_details.getString("client_time")));
                String title_heading = "";
             /*   if (schedule_details.getString("type").equals("2")) // surah
                {
                    TextView tv = (TextView) rootView.findViewById(R.id.tv_select_listeningtype);
                    title_heading = surah.getString("name") + " Verse From " + schedule_details.getString("ayah_from") + " To "
                            + schedule_details.getString("ayah_to");

                    tv.setText(surah.getString("name") + " Verse From " + schedule_details.getString("ayah_from") + " To "
                            + schedule_details.getString("ayah_to"));
                    main_surah = surah.getString("number");
                    main_ayahfrom = surah.getString("start_ayah");
                    main_ayahto = surah.getString("end_ayah");
                }
                if (schedule_details.getString("type").equals("3")) {
                    TextView tv = (TextView) rootView.findViewById(R.id.tv_select_listeningtype);
                    title_heading = parah.getString("Name");

                    tv.setText(parah.getString("Name"));
                    main_parah = parah.getString("number");
                }*/
                TextView temp  = (TextView) rootView.findViewById(R.id.heading_listening_edit);
                temp.setText(title_heading + "\n by " + qari.getString("fname") + qari.getString("lname"));
                String[] times = new String[]{"am", "pm"};
                sp = (Spinner) rootView.findViewById(R.id.select_time_zone);
                a = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, times);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(a);
                if (schedule_details.getString("ttype").equals("AM")) {
                    sp.setSelection(0);
                } else {
                    sp.setSelection(1);
                }
                if (schedule_details.getString("reminder").equals("1")) {
                    RadioButton rb = (RadioButton) rootView.findViewById(R.id.rg_off);
                    rb.setChecked(false);
                    rb = (RadioButton) rootView.findViewById(R.id.rg_on);
                    rb.setChecked(true);
                } else {
                    RadioButton rb = (RadioButton) rootView.findViewById(R.id.rg_off);
                    rb.setChecked(true);
                    rb = (RadioButton) rootView.findViewById(R.id.rg_on);
                    rb.setChecked(false);
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
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        Button b1 = (Button) rootView.findViewById(R.id.calculatetime);
        b1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String ayahfrom = main_ayahfrom;
                String cTime = "", ayahto = main_ayahto, durationval = "", name = "", parah = main_parah, part = main_type, qari = "", stime = "", surah = main_surah, time = "", zone = " ";
                EditText e = (EditText) rootView.findViewById(R.id.et_schedule_duration);
                durationval = e.getText().toString();
                if (!StringValidator.lengthValidator(context, durationval, 0, 5, "Duration")) {
                    return;
                }

                e = (EditText) rootView.findViewById(R.id.et_schedule_name);
                name = e.getText().toString();
                if (!StringValidator.lengthValidator(context, name, 0, 40, "Schedule Name")) {
                    return;
                }
                e = (EditText) rootView.findViewById(R.id.et_schedule_start_time);
                time = "";
                time = e.getText().toString();
                if (!StringValidator.lengthValidator(context, time, 8, 8, "Time")) {
                    return;
                }
                if (!StringValidator.ValidateTimeTwelveHours(context, time)) {
                    return;
                }
                Spinner sp = (Spinner) rootView.findViewById(R.id.select_time_zone);
                String ampm = "AM";
                if (sp.getSelectedItemPosition() == 1) {
                    ampm = "PM";
                }
                final ArrayList<BasicNameValuePair> params;
                if (!time.isEmpty()) {
                    if (!StringValidator.ValidateTimeTwelveHours(context, time)) {
                        return;
                    }
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
                    ASyncRequest serv = new ASyncRequest(context, "getServerTime");
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
                qari = qari_id;
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
                            new GenericDialogBox(context, "No record found for selected qari.", "", "Alert!");
                            return;
                        }
                        StringTokenizer st = new StringTokenizer(response, "%");
                        final String totaltime = st.nextToken();
                        String totalayah1 = st.nextToken();
                        final String totaldays = st.nextToken();
                        double totala = Double.parseDouble(totalayah1);
                        totala = Math.ceil(totala);
                        final String totalayah = new StringBuilder().append(totala).toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setMessage(
                                "Your schedule will complete in " + totaldays + " days reciting approx. " + totalayah
                                        + " ayats daily in approx." + totaltime + ". Do you want to continue?").setTitle("Message");
                        builder.setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        String reminder = "";
                                        RadioButton rb = (RadioButton) rootView.findViewById(R.id.rg_off);
                                        if (rb.isChecked() == true) {
                                            reminder = "0";
                                        }
                                        rb = (RadioButton) rootView.findViewById(R.id.rg_on);
                                        if (rb.isChecked() == true) {
                                            reminder = "1";
                                        }
                                        Spinner select_time_zone = (Spinner) rootView.findViewById(R.id.select_time_zone);
                                        params1.add(new BasicNameValuePair("totalayats", totalayah));
                                        params1.add(new BasicNameValuePair("totaldays", totaldays));
                                        params1.add(new BasicNameValuePair("totaltime", totaltime));
                                        params1.add(new BasicNameValuePair("reminder", reminder));
                                        params1.add(new BasicNameValuePair(
                                                "tType", select_time_zone.getSelectedItem().toString()));
                                        ASyncRequest obj1 = new ASyncRequest(context, "updateSchedule/" + main_id);
                                        try {
                                            String response = obj1.execute(params1).get();
                                            if (response.contains("Exception")) {
                                                new GenericDialogBox(context, "Schedule cannot be saved.", "", "Alert!");
                                            } else if (response.equals("1")) {
                                                new GenericDialogBox(context, "Schedule saved.", "", "Alert");
                                                mng.popBackStack();
                                                Fragment f = new ManageScheduleFragment();
                                                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
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
                                                mng.popBackStack();
                                                Fragment f = new ManageScheduleFragment();
                                                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
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
                                if(hourOfDay - 12 < 10) {
                                    sb.append("0").append(hourOfDay - 12);
                                } else {
                                    sb.append(hourOfDay - 12);
                                }
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
        return rootView;
    }
}