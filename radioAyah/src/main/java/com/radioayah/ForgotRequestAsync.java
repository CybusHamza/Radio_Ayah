package com.radioayah;

import android.content.Context;
import android.os.AsyncTask;

import com.radioayah.data.Session;
import com.radioayah.util.CustomProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SuppressWarnings("deprecation")
public class ForgotRequestAsync extends
        AsyncTask<List<BasicNameValuePair>, String, String> {

    private final Context mContext;
    public String response;
    public HttpResponse responsebody;
    String url = null;
    private String responseString;
    private CustomProgressDialog bar;

    public ForgotRequestAsync(Context context, String url) {
        mContext = context;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        bar = new CustomProgressDialog(mContext);
        bar.setCancelable(false);
        bar.show("");
    }

    @Override
    protected void onPostExecute(String re) {
        bar.dismiss("");
    }

    @Override
    protected String doInBackground(List<BasicNameValuePair>... params) {
        try {
            DefaultHttpClient authhttpClient = new DefaultHttpClient();
            HttpContext ctx = new BasicHttpContext();
            BasicCookieStore cookies = new BasicCookieStore();
            ctx.setAttribute(ClientContext.COOKIE_STORE, cookies);
            HttpPost httppost = new HttpPost(Session.base_url + url);
            List<BasicNameValuePair> nameValuePairs = params[0];
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            responsebody = authhttpClient.execute(httppost, ctx);
            responseString = new BasicResponseHandler()
                    .handleResponse(responsebody);
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
