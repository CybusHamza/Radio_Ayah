package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.radioayah.AllPlaylistFragment;
import com.radioayah.MainActivity;
import com.cybus.radioayah.R;
import com.radioayah.TrackPlayFragment;
import com.radioayah.data.Track;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.CircularImageView;
import com.radioayah.util.ExploreListData;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExploreAdapter extends ArrayAdapter<ExploreListData> {
    public Context context;
    ArrayList<Track> rec;
    boolean showOptions = true;
    FragmentManager mng;
    boolean showdel = false;
    String playlist_id = "";
     TextView t_likes;

    public ExploreAdapter(Context context, int textViewResourceId,
                          ArrayList<Track> records, boolean show, FragmentManager m,
                          boolean showdelete, String playlistId) {
        super(context, textViewResourceId);
        rec = new ArrayList<Track>();
        rec = records;
        showOptions = show;
        this.context = context;
        mng = m;
        showdel = showdelete;
        playlist_id = playlistId;
        // this.records = records;
    }

    @Override
    public int getCount() {
        return rec.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater
                    .inflate(R.layout.explore_listview_row, parent, false);
        } else {
            row = convertView;
        }


        final Track temp = rec.get(position);
        if (temp != null) {
            CircularImageView img = (CircularImageView) row
                    .findViewById(R.id.explore_list_circular_image);
            String url = MainActivity.currentSession.admin_base_url
                    + temp.getAimage();
            if (url != null || !url.isEmpty() || img != null) {
                Picasso.with(context).load(url)
                        .placeholder(R.drawable.track_icon).into(img);
            }
            TextView t = (TextView) row.findViewById(R.id.lv_qari_name);
            t.setText(temp.getFname() + " (" + temp.getPrintable_name()
                    + " Reciter)");
            t = (TextView) row.findViewById(R.id.lv_surah_name);
            t.setText(temp.getName());
            t = (TextView) row.findViewById(R.id.lv_play_no_of_times);
            t.setText(new StringBuilder().append(temp.getListens()).toString());


            t = (TextView) row.findViewById(R.id.tb_download);
            t.setText(new StringBuilder().append(temp.getDownloads()).toString());


             t_likes = (TextView) row
                    .findViewById(R.id.lv_likes_explore);
            t_likes.setText(new StringBuilder().append(temp.getLikes())
                    .toString());
            if (showOptions == false) {
                ImageView i = (ImageView) row.findViewById(R.id.lv_options);
                i.setVisibility(View.INVISIBLE);
            }
            ImageView i = (ImageView) row.findViewById(R.id.lv_options);
            final View finalRow = row;
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    PopupMenu popup = new PopupMenu(context, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    if (temp.getLike().equals("false")) {
                        inflater.inflate(R.menu.tracklike, popup.getMenu());
                    } else {
                        inflater.inflate(R.menu.trackunlike, popup.getMenu());
                    }
                    popup.show();
                    popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.like_song:
                                    if (!temp.getLike().equals("false")) {
                                        ASyncRequest obj = new ASyncRequest(
                                                context, "unliketrack");
                                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                        params.add(new BasicNameValuePair("id",
                                                temp.getId()));
                                        try {
                                            String response = obj.execute(params)
                                                    .get();
                                            Toast.makeText(context, response,
                                                    Toast.LENGTH_LONG).show();
                                            temp.setLike("false");
                                            t_likes = (TextView) finalRow
                                                    .findViewById(R.id.lv_likes_explore);
                                            if(response.equals("Un Liked."))
                                            {
                                                t_likes.setText(new StringBuilder()
                                                        .append(Integer
                                                                .parseInt(t_likes
                                                                        .getText()
                                                                        .toString()) - 1)
                                                        .toString());
                                            }

                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        return true;
                                    } else {
                                        ASyncRequest obj = new ASyncRequest(
                                                context, "liketrack");
                                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                        params.add(new BasicNameValuePair("id",
                                                temp.getId()));
                                        temp.setLike("111112");

                                        try {
                                            String response = obj.execute(params)
                                                    .get();
                                            t_likes = (TextView) finalRow
                                                    .findViewById(R.id.lv_likes_explore);
                                            if(response.equals("Liked."))
                                            {
                                                t_likes.setText(new StringBuilder().append(
                                                        Integer.parseInt(t_likes.getText()
                                                                .toString()) + 1)
                                                        .toString());
                                            }
                                            Toast.makeText(context, response,
                                                    Toast.LENGTH_LONG).show();
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        return true;
                                    }
                                default:
                                    return false;
                            }
                        }
                    });
                }
            });
            i = (ImageView) row.findViewById(R.id.playlist_track_delete_button);
            if (showdel == true) {
                i.setVisibility(View.VISIBLE);
            }
            i.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setMessage(
                            "Do you want to delete the track from playlist?")
                            .setTitle("Alert");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    ASyncRequest obj = new ASyncRequest(
                                            context, "RmPlEx");
                                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                    params.add(new BasicNameValuePair("track",
                                            temp.getId()));
                                    params.add(new BasicNameValuePair(
                                            "playlist", playlist_id));
                                    try {
                                        obj.execute(params).get();
                                        Toast.makeText(context,
                                                "Track Removed.",
                                                Toast.LENGTH_LONG).show();
                                        rec.remove(position);
                                        if(rec.size() == 0)
                                        {
                                            Fragment f = new AllPlaylistFragment();
                                            mng.popBackStack();
                                            mng.beginTransaction().replace(R.id.content_frame, f)
                                                    .addToBackStack(null).commit();
                                        }
                                        notifyDataSetChanged();
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
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            });
                    builder.show();
                }
            });
        }

        row.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) { // TODO Auto-generated method
                Bundle b = new Bundle();
                t_likes = (TextView) v.findViewById(R.id.lv_likes_explore);
                b.putString("admin_url", temp.getAdmin_url());
                b.putString("like", temp.getLike());
                b.putString("track_type", temp.getTrack_type());
                b.putString("listens", temp.getListens());
                b.putString("id", temp.getId());
                b.putString("printable_name", temp.getPrintable_name());
                b.putString("name", temp.getName());
                b.putString("path", temp.getPath());
                b.putString("surah_id", temp.getSurah_id());
                b.putString("fname", temp.getFname());
                b.putString("lname", temp.getLname());
                b.putString("aimage", temp.getAimage());
                b.putString("image", temp.getImage());
                b.putString("isdownloadable", temp.getIsdownloadable());
                b.putString("downloads", temp.getDownloads());
                b.putString("likes", t_likes.getText().toString());
                b.putString("uploader_name", temp.getUploader_name());
                b.putString("isOffline", "false");
                Fragment f = new TrackPlayFragment();
                f.setArguments(b);
                mng.beginTransaction().replace(R.id.content_frame, f)
                        .addToBackStack(null).commit();
            }
        });

        return row;
    }
}