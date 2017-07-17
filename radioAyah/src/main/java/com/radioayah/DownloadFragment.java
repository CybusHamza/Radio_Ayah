package com.radioayah;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    ArrayList<Track> tracks = new ArrayList<>();



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
                JSONObject inner = jsonArray.getJSONObject(i);
                Track t = new Track();

                t.setTrack_type(inner.getString("track_type"));
                t.setJuz_to(inner.getString("juz_to"));
                t.setIs_verified(inner.getString("is_verified"));
                t.setAimage(inner.getString("aimage"));
                t.setListens(inner.getString("listens"));
                t.setId(inner.getString("id"));
                t.setJuz_id(inner.getString("juz_id"));
                t.setUploaded_by(inner.getString("uploaded_by"));
                t.setDescription(inner.getString("description"));
                t.setName(inner.getString("name"));
                t.setPath(inner.getString("path"));
                t.setSurah_id(inner.getString("surah_id"));
                t.setFname(inner.getString("fname"));
                t.setLname(inner.getString("lname"));
                t.setCountry_id(inner.getString("country_id"));
                t.setUploader(inner.getString("uploader"));
                t.setAyah_to(inner.getString("ayah_to"));
                t.setImage(inner.getString("image"));
                t.setArtist_id(inner.getString("artist_id"));
                t.setDuration(inner.getString("duration"));
                t.setAyah_from(inner.getString("ayah_from"));
                t.setIsdownloadable(inner.getString("isdownloadable"));
                t.setJuz_from(inner.getString("juz_from"));
                t.setLikes(inner.getString("likes"));

                tracks.add(t);

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
                R.layout.activity_explore,mng, tracks);
        lv.setAdapter(adp);

        return rootView;

    }

}
