package com.radioayah.util;

import android.content.Context;
import android.os.AsyncTask;

import com.radioayah.MainActivity;
import com.radioayah.data.Session;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SuppressWarnings("deprecation")
public class FBLoginAsync extends
        AsyncTask<List<BasicNameValuePair>, String, String> {

    private final Context mContext;
    public String response;
    public HttpResponse responsebody;
    String url = null;
    private String responseString;
    private CustomProgressDialog bar;

    public FBLoginAsync(Context context, String url) {
        mContext = context;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        bar = new CustomProgressDialog(mContext);
        bar.show("");
    }

    @Override
    protected void onPostExecute(String re) {
        bar.dismiss("");
    }

    @Override
    protected String doInBackground(List<BasicNameValuePair>... params) {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    Session.base_url + url);
            List<BasicNameValuePair> nameValuePairs = params[0];
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            responsebody = client.execute(httppost);
            responseString = new BasicResponseHandler()
                    .handleResponse(responsebody);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Exception";
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Exception";
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
