package com.radioayah;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.facebook.share.model.ShareLinkContent;
import com.google.android.gms.plus.PlusShare;
import com.radioayah.adapters.AllCommentsAdapter;
import com.radioayah.ambience.Ambience;
import com.radioayah.ambience.AmbientTrack;
import com.radioayah.data.Comments;
import com.radioayah.data.Playlist;
import com.radioayah.data.Session;
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
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class TrackPlayFragment extends Fragment {
    Context context;
    Bundle b;
    String Url = MainActivity.currentSession.track_url;
    Intent streamService;
    String liked = "";
    String path;
    String isdownloadable;
    String url;
    public static Session currentSession;
    AllCommentsAdapter adp = null;
    private static final int REQUEST_PERMISSIONS = 20;
    public TrackPlayFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_track_play,
                container, false);
        ((Explore) getActivity()).setActionBarTitle("Track Details");
        b = getArguments();
        Url += b.getString("path");
        path = b.getString("path");
        liked = (b.getString("like"));
        url = MainActivity.currentSession.admin_base_url +b.getString("aimage");
        isdownloadable = b.getString("isdownloadable");
        TextView t = (TextView) rootView
                .findViewById(R.id.track_details_surah_name);
        t.setText(b.getString("name"));
        final String track_name = b.getString("name");
        final String contentid = b.getString("id");
        t = (TextView) rootView.findViewById(R.id.track_details_qari_name);
        t.setText(b.getString("fname") + " " + b.getString("lname"));
        t = (TextView) rootView
                .findViewById(R.id.track_details_play_no_of_times);
        t.setText(b.getString("listens"));
        t = (TextView) rootView.findViewById(R.id.track_details_likes_explore);
        t.setText(b.getString("likes"));
        ImageView img = (ImageView) rootView
                .findViewById(R.id.heart_track_button);
        CircularImageView img1 = (CircularImageView) rootView
                .findViewById(R.id.explore_list_circular_image);
        if (url != null || !url.isEmpty() || img1 != null) {
            Picasso.with(context).load(url).placeholder(R.drawable.silhouttee)
                    .into(img1);
        }
        if (url.contains("default.gif") && img1 != null) {
            Picasso.with(context).load(R.drawable.silhouttee).into(img1);
        }
        if (!liked.equals("false")) {
            img.setImageResource(R.drawable.liked);
        }
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (liked.equals("false")) {
                    ASyncRequest obj = new ASyncRequest(context, "liketrack");
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("id", b.getString("id")));
                   // params.add(new BasicNameValuePair("user_id", currentSession.data.getUserid()));
                    try {
                        String response = obj.execute(params).get();

                        if(response.equals("already liked"))
                        {
                            Toast.makeText(getActivity(), "You have already liked this track", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            liked = "1331223";
                            TextView t = (TextView) rootView
                                    .findViewById(R.id.track_details_likes_explore);
                            t.setText(new StringBuilder().append(
                                    Integer.parseInt(t.getText().toString()) + 1)
                                    .toString());
                            ImageView img_like = (ImageView) rootView.findViewById(R.id.heart_track_button);
                            img_like.setImageResource(R.drawable.liked);
                        }

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    ASyncRequest obj = new ASyncRequest(context, "unliketrack");
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("id", b.getString("id")));

                    try {
                        String response = obj.execute(params).get();
                        liked = "false";
                        TextView t = (TextView) rootView
                                .findViewById(R.id.track_details_likes_explore);
                        t.setText(new StringBuilder().append(
                                Integer.parseInt(t.getText().toString()) - 1)
                                .toString());
                        ImageView img_like = (ImageView) rootView.findViewById(R.id.heart_track_button);
                        img_like.setImageResource(R.drawable.track_details_heart);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        img = (ImageView) rootView.findViewById(R.id.play_track_button);
        final ImageView finalImg = img;
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                finalImg.setClickable(false);
                finalImg.setEnabled(false);

                ASyncRequest obj = new ASyncRequest(context, "trackcount/");
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("id", b.getString("id")));
                try {
                    String reponce =  obj.execute(params).get();



                    if(reponce.equals("No internet Connection"))
                    {


                        Toast.makeText(getActivity(), reponce, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Buffering", Toast.LENGTH_LONG).show();


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                finalImg.setClickable(true);
                                finalImg.setEnabled(true);
                            }
                        }, 5000);

                        TextView t = (TextView) rootView
                                .findViewById(R.id.track_details_play_no_of_times);
                        t.setText(new StringBuilder().append(
                                Integer.parseInt(t.getText().toString()) + 1)
                                .toString());

                        AmbientTrack track1 = AmbientTrack.newInstance();
                        track1.setName(b.getString("name"))
                                .setId(new Random().nextInt(100) + 1)
                                .setAlbumName(
                                        b.getString("fname") + " "
                                                + b.getString("lname"))
                                .setAudioUri(Uri.parse(Url));

                        Ambience.activeInstance()
                                .setPlaylistTo(new AmbientTrack[]{track1}).play();
                        MusicFragment.setButtons(true);

                        MusicFragment.playing = true;
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
//                ImageView img1 = (ImageView) rootView
//                        .findViewById(R.id.play_track_button);
//                img1.setEnabled(false);
            }
        });
        img = (ImageView) rootView.findViewById(R.id.add_playlist_track_button);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String track_id = b.getString("id");
                ArrayList<Playlist> playlists_list = new ArrayList<Playlist>();
                ASyncRequest obj = new ASyncRequest(context, "AddtoPlaylist/"
                        + track_id);
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                try {
                    String response = obj.execute(params).get();
                    if (StringValidator.isJSONValid(response)) {
                        JSONObject o = new JSONObject(response);
                        String playlists = o.getString("pls");
                        if (playlists.equals("false")) {
                        } else {
                            JSONArray playlists_arr = new JSONArray(playlists);
                            for (int i = 0; i < playlists_arr.length(); i++) {
                                JSONObject tmp = playlists_arr.getJSONObject(i);
                                Playlist p = new Playlist();
                                p.setId(tmp.getString("id"));
                                p.setName(tmp.getString("name"));
                                if (tmp.getString("total").equals("false")) {
                                    p.setCount("0");
                                } else {
                                    p.setCount(tmp.getString("total"));
                                }
                                p.setIse(tmp.getString("ise"));
                                playlists_list.add(p);
                            }

                        }
                        if (playlists_list.size() == 0) {
                            Toast.makeText(context, "No Playlist Found",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            new AddToPlaylistDialog(context, playlists_list,
                                    track_id);
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
            }
        });

        getComments(rootView);
        img = (ImageView) rootView.findViewById(R.id.add_comment_button);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ASyncRequest obj = new ASyncRequest(context, "newComment");
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("id", b.getString("id")));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                EditText edit = (EditText) rootView
                        .findViewById(R.id.addCommentEdittext);
                String comment = edit.getText().toString();
                if (StringValidator.lengthValidator(context, comment, 0, 100,
                        "Comment")) {
                    params.add(new BasicNameValuePair("Comment", comment));
                    try {
                        String response = obj.execute(params).get();
                        if (response.equals("-1")) {
                            Toast.makeText(context, "Comment Not Added",
                                    Toast.LENGTH_LONG).show();
                            // no comments
                        } else {
                            Toast.makeText(context, "Comment Added",
                                    Toast.LENGTH_LONG).show();
                            edit.setText("");
                            getComments(rootView);
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        });
        img = (ImageView) rootView.findViewById(R.id.download_track_button);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getActivity(),  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                    if (Build.VERSION.SDK_INT > 22) {

                        requestPermissions(new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                      /*  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);*/
                        // Toast.makeText(MainActivity.this.getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();

                    }

                }

                else
                {
                    upload();

                }

            }
        });
        img = (ImageView) rootView.findViewById(R.id.share_track_button);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Share On")
                        .setItems(new String[]{"Facebook", "Google +"}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    ShareLinkContent content = new ShareLinkContent.Builder()
                                            .setContentTitle(track_name)
                                            .setContentTitle("Radio Ayah")
                                            .setContentDescription("Radio Ayah - Listen Islam")
                                            .setContentUrl(
                                                    Uri.parse(Session.share_base_url
                                                            + contentid)).build();
                                    Explore.shareDialog.show(content);
                                } else {
                                   Intent shareIntent = new PlusShare.Builder(context)
                                            .setType("text/plain")
                                            .setText(track_name)
                                            .setContentUrl(Uri.parse(Session.share_base_url
                                                    + contentid))
                                            .getIntent();
                                    startActivityForResult(shareIntent, 0);
                                }
                            }
                        });
                builder.show();

            }
        });
        CircularImageView user_img = (CircularImageView) rootView
                .findViewById(R.id.showuserprofilepic);
        if (!MainActivity.currentSession.data.getUpload_picture().isEmpty()
                && !MainActivity.currentSession.data.getUpload_picture()
                .contains("default.gif")) {
            if (!MainActivity.currentSession.data.getUpload_picture().contains(
                    "graph.facebook")) {
                String url = MainActivity.currentSession.admin_base_url
                        + MainActivity.currentSession.data.getUpload_picture();
                Picasso.with(context).load(url)
                        .placeholder(R.drawable.silhouttee).into(user_img);
            } else {
                String fb_url = MainActivity.currentSession.data
                        .getUpload_picture();
                Picasso.with(context).load(fb_url)
                        .placeholder(R.drawable.silhouttee).into(user_img);
            }

        }
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {




        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                upload();
            } else {


                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void upload()
    {
        if (isdownloadable.equals("1")
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            String url = Url;
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url));
            request.setDescription("Radio Ayah");
            request.setTitle("Downloading " + path);

            // in order for this if to run, you must use the android 3.2
            // to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, path);

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);


            ASyncRequest obj = new ASyncRequest(context, "addToDownload");
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("track_id", b.getString("id")));
            try {
                String response = obj.execute(params).get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } else if (isdownloadable.equals("0")) {
            Toast.makeText(context, "File can't be downloaded.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void getComments(View rootView) {
        ASyncRequest obj = new ASyncRequest(context, "getComments");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("id", b.getString("id")));
        try {
            String response = obj.execute(params).get();
            if (response.equals("false")) {
                // no comments
            } else if (StringValidator.isJSONValid(response)) {
                ArrayList<Comments> comments = new ArrayList<Comments>();
                JSONArray o1 = new JSONArray(response);
                for (int i = 0; i < o1.length(); i++) {
                    JSONObject o = o1.getJSONObject(i);
                    Comments c = new Comments();
                    c.setComment(o.getString("comment"));
                    c.setId(o.getString("id"));
                    c.setTime(o.getString("time"));
                    c.setUser_id(o.getString("user_id"));
                    c.setFlag(o.getString("flag"));
                    if(! o.getString("user").equals("false")) {
                        JSONObject tmp = o.getJSONObject("user");
                        c.setPicture(tmp.getString("upload_picture"));
                        c.setUsername(tmp.getString("first_name") + " "
                                + tmp.getString("last_name"));
                        comments.add(c);
                    }
                }
                ListView lv = (ListView) rootView
                        .findViewById(R.id.showallcommentslistview);
                adp = new AllCommentsAdapter(context,
                        R.layout.explore_listview_row, comments);
                lv.setAdapter(adp);
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
    @Override
    public void onResume() {
        super.onResume();
    }
}