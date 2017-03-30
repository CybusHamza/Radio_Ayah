package com.radioayah;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.facebook.share.widget.ShareDialog;
import com.radioayah.adapters.NavDrawerListAdapter;
import com.radioayah.adapters.NavDrawerOnItemClickListener;
import com.radioayah.ambience.Ambience;
import com.radioayah.data.Parah;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.DB;
import com.radioayah.util.RefreshSuggestions;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;



public class Explore extends Activity  {
    public static ShareDialog shareDialog;
    final String[] from = new String[]{"cityName"};
    final int[] to = new int[]{android.R.id.text1};
    FragmentManager fragmentManager = null;
    DrawerLayout d;
    ActionBarDrawerToggle mDrawerToggle;
    ArrayList<Parah> tmp;
    String currentTab = "";
    private String[] mMenuTitles;
    private ListView mDrawerList;
    private String[] SUGGESTIONS;
    private SimpleCursorAdapter mAdapter;
    SearchView searchView = null;
    boolean loadprojectfragment = false;
    Bundle b;
    String language = "eng";
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String language= pref.getString("user_id","eng");
        changeLang(language);

        if(getIntent() != null)
        {
            if(getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("project"))
            {
                loadprojectfragment = true;
            }
            if(MainActivity.currentSession == null)
            {
                Intent i = new Intent("android.intent.action.MainActivity");
                startActivity(i);
                finish();
            }
        }
        DB db = new DB(this);
        shareDialog = new ShareDialog(this);
        tmp = db.getAllSuggestionsDB();
        SUGGESTIONS = new String[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            SUGGESTIONS[i] = tmp.get(i).name;
        }
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setProgressBarIndeterminateVisibility(true);
        Ambience.turnOn(this);
        fragmentManager = getFragmentManager();
        Fragment fragment = new ExploreFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        Fragment player = new MusicFragment();
        FragmentManager f = getFragmentManager();
        f.beginTransaction().replace(R.id.player_frame, player).commit();
        if(loadprojectfragment)
        {
            fragment = new ManageProjectsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        }
        d = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuTitles = getResources().getStringArray(R.array.LeftMenuItems);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(this, R.layout.drawer_layout, mMenuTitles);
        mDrawerList.setOnItemClickListener(new NavDrawerOnItemClickListener(this, fragmentManager, d));
        mDrawerList.invalidate();
        mDrawerList.setAdapter(adapter);

        setActionBarTitle("Explore");
        mDrawerToggle = new ActionBarDrawerToggle(this, /*
                                                         * host Activity
														 */
                d, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.abc_action_bar_home_description, /* "open drawer" description */
                R.string.abc_action_bar_home_description /* "close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(currentTab);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(currentTab);
            }
        };

        // Set the drawer toggle as the DrawerListener
        d.setDrawerListener(mDrawerToggle);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(R.drawable.ic_drawer);
       //GCMManager.getInstance(this).registerListener(this);

        Intent intent = new Intent(Explore.this,RegistrationIntentService.class);
        startService(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.explore, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setOnSuggestionListener(new OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                // TODO Auto-generated method stub
                String track_id = tmp.get(position).id;
                ArrayList<String> tmp_array = new ArrayList<String>();
                for (int i = 0; i < SUGGESTIONS.length; i++) {
                    if (SUGGESTIONS[i].toLowerCase().startsWith(searchView.getQuery().toString().toLowerCase())) {
                        tmp_array.add(SUGGESTIONS[i]);
                    }
                }
                String track_name = tmp_array.get(position);
                for (int i = 0; i < tmp.size(); i++) {
                    if (track_name.equals(tmp.get(i).name)) {
                        track_id = tmp.get(i).id;
                    }
                }
                ASyncRequest obj = new ASyncRequest(Explore.this, "loadTrackDetail/" + track_id);
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                try {
                    String res = obj.execute(params).get();
                    if (StringValidator.isJSONValid(res)) {
                        JSONObject o = new JSONObject(res);
                        JSONObject track = o.getJSONObject("track");
                        Bundle b = new Bundle();
                        b.putString("artist_id", track.getString("artist_id"));
                        b.putString("name", track.getString("name"));
                        b.putString("path", track.getString("path"));
                        b.putString("isdownloadable", track.getString("isdownloadable"));
                        b.putString("image", track.getString("image"));
                        b.putString("description", track.getString("description"));
                        if (o.getString("artist").equals("false")) {
                            Toast.makeText(Explore.this, "An Error Occured While Loading Track Info", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        JSONObject artist = o.getJSONObject("artist");
                        b.putString("fname", artist.getString("fname"));
                        b.putString("bio", artist.getString("bio"));
                        b.putString("lname", artist.getString("lname"));
                        b.putString("city", artist.getString("city"));
                        b.putString("bio", artist.getString("bio"));
                        b.putString("likes", track.getString("likes"));
                        b.putString("listens", track.getString("listens"));
                        b.putString("like", o.getString("like"));
                        b.putString("id", track.getString("id"));
                        Fragment f = new TrackPlayFragment();
                        f.setArguments(b);
                        FragmentManager mng = getFragmentManager();
                        mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
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
                return false;
            }
        });
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Auto-generated method stub
                populateAdapter(s);
                return false;
            }
        });

        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        return true;
    }

    private void populateAdapter(String query) {

        new RefreshSuggestions(this).execute();
        DB db = new DB(this);
        tmp = db.getAllSuggestionsDB();
        SUGGESTIONS = new String[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            SUGGESTIONS[i] = tmp.get(i).name;
        }
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
        for (int i = 0; i < SUGGESTIONS.length; i++) {
            if (SUGGESTIONS[i].toLowerCase().startsWith(query.toLowerCase())) {
                c.addRow(new Object[]{i, SUGGESTIONS[i]});
            }
        }
        mAdapter.changeCursor(c);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.advsearch){
            advanceSearchDialog();
        }
        if (id == R.id.action_settings) {
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Ambience.activeInstance();
        Ambience.stopListeningForUpdates();
    }

    public void advanceSearchDialog(){
        new AdvancedSearch(this,fragmentManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //GCMManager.getInstance(this).unRegisterListener();
        Ambience.turnOff();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.getStringExtra(SearchManager.QUERY);
        }
    }

    public void setActionBarTitle(String title_str) {
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ACB3")));

        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
                title.setText(title_str);
            }
        }
        currentTab = title_str;
    }

   /* @Override
    public void onDeviceRegisted(String s) {
        ASyncRequest obj = new ASyncRequest(Explore.this, "save_device_id/");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("device_id",s));
        obj.execute(params);
    }

    @Override
    public void onMessage(String s, Bundle bundle) {

    }*/
    public void changeLang(String lang)
    {

            myLocale = new Locale(lang);
            saveLocale(lang);
            Locale.setDefault(myLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = myLocale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            updateTexts();
    }

    public void saveLocale(String lang)
    {
        String langPref = language;
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }


    public void loadLocale()
    {
        String langPref =language;
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }


    private void updateTexts()
    {

    }

  /*  @Override
    public void onPlayServiceError() {
        GCMManager.getInstance(this).registerListener(this);
    }*/
}
