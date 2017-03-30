package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.cybus.radioayah.R;
import com.radioayah.adapters.AllRecitersAdapter;
import com.radioayah.data.Reciters;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AllReciters extends Fragment {
    Context context;
    FragmentManager mng;

    public AllReciters() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_all_reciters, container, false);
        ((Explore) context).setActionBarTitle("All Reciters");
        ArrayList<Reciters> reciters = new ArrayList<Reciters>();
        ASyncRequest obj = new ASyncRequest(context, "allReciters");
        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String res = obj.execute(params).get();
            if (StringValidator.isJSONValid(res)) {
                JSONArray arr = new JSONArray(res);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Reciters r = new Reciters();
                    r.setAdded_by(o.getString("added_by"));
                    r.setBio(o.getString("bio"));
                    r.setCity(o.getString("city"));
                    r.setCountry_id(o.getString("country_id"));
                    r.setCreated_by(o.getString("created_by"));
                    r.setDob(o.getString("dob"));
                    r.setDod(o.getString("dod"));
                    r.setFname(o.getString("fname"));
                    r.setId(o.getString("id"));
                    r.setImage(o.getString("image"));
                    r.setLink(o.getString("link"));
                    r.setLname(o.getString("lname"));
                    r.setPrintable_name(o.getString("printable_name"));
                    r.setReason(o.getString("reason"));
                    r.setVerify_qari(o.getString("verify_qari"));
                    reciters.add(r);
                }
                GridView gv = (GridView) rootView.findViewById(R.id.gridView1);
                gv.setAdapter(new AllRecitersAdapter(context, reciters, mng));
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
        return rootView;
    }
}
