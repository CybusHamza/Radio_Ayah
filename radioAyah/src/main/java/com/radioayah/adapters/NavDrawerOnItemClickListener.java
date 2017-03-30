package com.radioayah.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.radioayah.AddPlaylistFragment;
import com.radioayah.AddProjectFragment;
import com.radioayah.AllReciters;
import com.radioayah.DownloadFragment;
import com.radioayah.EditProfileFragment;
import com.radioayah.ExploreFragment;
import com.radioayah.LanguageFragment;
import com.radioayah.ListeningFragment;
import com.radioayah.MainActivity;
import com.radioayah.ManagePlaylist;
import com.radioayah.ManageProjectsFragment;
import com.radioayah.ManageScheduleFragment;
import com.radioayah.MusicFragment;
import com.radioayah.MyLikesFragment;
import com.cybus.radioayah.R;import com.radioayah.UploadFragment;
import com.radioayah.util.CustomProgressDialog;
import com.radioayah.util.ProgressDialog;

public class NavDrawerOnItemClickListener implements OnItemClickListener {

    private final Context c;
    FragmentManager mng;
    DrawerLayout dL;
    private CustomProgressDialog bar;
    private GoogleApiClient mGoogleApiClient;

    public NavDrawerOnItemClickListener(Context c, FragmentManager m,
                                        DrawerLayout d) {
        this.c = c;
        mng = m;
        dL = d;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(c)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            final int position, long id) {
        ProgressDialog.showDialog(c);
        dL.closeDrawer(Gravity.LEFT);
        // TODO Auto-generated method stub

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Fragment f = null;
                switch (position) {
                    case 0: {
                        if (!MainActivity.currentSession.isGoogleLogin && !MainActivity.currentSession.isFacebookLogin) {
                            f = new EditProfileFragment();
                        }
                        break;
                    }
                    case 1: {
                        f = new ExploreFragment();
                        break;
                    }
                    case 2: {
                        f = new MyLikesFragment();
                        break;
                    }
                    case 3: {
                        //playlist
                        f = new AddPlaylistFragment();
                        break;
                    }
                    case 4: {
                        f = new AllReciters();
                        break;
                    }
                    case 5: {
                        f = new UploadFragment();
                        break;
                    }
                    case 6: {
                        //project
                        f = new AddProjectFragment();
                        break;
                    }
                    case 7: {
                        //schedule
                        f = new ListeningFragment();
                        break;
                    }
                    case 8: {
                        Toast.makeText(c, "Loading Radio", Toast.LENGTH_SHORT).show();
                        MusicFragment.playRadio();
                        break;
                    }
                    case 9: {

                       f=new DownloadFragment();

                        break;
                    }
                    case 10: {

                       f=new LanguageFragment() ;

                        break;
                    }
                    case 11: {
                        if (MainActivity.currentSession.isFacebookLogin == true) {
                            LoginManager.getInstance().logOut();
                            mng.popBackStack();

                            MainActivity.currentSession.isFacebookLogin = false;
                        } else {
                            mng.popBackStack();

                        }

                        Intent i = new Intent(c,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(i);


                        SharedPreferences settings = c.getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                        settings.edit().clear().commit();
                        Toast.makeText(c, "Logged Out.", Toast.LENGTH_LONG).show();


                        SharedPreferences pref = c.getApplicationContext().getSharedPreferences("Google Prefs", c.MODE_PRIVATE);
                        String user_session= pref.getString("is_loggedIn", "");

                        SharedPreferences sharedPref = c.getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("logged_in", "false");
                        editor.commit();


                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {

                                    }
                                });
                        break;
                    }
                }
                if (f != null) {
                    mng.beginTransaction().replace(R.id.content_frame, f)
                            .addToBackStack(null).commit();
                }
                ProgressDialog.dismissDialog();
            }
        };

        handler.postDelayed(runnable, 400);
    }



}
