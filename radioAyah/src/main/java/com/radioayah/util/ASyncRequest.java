package com.radioayah.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.radioayah.MainActivity;
import com.radioayah.data.Session;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SuppressWarnings("deprecation")
public class ASyncRequest extends
        AsyncTask<List<BasicNameValuePair>, String, String> {

    public String response;
    public HttpResponse responsebody;
    String url = null;
    private String responseString;
    private Context context;

    public ASyncRequest(Context context, String url) {
        this.url = url;
        this.context = context;
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
            HttpContext ctx = new BasicHttpContext();
            ctx.setAttribute(ClientContext.COOKIE_STORE, MainActivity.currentSession.cookieStore);
            MainActivity.currentSession.httppost = new HttpPost(Session.base_url + url);
            List<BasicNameValuePair> nameValuePairs = params[0];
            SharedPreferences settings = context.getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
            String test = settings.getString("token", "false");
            if (!test.equals("")) {
                nameValuePairs.add(new BasicNameValuePair("auth_token", settings.getString("token", "")));
            }
            MainActivity.currentSession.httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            responsebody = MainActivity.currentSession.authhttpClient.execute(MainActivity.currentSession.httppost, ctx);
            responseString = new BasicResponseHandler().handleResponse(responsebody);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Exception";
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            return "Exception";
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
