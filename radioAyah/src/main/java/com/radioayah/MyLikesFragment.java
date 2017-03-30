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
import com.radioayah.adapters.ExploreAdapter;
import com.radioayah.data.Track;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyLikesFragment extends Fragment {
    Context context;
    FragmentManager mng;

    public MyLikesFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mylikes, container, false);
        ((Explore) getActivity()).setActionBarTitle("My Likes");
        ArrayList<Track> tracks = new ArrayList<Track>();
        ASyncRequest obj = new ASyncRequest(context, "getUserlikes");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        try {
            String response = obj.execute(params).get();
            if (response.contains("Exception")) {

            } else if (response.contains("false")) {
                // No likes yet
            } else {
                if (StringValidator.isJSONValid(response)) {
                    JSONArray json = new JSONArray(response);
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject inner = json.getJSONObject(i);
                        Track t = new Track();
                        t.setTrack_type(inner.getString("track_type"));
                        t.setJuz_to(inner.getString("juz_to"));
                        t.setIs_verified(inner.getString("is_verified"));
                        t.setAimage(inner.getString("aimage"));
                        t.setAid(inner.getString("aid"));
                        t.setListens(inner.getString("listens"));
                        t.setId(inner.getString("id"));
                        t.setLike(inner.getString("id"));
                        t.setJuz_id(inner.getString("juz_id"));
                        t.setUploaded_by(inner.getString("uploaded_by"));
                        t.setDescription(inner.getString("description"));
                        t.setPrintable_name(inner.getString("printable_name"));
                        t.setName(inner.getString("name"));
                        t.setPath(inner.getString("path"));
                        t.setSurah_id(inner.getString("surah_id"));
                        t.setFname(inner.getString("fname"));
                        t.setLname(inner.getString("lname"));
                        t.setCountry_id(inner.getString("country_id"));
                        t.setUploader(inner.getString("uploader"));
                        t.setId(inner.getString("ltid"));
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
        ListView lv = (ListView) rootView.findViewById(R.id.myLikes);
        ExploreAdapter adp = new ExploreAdapter(context, R.layout.activity_my_likes, tracks, false, mng, false, "");
        lv.setAdapter(adp);
        return rootView;
    }
}
