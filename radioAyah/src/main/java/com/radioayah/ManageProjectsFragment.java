package com.radioayah;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.adapters.ManageProjectsAdapter;
import com.radioayah.data.Project;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ManageProjectsFragment extends Fragment {
    Context context;
    FragmentManager mng;
    ArrayList<Project> proj_list;

    public ManageProjectsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_projects, container, false);
        ((Explore) getActivity()).setActionBarTitle("Manage Projects");
        try {
            ASyncRequest o = new ASyncRequest(context, "manageProject");
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String res = o.execute(params).get();
            if (res.equals("-1")) {
               /* Fragment f = new ExploreFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();*/
                Toast.makeText(context, "No Projects Added Yet.", Toast.LENGTH_LONG).show();
            } else if (StringValidator.isJSONValid(res)) {
                JSONArray arr = new JSONArray(res);
                proj_list = new ArrayList<Project>();
                for (int i = 0; i < arr.length(); i++) {
                    Project p = new Project();
                    ArrayList<Project> temp_inner = new ArrayList<Project>();
                    JSONObject obj = arr.getJSONObject(i);
                    p.setProjectid(obj.getString("project_id"));
                    p.setName(obj.getString("project_name"));
                    p.setDesc(obj.getString("project_description"));
                    p.setType(obj.getString("actions"));
                    JSONArray inner_arr = obj.getJSONArray("members");
                    for (int j = 0; j < inner_arr.length(); j++) {
                        Project p_inner = new Project();
                        JSONObject innerjson = inner_arr.getJSONObject(j);
                        p_inner.setProjectid(innerjson.getString("project_id"));
                        p_inner.setTaskid(innerjson.getString("task_id"));
                        p_inner.setUserid(innerjson.getString("user_id"));
                        if (!innerjson.isNull("status")) {
                            p_inner.setStatus(innerjson.getString("status"));
                        }
                        p_inner.setMember_name(innerjson.getString("member_name"));
                        p_inner.setType(innerjson.getString("type"));
                        if (innerjson.getString("type").equals("3")) {
                            if (!innerjson.isNull("ayah_to")) {
                                p_inner.setAyahto(innerjson.getString("ayah_to"));
                            }
                            if (!innerjson.isNull("ayah_from")) {
                                p_inner.setAyahfrom(innerjson.getString("ayah_from"));
                            }
                        }
                        p_inner.setTypename(innerjson.getString("type_name"));
                        p_inner.setIsadmin(innerjson.getString("is_admin"));
                        if (!innerjson.isNull("status_can_change")) {
                            p_inner.setCanchangestatus(innerjson.getString("status_can_change"));
                        } else {
                            p_inner.setCanchangestatus("0");
                        }
                        if (!innerjson.isNull("can_remove")) {
                            p_inner.setCanremove(innerjson.getString("can_remove"));
                        } else {
                            p_inner.setCanremove("-1");
                        }
                        if (!innerjson.isNull("remove_caption")) {
                            p_inner.setRemovecaption(innerjson.getString("remove_caption"));
                        }
                        temp_inner.add(p_inner);
                    }
                    p.setMembers(temp_inner);
                    proj_list.add(p);
                }
            }
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (proj_list != null) {
            ListView lv = (ListView) rootView.findViewById(R.id.manage_projects_list);
            ManageProjectsAdapter adp = new ManageProjectsAdapter(context, R.layout.explore_listview_row, proj_list, mng);
            lv.setAdapter(adp);
        }
        Button b = (Button) rootView.findViewById(R.id.add_new_project);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new AddProjectFragment();
                mng.popBackStack();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        return rootView;
    }
}
