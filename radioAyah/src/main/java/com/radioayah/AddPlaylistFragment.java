package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cybus.radioayah.R;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.NetworkChecker;
import com.radioayah.util.ProgressDialog;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddPlaylistFragment extends Fragment {
    Context context;
    FragmentManager mng;

    public AddPlaylistFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();
        ((Explore) getActivity()).setActionBarTitle("Playlists");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_playlists, container, false);
        Button b = (Button) rootView.findViewById(R.id.createPlaylist);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog.showDialog(context);
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        EditText et = (EditText) rootView.findViewById(R.id.et_playlist_name);
                        String playlistName = et.getText().toString();
                        if (StringValidator.lengthValidator(context, playlistName, 0, 20, "Playlist Name")) {
                            if (NetworkChecker.checkStatus(context)) {
                                ASyncRequest obj = new ASyncRequest(context, "CreateNewPlaylist");
                                try {
                                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                    params.add(new BasicNameValuePair("name", playlistName));
                                    params.add(new BasicNameValuePair("id", "-1"));
                                    String res = obj.execute(params).get();
                                    if (res.equals("1")) {
                                        new GenericDialogBox(context, "Playlist Saved", "", "Alert!");
                                        Fragment f = new AllPlaylistFragment();
                                        mng.popBackStack();
                                        mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
                                    } else {
                                        new GenericDialogBox(context, "There was an error saving playlist","", "Alert!");
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else {
                                ProgressDialog.dismissDialog();
                                new GenericDialogBox(context, "No Internet Connectivity.", "", "Alert!");
                            }
                        }
                        ProgressDialog.dismissDialog();
                    }
                };
                handler.postDelayed(runnable, 400);
            }
        });

        b = (Button) rootView.findViewById(R.id.manage_playlists);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new AllPlaylistFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        return rootView;
    }
}
