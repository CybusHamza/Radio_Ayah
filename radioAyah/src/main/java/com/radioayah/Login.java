package com.radioayah;

import android.content.Context;
import android.os.AsyncTask;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.radioayah.data.Session;
import com.radioayah.util.CustomProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SuppressWarnings("deprecation")
public class Login extends AsyncTask<List<BasicNameValuePair>, String, String> {

    private final Context mContext;
    public String response;
    public HttpResponse responsebody;
    String url = null;
//    MaterialDialog dialog;
    private String responseString;
    private CustomProgressDialog bar;

    public Login(Context context, String url) {
        mContext = context;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String re) {
    }

    @Override
    protected String doInBackground(List<BasicNameValuePair>... params) {
        try {
            MainActivity.currentSession = new Session();
            MainActivity.currentSession.authhttpClient = new DefaultHttpClient();
            HttpContext ctx = new BasicHttpContext();
            ctx.setAttribute(ClientContext.COOKIE_STORE, MainActivity.currentSession.cookieStore);
            MainActivity.currentSession.httppost = new HttpPost(Session.base_url + url);
            List<BasicNameValuePair> nameValuePairs = params[0];
            MainActivity.currentSession.httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            responsebody = MainActivity.currentSession.authhttpClient.execute(MainActivity.currentSession.httppost, ctx);
            responseString = new BasicResponseHandler().handleResponse(responsebody);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Exception";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return "Exception";
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception";
        }
        if (responseString.contains("unable to allocate memory for pool")) {
            return "Exception";
        }
        if (responseString.contains("logout")) {
            return "Exception";
        }
        return responseString;
    }
}
