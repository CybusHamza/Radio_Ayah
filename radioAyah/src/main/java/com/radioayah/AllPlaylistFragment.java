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
import com.radioayah.adapters.PlaylistsAdapter;
import com.radioayah.data.Playlist;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AllPlaylistFragment extends Fragment {

    Context context;
    FragmentManager mng;

    public AllPlaylistFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_playlists, container, false);
        ((Explore) context).setActionBarTitle("My Playlists");
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        ASyncRequest obj = new ASyncRequest(context, "seeAllPlaylist");
        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String res = obj.execute(params).get();
            if (StringValidator.isJSONValid(res)) {

                JSONArray arr = new JSONArray(res);
                if (arr.length() == 0) {
                    Toast.makeText(context, "No Playlists Added Yet.", Toast.LENGTH_LONG).show();
                  /*  Fragment f = new ExploreFragment();
                    mng.popBackStack();
                    mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();*/
                }
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Playlist r = new Playlist();
                    r.setId(o.getString("id"));
                    r.setName(o.getString("name"));
                    playlists.add(r);
                }
            } else if (res.equals("false")) {
                Toast.makeText(context, "No Playlists Added Yet.", Toast.LENGTH_LONG).show();
                mng.popBackStack();
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
        ListView lv = (ListView) rootView.findViewById(R.id.manage_show_playlists);
        PlaylistsAdapter apd = new PlaylistsAdapter(context, R.layout.activity_explore, playlists, false, mng);
        lv.setAdapter(apd);
        Button b = (Button) rootView.findViewById(R.id.add_new_playlist);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new AddPlaylistFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        return rootView;
    }
}