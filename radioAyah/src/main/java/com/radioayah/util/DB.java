package com.radioayah.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.radioayah.data.Countries;
import com.radioayah.data.Parah;
import com.radioayah.data.Surah;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "RadioAyah.db";
    public final static int VERSION_NUMBER = 4;
    public String querytablecountries = "CREATE TABLE countries" + "("
            + "id integer PRIMARY KEY ," + "country_name text)";
    public String droptablecountries = "DROP TABLE IF EXISTS 'countries'";
    public Context c;

    public String querytableparah = "CREATE TABLE parah (id integer PRIMARY KEY ,parah_name text, parah_number text)";
    public String querytablesugestions = "CREATE TABLE sugestions (id integer PRIMARY KEY ,sugesstion_name text, image text)";
    public String droptablesugestions = "DROP TABLE IF EXISTS sugestions";
    public String querytablesurah = "CREATE TABLE surah (id integer PRIMARY KEY ,surah_name text, parah_number text)";
    public String droptablesurah = "DROP TABLE IF EXISTS 'parah'";
    public String droptabledownloads= "DROP TABLE IF EXISTS 'downloads'";
    public String droptableparah = "DROP TABLE IF EXISTS 'surah'";
    public String tabledonwloads = "CREATE TABLE downloads (id integer PRIMARY KEY AUTOINCREMENT,surah_name text, qari_name text, likes text, played text, download text , trackurl text)";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(querytablecountries);
        db.execSQL(querytableparah);
        db.execSQL(querytablesurah);
        db.execSQL(querytablesugestions);
        db.execSQL(tabledonwloads);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(droptablecountries);
        db.execSQL(droptableparah);
        db.execSQL(droptablesurah);
        db.execSQL(droptablesugestions);
        db.execSQL(droptabledownloads);
        onCreate(db);
    }

    public void insertDownload(String trackname,String qariname,String likes,String downloads,String plays,String trackURL)
    {
        SQLiteDatabase db = DB.this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("surah_name",trackname);
        values.put("qari_name",qariname);
        values.put("likes",likes);
        values.put("download",downloads);
        values.put("played",plays);
        values.put("trackurl",trackURL);
        db.insert("downloads", null, values);
    }

    public void insertCountries(final String countries) {
        new AsyncTask<Context, Object, Object>() {

            @Override
            protected Object doInBackground(Context... params) {
                try {
                    if (StringValidator.isJSONValid(countries)) {
                        SQLiteDatabase db = DB.this.getWritableDatabase();
                        db.execSQL(droptablecountries);
                        db.execSQL(querytablecountries);
                        JSONArray arr = new JSONArray(countries);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj1 = arr.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id",
                                    Integer.parseInt(obj1.getString("id")));
                            values.put("country_name",
                                    obj1.getString("country_name"));
                            db.insert("countries", null, values);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public ArrayList<Countries> getCountriesDB(String whereClause) {
        ArrayList<Countries> records = new ArrayList<Countries>();
        String selectQuery;
        if (whereClause.isEmpty()) {
            selectQuery = "Select * from countries ";
        } else {
            selectQuery = "Select * from countries " + whereClause;
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Countries tm = new Countries();
        tm.setName("Select Country");
        tm.setId("-1");
        records.add(tm);
        if (cursor.moveToFirst()) {
            do {
                Countries rec = new Countries();
                StringBuilder sb = new StringBuilder();
                rec.setId(sb.append(cursor.getInt(0)).toString());
                rec.setName(cursor.getString(1));
                records.add(rec);
            } while (cursor.moveToNext());
        }
        db.close();
        return records;
    }

    public void insertParah(final String parah) {
        try {
            if (StringValidator.isJSONValid(parah)) {
                SQLiteDatabase db = DB.this.getWritableDatabase();
                db.execSQL("DELETE FROM parah");
                JSONArray arr = new JSONArray(parah);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put("id", Integer.parseInt(obj1.getString("id")));
                    values.put("parah_name", obj1.getString("parah_name"));
                    values.put("parah_number", obj1.getString("parah_no"));
                    db.insert("parah", null, values);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insertSurah(final String surah) {
        try {
            if (StringValidator.isJSONValid(surah)) {
                SQLiteDatabase db = DB.this.getWritableDatabase();
                db.execSQL("DELETE FROM surah");
                JSONArray arr = new JSONArray(surah);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put("id", Integer.parseInt(obj1.getString("id")));
                    values.put("surah_name", obj1.getString("name"));
                    values.put("parah_number", obj1.getString("parah_id"));
                    db.insert("surah", null, values);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<Surah> getAllSurahDB(String whereClause) {
        ArrayList<Surah> records = new ArrayList<Surah>();
        String selectQuery;
        if (whereClause.isEmpty()) {
            selectQuery = "Select * from surah ";
        } else {
            selectQuery = "Select * from surah " + whereClause;
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Surah rec = new Surah();
                StringBuilder sb = new StringBuilder();
                rec.id = (sb.append(cursor.getInt(0)).toString());
                rec.Name = new StringBuilder().append(cursor.getInt(0)).toString() + " " + (cursor.getString(1));
                records.add(rec);
            } while (cursor.moveToNext());
        }
        db.close();
        return records;
    }

    public ArrayList<Parah> getAllParahDB(String whereClause) {
        ArrayList<Parah> records = new ArrayList<Parah>();
        String selectQuery;
        if (whereClause.isEmpty()) {
            selectQuery = "Select * from parah ";
        } else {
            selectQuery = "Select * from parah " + whereClause;
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Parah rec = new Parah();
                StringBuilder sb = new StringBuilder();
                rec.id = (sb.append(cursor.getInt(0)).toString());
                rec.name = (cursor.getString(1));
                records.add(rec);
            } while (cursor.moveToNext());
        }
        db.close();
        return records;
    }

    public void insertSugesstions(String surah) {
        try {
            if (StringValidator.isJSONValid(surah)) {
                SQLiteDatabase db = DB.this.getWritableDatabase();
                db.execSQL("DELETE FROM sugestions");
                JSONArray arr = new JSONArray(surah);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put("id", Integer.parseInt(obj1.getString("id")));
                    values.put("sugesstion_name", obj1.getString("name"));
                    db.insert("sugestions", null, values);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<Parah> getAllSuggestionsDB() {
        ArrayList<Parah> records = new ArrayList<Parah>();
        String selectQuery;
        selectQuery = "Select * from sugestions ";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Parah rec = new Parah();
                StringBuilder sb = new StringBuilder();
                rec.id = (sb.append(cursor.getInt(0)).toString());
                rec.name = (cursor.getString(1));
                records.add(rec);
            } while (cursor.moveToNext());
        }
        db.close();
        return records;
    }


}
