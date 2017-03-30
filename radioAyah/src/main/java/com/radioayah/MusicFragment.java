package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.ambience.Ambience;
import com.radioayah.ambience.AmbientTrack;

import java.util.ArrayList;
import java.util.Random;

public class MusicFragment extends Fragment implements Ambience.AmbientListener {

    public static boolean playing = false;
    private static ImageView mPlayButton;
    private TextView mNowPlayingTrack;
    private TextView mNowPlayingAlbum;
    private static ImageView mPrevButton;
    private static ImageView mNextButton;
    boolean loaded = false;
    static Context context;
    static ProgressBar pd ;
    private static boolean radio = false;
    static boolean schedule = false;
    public MusicFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create adapter for track list;
    }

    @Override
    public void onResume() {
        super.onResume();

        // start getting updates for the ambient service
        Ambience.activeInstance().listenForUpdatesWith(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player, container, false);
        setupPlayerView(rootView);
        rootView.setFocusable(true);
        return rootView;
    }

    private void setupPlayerView(final View view) {

        mPlayButton = (ImageView) view.findViewById(R.id.play);
        pd = (ProgressBar) view.findViewById(R.id.radio_loader);
        mPlayButton.setFocusable(true);
        mPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (playing == false && loaded == true) {
                    if(schedule)
                    {
                        SchedulePlayDialog.showCurrentAyah();
                    }
                    Ambience.activeInstance().play();
                    playing = true;
                    mPlayButton.setImageResource(R.drawable.ic_action_pause);
                } else if (loaded == true) {
                    Ambience.activeInstance().pause();
                    mPlayButton.setImageResource(R.drawable.ic_action_play);
                    playing = false;
                }
            }
        });
        mNowPlayingTrack = (TextView) view.findViewById(R.id.display_track_name);
        mNowPlayingAlbum = (TextView) view.findViewById(R.id.display_track_album);
        mPrevButton = (ImageView) view.findViewById(R.id.previous);
        mPrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(schedule)
                {
                    if(SchedulePlayDialog.current_track - 2 >= 0 || SchedulePlayDialog.current_track == 1) {
                        SchedulePlayDialog.current_track --;
                        SchedulePlayDialog.showPreviousAyah();
                        Ambience.activeInstance().previous();
                    }
                }
                else
                {
                    Ambience.activeInstance().previous();
                }
            }
        });
        mNextButton = (ImageView) view.findViewById(R.id.next);
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Ambience.activeInstance().skip();
                if (schedule) {
                    //        SchedulePlayDialog.showNextAyah();
                }
            }
        });
    }

    public static void playRadio()
    {
        radio = true;
        pd.setVisibility(View.VISIBLE);
        setButtons(false);
        AmbientTrack track1 = AmbientTrack.newInstance();
        track1.setName("Live Radio").setId(new Random().nextInt(100) + 1).setAlbumName("")
                .setAudioUri(Uri.parse("http://xfer.cybussolutions.com:8000/stream/1/"));

        Ambience.activeInstance().setPlaylistTo(new AmbientTrack[]{track1}).play();
    }
    public static void setButtons(boolean state)
    {
        mPrevButton.setEnabled(state);
        mNextButton.setEnabled(state);
    }
    public void setPauseView() {

        if (mPlayButton != null) {
            mPlayButton.setImageResource(R.drawable.ic_action_play);
            mPrevButton.setEnabled(false);
            mNextButton.setEnabled(false);
            playing = false;
        }
    }

    public void setPlayView() {
        if (mPlayButton != null) {
            playing = true;
            mPlayButton.setImageResource(R.drawable.ic_action_pause);
            mPrevButton.setEnabled(true);
            mNextButton.setEnabled(true);
        }
    }

    public void hideNowPlayingTextViews() {
        if (mNowPlayingTrack != null) {
            mNowPlayingTrack.setVisibility(View.GONE);
        }

        if (mNowPlayingAlbum != null) {
            mNowPlayingAlbum.setVisibility(View.GONE);
        }
    }

    private void disablePlayerButton() {
        // mPlayButton.setEnabled(false);
    }

    private void enablePlayerButtons() {
        mPlayButton.setEnabled(true);
    }

    public void showNowPlayingTextViews() {

        if (mNowPlayingTrack != null) {
            mNowPlayingTrack.setVisibility(View.VISIBLE);
        }

        if (mNowPlayingAlbum != null) {
            mNowPlayingAlbum.setVisibility(View.VISIBLE);
        }
    }
    public static void loadSchedule(ArrayList<AmbientTrack> tracks)
    {
        setButtons(true);
        if (tracks.size() > 0) {
            Ambience.activeInstance().setPlaylistTo(tracks).play();
            Ambience.activeInstance().turnRepeatOff();
            Toast.makeText(context, "Schedule Loaded To Player", Toast.LENGTH_LONG).show();
            schedule = true;
            radio = false;
        }
    }
    @Override
    public void ambienceIsPreppingTrack() {
        disablePlayerButton();
        if(schedule)
            SchedulePlayDialog.showNextAyah();
    }

    @Override
    public void ambienceTrackDuration(int time) {

    }

    @Override
    public void ambiencePlayingTrack(AmbientTrack track) {
        if (mNowPlayingTrack != null) {
            mNowPlayingTrack.setText(track.getName());
        }
        if (mNowPlayingAlbum != null) {
            mNowPlayingAlbum.setText(track.getAlbumName());
        }
        showNowPlayingTextViews();
    }

    @Override
    public void ambienceTrackCurrentProgress(int time) {

    }

    @Override
    public void ambienceTrackIsPlaying() {
        loaded = true;
        enablePlayerButtons();
        setPlayView();
        if(radio)
        {
            pd.setVisibility(View.GONE);
        }
    }

    @Override
    public void ambienceTrackIsPaused() {
        setPauseView();
        playing = false;

    }

    @Override
    public void ambienceTrackHasStopped() {
        loaded = false;
        mPlayButton.setImageResource(R.drawable.ic_action_play);
        playing = false;
    }

    @Override
    public void ambiencePlaylistCompleted() {

        Ambience.activeInstance().stop();
        setPauseView();
    }

    @Override
    public void ambienceErrorOccurred() {
        // not implemented
    }

    @Override
    public void ambienceServiceStarted(Ambience activeInstance) {
        // not implemented
    }

    @Override
    public void ambienceServiceStopped(Ambience activeInstance) {
        // not implemented
    }
    public static void loadRadio()
    {

    }
}
