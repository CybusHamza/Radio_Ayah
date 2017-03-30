package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.radioayah.AllPlaylistFragment;
import com.radioayah.MainActivity;
import com.radioayah.ManagePlaylist;
import com.radioayah.MusicFragment;
import com.cybus.radioayah.R;
import com.radioayah.ambience.Ambience;
import com.radioayah.ambience.AmbientTrack;
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

public class PlaylistsAdapter extends ArrayAdapter<Playlist> {
    public Context context;
    ArrayList<Playlist> rec;
    boolean showOptions = true;
    FragmentManager mng;

    public PlaylistsAdapter(Context context, int textViewResourceId,
                            ArrayList<Playlist> records, boolean show, FragmentManager m) {
        super(context, textViewResourceId);
        rec = new ArrayList<Playlist>();
        rec = records;
        showOptions = show;
        this.context = context;
        mng = m;
        // this.records = records;
    }

    @Override
    public int getCount() {
        return rec.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.manage_playlist_row, parent, false);
        } else {
            row = convertView;
        }
        final Playlist temp = rec.get(position);
        if (temp != null) {
            TextView t = (TextView) row.findViewById(R.id.tv_playlist_name);
            t.setText(temp.getName());

            row.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Bundle b = new Bundle();
                    b.putString("id", temp.getId());
                    Fragment f = new ManagePlaylist();
                    f.setArguments(b);
                    mng.beginTransaction().replace(R.id.content_frame, f)
                            .addToBackStack(null).commit();
                }
            });
            ImageView i = (ImageView) row.findViewById(R.id.playlist_remove_button);
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setMessage(
                            "Do you really want to Delete the playlist.")
                            .setTitle("Alert");
                    builder.setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    ASyncRequest obj = new ASyncRequest(
                                            context, "deletePlaylist/"
                                            + temp.getId());
                                    try {
                                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                        String res = obj.execute(params).get();
                                        if (res.equals("1")) {
                                            Toast.makeText(context,
                                                    "Playlist Deleted.",
                                                    Toast.LENGTH_LONG).show();
                                            mng.popBackStack();
                                            Fragment f = new AllPlaylistFragment();
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
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                }
                            });
                    builder.show();
                }
            });
            i = (ImageView) row.findViewById(R.id.playlist_play_button);
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    ASyncRequest obj = new ASyncRequest(context, "PlayAll/");
                    try {
                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        params.add(new BasicNameValuePair("id", temp.getId()));
                        String res = obj.execute(params).get();
                        if (res.equals("-3")) {
                            Toast.makeText(context, "Playlist Empty.",
                                    Toast.LENGTH_LONG).show();
                        } else if (StringValidator.isJSONValid(res)) {
                            ArrayList<AmbientTrack> playlist = new ArrayList<AmbientTrack>();
                            JSONArray arr = new JSONArray(res);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject o = arr.getJSONObject(i);
                                AmbientTrack track1 = AmbientTrack
                                        .newInstance();
                                track1.setName(o.getString("name"))
                                        .setId(Integer.parseInt(o
                                                .getString("id")))
                                        .setArtistName(
                                                o.getString("fname") + " "
                                                        + o.getString("lname"))
                                        .setAlbumName(
                                                o.getString("fname") + " "
                                                        + o.getString("lname"))
                                        .setAudioUri(
                                                Uri.parse(MainActivity.currentSession.track_url
                                                        + o.getString("path")));
                                playlist.add(track1);
                            }
                            Ambience.activeInstance().setPlaylistTo(playlist)
                                    .play();
                            MusicFragment.setButtons(true);
                            Toast.makeText(context, "Playlist Started",
                                    Toast.LENGTH_LONG).show();
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
            });
        }
        return row;
    }
}