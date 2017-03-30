package com.radioayah;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.adapters.InviteUsersNameAdapter;
import com.radioayah.data.Project;
import com.radioayah.data.UserData;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InviteUsersFragment extends Fragment {
    public static String project_id = null;
    public static int taskid = 0;
    Context context;
    FragmentManager mng;
    HashMap<UserData, List<Project>> data;
    ArrayList<UserData> userlist;
    ListView lv;

    public InviteUsersFragment(String projectid) {
        data = new HashMap<UserData, List<Project>>();
        userlist = new ArrayList<UserData>();
        project_id = projectid;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_invite_members, container, false);
        ((Explore) getActivity()).setActionBarTitle("Invite Users");
        lv = (ListView) rootView.findViewById(R.id.list_users_to_be_added);
        Button b = (Button) rootView.findViewById(R.id.inviteuser);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final EditText ee = (EditText) rootView
                        .findViewById(R.id.et_invite_members);
                final String username = ee.getText().toString();
                if (username.equals(MainActivity.currentSession.data.getEmail())) {
                    Toast.makeText(context,
                            "You cannot type in your email address.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (StringValidator.lengthValidator(context, username, 0, 50,
                        "Email") && StringValidator.ValidateEmail(context, username)) {
                    ASyncRequest obj = new ASyncRequest(context, "getUser");
                    try {
                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        params.add(new BasicNameValuePair("user", username));
                        String res = obj.execute(params).get();
                        if (res.equals("-1")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    context);
                            builder.setMessage(
                                    "Do you want to invite the user by email?")
                                    .setTitle("Alert");
                            builder.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub

                                            ASyncRequest obj = new ASyncRequest(
                                                    context,
                                                    "sendInvitationByEmail/");
                                            try {
                                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                                params.add(new BasicNameValuePair(
                                                        "email", username));
                                                params.add(new BasicNameValuePair(
                                                        "project_id",
                                                        project_id));
                                                obj.execute(params).get();
                                                Toast.makeText(context,
                                                        "Invitation Sent.",
                                                        Toast.LENGTH_LONG)
                                                        .show();
                                                ee.setText("");

                                            } catch (InterruptedException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            } catch (ExecutionException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            builder.setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub

                                        }
                                    });
                            builder.show();

                        } else if (res.equals("-2")) {
                            Toast.makeText(context, "User is not activated!",
                                    Toast.LENGTH_LONG).show();
                        } else if (StringValidator.isJSONValid(res)) {
                            JSONObject o = new JSONObject(res);
                            String username1 = o.getString("first_name") + " "
                                    + o.getString("last_name");
                            String userid = o.getString("id");
                            UserData u = new UserData();
                            u.setFirst_name(username1);
                            u.setEmail(userid);
                            userlist.add(u);
                            List<Project> a = new ArrayList<Project>();
                            data.put(u, a);
                            InviteUsersNameAdapter adp = new InviteUsersNameAdapter(
                                    context, R.layout.fragment_explore,
                                    userlist, mng, project_id, data);
                            lv.setAdapter(adp);
                            Toast.makeText(context, "User Added to Project",
                                    Toast.LENGTH_LONG).show();
                            ee.setText("");
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
                }
            }

        });
        b = (Button) rootView.findViewById(R.id.done_editing);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String invite_reg = "";
                if (userlist.size() == 0) {
                    ASyncRequest obj = new ASyncRequest(context,
                            "inviteRegister/");
                    try {
                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        params.add(new BasicNameValuePair("invite_reg",
                                ""));
                        params.add(new BasicNameValuePair("project_id",
                                project_id));
                        String res  = obj.execute(params).get();
                        Toast.makeText(context, "Project Saved.",
                                Toast.LENGTH_LONG).show();
                        mng.popBackStack();
                        Fragment f = new ManageProjectsFragment();
                        mng.beginTransaction().replace(R.id.content_frame, f)
                                .addToBackStack(null).commit();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch
                        // block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch
                        // block
                        e.printStackTrace();
                    }



                }
                else

                {


                for (int i = 0; i < userlist.size(); i++) {
                    invite_reg += "added_" + userlist.get(i).getEmail() + ",";
                    ASyncRequest obj = new ASyncRequest(context,
                            "inviteRegister/");
                    try {
                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        params.add(new BasicNameValuePair("invite_reg",
                                invite_reg));
                        params.add(new BasicNameValuePair("project_id",
                                project_id));
                        String res  = obj.execute(params).get();
                        Toast.makeText(context, "Project Saved.",
                                Toast.LENGTH_LONG).show();
                        mng.popBackStack();
                        Fragment f = new ManageProjectsFragment();
                        mng.beginTransaction().replace(R.id.content_frame, f)
                                .addToBackStack(null).commit();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch
                        // block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch
                        // block
                        e.printStackTrace();
                    }
                 }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
        InviteUsersNameAdapter adp = new InviteUsersNameAdapter(context,
                R.layout.fragment_explore, userlist, mng, project_id, data);

        lv.setAdapter(adp);
    }
}
