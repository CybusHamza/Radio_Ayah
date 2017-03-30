package com.radioayah.data;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

public class Session {
    public static String base_url = "http://www.radioayah.com/api/";
    public static String share_base_url = "http://www.radioayah.com/loadTrack/loadtrackpage/";
    public static String base_url_schedule = "http://www.radioayah.com/loadTrack/Schedule1/";
    public DefaultHttpClient authhttpClient = new DefaultHttpClient();
    public BasicCookieStore cookieStore = new BasicCookieStore();
    public HttpPost httppost = new HttpPost();
    public String no_InternetConnection = "No Internet Connectivity";
    public UserData data;
    public String admin_base_url = "http://admin.radioayah.com/uploads/profile_pic/";
    public String track_url = "http://admin.radioayah.com/uploads/tracks/";
    public boolean isFacebookLogin = false;
    public boolean isGoogleLogin = false;
}