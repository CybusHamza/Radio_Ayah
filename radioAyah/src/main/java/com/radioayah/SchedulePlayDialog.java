package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.ambience.AmbientTrack;
import com.radioayah.util.ASyncRequest;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class SchedulePlayDialog extends Fragment {
    static ArrayList<String> schedule = null;
    ArrayList<AmbientTrack> tracks = null;
    Context context;
    FragmentManager mng;
    static int current_track= -1;
    int total_tracks;
    static TextView schedule_text,schedule_text1,schedule_text2,schedule_text3,schedule_text4,schedule_text5;
    static ArrayList<String> surah_names;
    static TextView tv;
    public SchedulePlayDialog() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();
        ((Explore) getActivity()).setActionBarTitle("Play Schedule");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_schedule, container, false);
        tv = (TextView) rootView.findViewById(R.id.heading_schedule_dialog);
        current_track = -1;
        String id = getArguments().getString("id");
        ArrayList<String> tokens = new ArrayList<String>();
        ASyncRequest obj = new ASyncRequest(context, "playSchedule/");
        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            String res = obj.execute(params).get();
            if (res.equals("-3")) {
                Toast.makeText(context, "Schedule Cannot be played.", Toast.LENGTH_LONG).show();
            } else {
                StringTokenizer st = new StringTokenizer(res, "%");
                while (st.hasMoreElements()) {
                    tokens.add(st.nextToken());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        schedule = new ArrayList<>();
        tracks = new ArrayList<>();
        surah_names = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String temp = tokens.get(i);
            StringTokenizer st = new StringTokenizer(temp, "!");
            if (st.hasMoreElements()) {
                temp = st.nextToken();
            }
            String qari = st.nextToken();
            surah_names.add(temp + " \n( " + qari + " )");
            String tbp = tokens.get(i);
            StringTokenizer st1 = new StringTokenizer(tbp, "!");
            String ayah = st1.nextToken();
            String qariname = st1.nextToken();
            String trackpath = st1.nextToken();
            AmbientTrack track1 = AmbientTrack.newInstance();
            track1.setName("Schedule " + ayah).setId(new Random().nextInt(100) + 1).setAlbumName(qariname)
                    .setAudioUri(Uri.parse(MainActivity.currentSession.track_url + trackpath));
            tracks.add(track1);
            if (st1.hasMoreTokens()) {
                String arabic = st1.nextToken() + ".";
                arabic = arabic.replace("<p>", "");
                arabic = arabic.replace("</p>", "");
                schedule.add(arabic);
            }
        }
        MusicFragment.loadSchedule(tracks);
        total_tracks = tracks.size();
        schedule_text = (TextView) rootView.findViewById(R.id.ayah_schedule_dialog);
        schedule_text1 = (TextView) rootView.findViewById(R.id.ayah_schedule_dialog1);
        schedule_text2 = (TextView) rootView.findViewById(R.id.ayah_schedule_dialog2);
        schedule_text3 = (TextView) rootView.findViewById(R.id.ayah_schedule_dialog3);
        schedule_text4 = (TextView) rootView.findViewById(R.id.ayah_schedule_dialog4);
        schedule_text5 = (TextView) rootView.findViewById(R.id.ayah_schedule_dialog5);
        schedule_text.setMovementMethod(new ScrollingMovementMethod());
        return rootView;
    }

    public static void showNextAyah()
    {
        current_track ++;
        schedule_text.setText(schedule.get(current_track));
        tv.setText(surah_names.get(current_track));
    }
    public static void showPreviousAyah()
    {
        current_track --;
        if(current_track  < 0) {
            current_track = -1;
        }
        else{
        schedule_text.setText(schedule.get(current_track));
        tv.setText(surah_names.get(current_track));
    }}
    public static void showCurrentAyah()
    {
        current_track --;
    }
}