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
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.adapters.ExploreAdapter;
import com.radioayah.data.Track;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ManagePlaylist extends Fragment {

    Context context;
    FragmentManager mng;
    Bundle b;
    ArrayList<Track> tracksData;
    String playlist_id = "";

    public ManagePlaylist() {

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
        View rootView = inflater.inflate(R.layout.fragment_manage_playlists,
                container, false);
        ((Explore) getActivity()).setActionBarTitle("Manage Playlist");
        b = getArguments();
        playlist_id = b.getString("id");
        ASyncRequest obj = new ASyncRequest(context, "seeAllPlaylist");
        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String res = obj.execute(params).get();
            if (StringValidator.isJSONValid(res)) {
                JSONArray arr = new JSONArray(res);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    if (o.getString("id").equals(b.get("id"))) {
                        tracksData = new ArrayList<Track>();
                        JSONArray arr1 = o.getJSONArray("tracks");
                        if (arr1.length() == 0) {
                            new GenericDialogBox(context,"No Tracks added in this play list.","","Alert!");
                          /*  Fragment f = null;
                            f = new AddPlaylistFragment();
                            mng.beginTransaction().replace(R.id.content_frame, f).commit();*/

                        } else {

                            for (int j = 0; j < arr1.length(); j++) {
                                JSONArray arr2 = arr1.getJSONArray(j);
                                JSONObject inner = arr2.getJSONObject(0);
                                Track t = new Track();
                                t.setTrack_type(inner.getString("track_type"));
                                t.setJuz_to(inner.getString("juz_to"));
                                t.setIs_verified(inner.getString("is_verified"));
                                t.setAimage(inner.getString("aimage"));
                                t.setAid(inner.getString("aid"));
                                t.setListens(inner.getString("listens"));
                                t.setId(inner.getString("id"));
                                t.setJuz_id(inner.getString("juz_id"));
                                t.setUploaded_by(inner.getString("uploaded_by"));
                                t.setDescription(inner.getString("description"));
                                t.setPrintable_name(inner
                                        .getString("printable_name"));
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
                                t.setIsdownloadable(inner
                                        .getString("isdownloadable"));
                                t.setJuz_from(inner.getString("juz_from"));
                                t.setLikes(inner.getString("likes"));
                                t.setLike("false");
                                tracksData.add(t);
                            }
                            ListView lv = (ListView) rootView
                                    .findViewById(R.id.manage_show_playlists);
                            ExploreAdapter adp = new ExploreAdapter(context,
                                    R.id.activity_qari_detail_title, tracksData, false, mng, true,
                                    playlist_id);
                            lv.setAdapter(adp);

                        }
                    }
                }
            }
            }catch(InterruptedException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch(ExecutionException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch(JSONException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return rootView;

        }
    }

