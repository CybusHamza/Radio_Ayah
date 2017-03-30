package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cybus.radioayah.R;
import com.radioayah.adapters.ExploreAdapter;
import com.radioayah.data.Track;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.CircularImageView;
import com.radioayah.util.StringValidator;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class QariDetailFragment extends Fragment {
    Context context;
    String qari;
    FragmentManager mng;

    public QariDetailFragment() {

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
        View rootView = inflater.inflate(R.layout.activity_qari_detail,
                container, false);
        ((Explore) getActivity()).setActionBarTitle("Qari Detail");
        qari = getArguments().getString("qari_id");
        getArguments().getString("bio");

        ArrayList<Track> tracks = new ArrayList<Track>();
        ASyncRequest async = new ASyncRequest(context, "qariRecitations/"
                + qari);
        String[] likedid;
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        JSONObject artistdata = null;
        try {
            String response = async.execute(params).get();
            if (StringValidator.isJSONValid(response)) {
                JSONObject outerjson = new JSONObject(response);
                artistdata = outerjson.getJSONObject("artist");
                JSONArray tracksjson = new JSONArray();
                if (!outerjson.isNull("tracks")) {
                    tracksjson = outerjson.getJSONArray("tracks");
                }
                JSONArray likesjson = new JSONArray();
                if (!outerjson.isNull("likes")) {
                    likesjson = outerjson.getJSONArray("likes");
                }

                likedid = new String[likesjson.length()];
                for (int i = 0; i < likesjson.length(); i++) {
                    JSONObject inner = likesjson.getJSONObject(i);
                    likedid[i] = inner.getString("ltid");
                }
                for (int i = 0; i < tracksjson.length(); i++) {
                    JSONObject inner = tracksjson.getJSONObject(i);
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
                    t.setPrintable_name(inner.getString("printable_name"));
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
                    t.setLike("false");
                    for (int j = 0; j < likedid.length; j++) {
                        if (t.getId().equals(likedid[j])) {
                            t.setLike("13234");
                        }
                    }
                    tracks.add(t);
                }
                if (artistdata != null) {
                    TextView t = (TextView) rootView
                            .findViewById(R.id.qari_details_qari_name);
                    t.setText(artistdata.getString("fname") + " "
                            + artistdata.getString("lname"));
                    t = (TextView) rootView.findViewById(R.id.qari_details_dob);
                    String qari_details = "( "
                            + new StringTokenizer(artistdata.getString("dob"),
                            " ").nextToken()
                            + " - ";
                    if(artistdata.getString("dod").contains("0000-00-00"))
                    {
                        qari_details += "Alive)";
                    }
                    else
                        qari_details += new StringTokenizer(artistdata.getString("dod"),
                            " ").nextToken() + " )";
                    t.setText(qari_details);
                    t = (TextView) rootView
                            .findViewById(R.id.set_qari_details_here);
                    t.setText(artistdata.getString("bio"));
                    t.setMovementMethod(new ScrollingMovementMethod());
                    String url = MainActivity.currentSession.admin_base_url
                            + artistdata.getString("image");
                    CircularImageView c = (CircularImageView) rootView
                            .findViewById(R.id.qari_details_circular_qari_pic);
                    Picasso.with(context).load(url)
                            .placeholder(R.drawable.silhouttee).into(c);
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
        ListView lv = (ListView) rootView
                .findViewById(R.id.qari_details_tracks_listview);
        ExploreAdapter adp = new ExploreAdapter(context,
                R.layout.activity_qari_detail, tracks, false, mng, false, "");
        lv.setAdapter(adp);
        return rootView;
    }
}