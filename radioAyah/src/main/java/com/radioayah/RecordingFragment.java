package com.radioayah;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.util.VisualizerView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RecordingFragment extends Fragment {
    public static final String DIRECTORY_NAME_TEMP = "AudioTemp";
    public static final int REPEAT_INTERVAL = 40;
    Context context;
    FragmentManager mng;
    Button play, stop, record;
    int recordtime = 0;
    VisualizerView visualizerView;
    File audioDirTemp;
    Handler play_handler;
    int recordtime_seconds = 0;
    MediaPlayer m;
    boolean isplaying = false;
    Button b = null;
    private Button txtRecord;
    private Button play_button;
    private MediaRecorder recorder = null;
    private boolean isRecording = false;
    private Handler handler;
    private boolean record_finished = false;
    private TextView status = null;
    String[] permissionsRequired = new String[]{android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUEST_PERMISSIONS = 20;
    // updates the visualizer every 50 milliseconds
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView
                recordtime += 40;
                status.setText("Record Time : "
                        + convertMilliseconds(recordtime));
                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL);
            } else {
                recordtime = 0;
                recordtime_seconds = 0;
            }
        }
    };
    OnClickListener recordClick = new OnClickListener() {

        @Override
        public void onClick(View v) {


            if (ActivityCompat.checkSelfPermission(getActivity(),  android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(),  Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                if (Build.VERSION.SDK_INT > 22) {

                    requestPermissions(permissionsRequired, REQUEST_PERMISSIONS);
                      /*  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);*/
                    // Toast.makeText(MainActivity.this.getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();

                }

            }

            else
            {

                record();

            }


        }
    };


    public void record()
    {
        if (isplaying) {
            Toast.makeText(context, "Cannot record while playing",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!isRecording) {
            // isRecording = true;
            recordtime = 0;
            txtRecord.setText("Stop Recording");
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioChannels(1);
            recorder.setAudioSamplingRate(44100);
            recorder.setAudioEncodingBitRate(96000);
            recorder.setOutputFile(audioDirTemp + "/audio_file" + ".m4a");
            OnErrorListener errorListener = null;
            recorder.setOnErrorListener(errorListener);
            OnInfoListener infoListener = null;
            recorder.setOnInfoListener(infoListener);
            try {
                recorder.prepare();
                recorder.start();
                isRecording = true; // we are currently recording
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(updateVisualizer);
        } else {
            txtRecord.setText("Start Recording");
            releaseRecorder();
        }
    }

    Runnable updatePlayTime = new Runnable() {
        @Override
        public void run() {
            if (m != null) {
                if (m.isPlaying()) {
                    isplaying = true;
                    long t = m.getCurrentPosition();
                    String played = convertMilliseconds(t);
                    t = m.getDuration();
                    String total = convertMilliseconds(t);
                    status.setText(new StringBuilder()
                            .append("Recording Time :").append(played + "/")
                            .append(total).toString());
                    handler.postDelayed(this, 1000);
                } else {
                    isplaying = false;
                }
            }
        }
    };

    public RecordingFragment() {
    }

    public static boolean deleteFilesInDir(File path) {

        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {

                } else {
                    files[i].delete();
                }
            }
        }
        return true;
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
        final View rootView = inflater.inflate(R.layout.fragment_recording,
                container, false);
        ((Explore) getActivity()).setActionBarTitle("Record Audio");
        visualizerView = (VisualizerView) rootView
                .findViewById(R.id.visualizer);
        play_button = (Button) rootView.findViewById(R.id.play_recording);
        txtRecord = (Button) rootView.findViewById(R.id.record_button);
        txtRecord.setOnClickListener(recordClick);
        status = (TextView) rootView.findViewById(R.id.status_play);
        audioDirTemp = new File(Environment.getExternalStorageDirectory(),
                DIRECTORY_NAME_TEMP);
        if (audioDirTemp.exists()) {
            deleteFilesInDir(audioDirTemp);
        } else {
            audioDirTemp.mkdirs();
        }
        b = (Button) rootView.findViewById(R.id.done_recording);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (record_finished == false) {
                    Toast.makeText(context,
                            "Please record track before exiting.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (record_finished == true) {
                    Toast.makeText(context,
                            "recorded sucessfully",
                            Toast.LENGTH_LONG).show();

                    UploadFragment.record = 1;
                    mng.popBackStack();
                }


            }
        });
        handler = new Handler();
        play_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (record_finished == false) {
                    Toast.makeText(context,
                            "Please Record a track to play it.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (isRecording == true) {
                    Toast.makeText(
                            context,
                            "Track is currently recording, can't play while recording.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (!isplaying) {
                    m = new MediaPlayer();
                    isplaying = true;
                    try {
                        m.setDataSource(audioDirTemp + "/audio_file" + ".m4a");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        m.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    m.start();
                    play_handler = new Handler();
                    play_handler.post(updatePlayTime);
                } else {
                    play_handler.removeCallbacks(updatePlayTime);
                    m.stop();
                    m.reset();
                    m.release();
                    m = null;
                    isplaying = false;
                }
                if (isplaying) {
                    play_button.setText("Stop Playing");
                } else {
                    play_button.setText("Play");
                }
            }
        });
        return rootView;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                record();
            } else {


                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private void releaseRecorder() {
        if (recorder != null) {
            isRecording = false; // stop recording
            record_finished = true;
            handler.removeCallbacks(updateVisualizer);
            visualizerView.clear();
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    String convertMilliseconds(long millis) {
        String hms = String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(millis)));
        return hms;
    }
}
