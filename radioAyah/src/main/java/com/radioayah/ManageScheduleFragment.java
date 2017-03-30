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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.adapters.ManageScheduleAdapter;
import com.radioayah.data.Schedule;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ManageScheduleFragment extends Fragment {
    Context context;
    FragmentManager mng;
    ArrayList<Schedule> arraylist;

    public ManageScheduleFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.manage_schedule_fragment, container, false);
        ((Explore) getActivity()).setActionBarTitle("Manage Listening Schedule");
        Button b = (Button) rootView.findViewById(R.id.add_new_schedule);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new ListeningFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        ASyncRequest obj = new ASyncRequest(context, "mySchedule/");
        try {
            List<BasicNameValuePair> params = new ArrayList<>();
            String res = obj.execute(params).get();
            if (StringValidator.isJSONValid(res)) {
                arraylist = new ArrayList<>();
                JSONArray arr = new JSONArray(res);
                if (arr.length() == 0) {
                    Toast.makeText(context, "No Schedules Made Yet.", Toast.LENGTH_LONG).show();
                }
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Schedule a = new Schedule();
                    a.setId(o.getString("id"));
                    a.setName(o.getString("name"));
                    arraylist.add(a);
                }
                ListView lv = (ListView) rootView.findViewById(R.id.manage_schedule_listview);
                ManageScheduleAdapter adp = new ManageScheduleAdapter(context, R.layout.manage_schedule_row, arraylist, false, mng);
                lv.setAdapter(adp);
            }
        } catch (InterruptedException | JSONException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rootView;
    }
}