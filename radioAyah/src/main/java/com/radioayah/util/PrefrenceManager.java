package com.radioayah.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MAD on 5/27/2016.
 */
public class PrefrenceManager {

        Context context;

    public PrefrenceManager(Context context) {
        this.context = context;
    }

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String language = "language";


    SharedPreferences pref = context.getSharedPreferences("MyPref",context.MODE_PRIVATE);


        public void putLang(String language)
        {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("lang",language);

        }

        public String getLang()
        {

            return pref.getString("lang", null);
        }

}




