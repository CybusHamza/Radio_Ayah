package com.radioayah;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cybus.radioayah.R;
import com.radioayah.adapters.Download_Addaper;
import com.radioayah.adapters.ExploreAdapter;
import com.radioayah.data.Download;
import com.radioayah.data.Track;
import com.radioayah.util.ASyncRequest;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class DownloadFragment extends Fragment {

    Context context;
    FragmentManager mng;
    Bundle b = null;
    ArrayList<Download> tracks = new ArrayList<>();



    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();
        b = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.download_fragment, container, false);

        ((Explore) context).setActionBarTitle("My Downloads");

        ASyncRequest obj = new ASyncRequest(context, "getDownload");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        try {
            String response = obj.execute(params).get();

            JSONArray jsonArray =  new JSONArray(response);

            for (int i = 0 ; i <=jsonArray.length();i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                Download downlaod = new Download();

                downlaod.setTrack_id(object.getString("track_id"));
                downlaod.setFname(object.getString("fname"));
                downlaod.setLname(object.getString("lname"));
                downlaod.setName(object.getString("name"));
                downlaod.setListens(object.getString("listens"));
                downlaod.setLikes(object.getString("likes"));
                downlaod.setImage(object.getString("image"));
                downlaod.setArtist_id(object.getString("artist_id"));

                tracks.add(downlaod);

            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView lv = (ListView) rootView.findViewById(R.id.download_listview);
        Download_Addaper adp = new Download_Addaper(context,
                R.layout.activity_explore, tracks);
        lv.setAdapter(adp);

        return rootView;

    }

}
