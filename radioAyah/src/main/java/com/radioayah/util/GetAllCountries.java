package com.radioayah.util;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class GetAllCountries extends AsyncTask<String, String, String> {

    public Context mContext;
    String response = null;

    public GetAllCountries(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... arg0) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;
        HttpGet httpGet = new HttpGet(
                "http://omar.cybussolutions.com/radioAyah/client/api/getAllCountriesJSON");
        try {
           // httpGet.setHeader("Accept", "application/json");
            httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            DB obj = new DB(mContext);
            if (StringValidator.isJSONValid(response)) {
                obj.insertCountries(response);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Exception";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Exception";
        }
        if (response.contains("unable to allocate memory for pool")) {
            return "Exception";
        }
        if (response.contains("logout")) {
            return "Exception";
        }
        return response;
    }

    protected void onPostExecute(Void arg0) {

    }
}
