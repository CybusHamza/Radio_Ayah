package com.radioayah;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.data.Countries;
import com.radioayah.data.Session;
import com.radioayah.data.UserData;
import com.radioayah.util.CustomProgressDialog;
import com.radioayah.util.DB;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SignUp extends Activity {

    ArrayList<Countries> c;
    String[] c_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar bar = getActionBar();

        DB o = new DB(this);
        c = o.getCountriesDB("");
        if (!c.isEmpty()) {
            c_str = new String[c.size()];
            for (int i = 0; i < c.size(); i++) {
                c_str[i] = c.get(i).name;
            }
            Spinner sp = (Spinner) findViewById(R.id.countries_dropdown);
            ArrayAdapter<String> a = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, c_str);
            a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(a);
        }

        Button b = (Button) findViewById(R.id.submit_signup);
        b.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String username, password, cPass, fname, lname, email;
                EditText e = (EditText) findViewById(R.id.et_signup_username);
                username = e.getText().toString();
                e = (EditText) findViewById(R.id.et_signup_password);
                password = e.getText().toString();
                e = (EditText) findViewById(R.id.et_confirm_password);
                cPass = e.getText().toString();
                e = (EditText) findViewById(R.id.et_fname);
                fname = e.getText().toString();
                e = (EditText) findViewById(R.id.et_lname);
                lname = e.getText().toString();
                e = (EditText) findViewById(R.id.et_email);
                email = e.getText().toString();
                Spinner sp = (Spinner) findViewById(R.id.countries_dropdown);
                final String country = c.get(sp.getSelectedItemPosition())
                        .getId();

                if (StringValidator.lengthValidator(SignUp.this, username, 3,
                        25, "Username")
                        && StringValidator.lengthValidator(SignUp.this, fname,
                        3, 50, "First Name")
                        && StringValidator.lengthValidator(SignUp.this, lname,
                        3, 50, "Last Name")
                        && StringValidator.lengthValidator(SignUp.this, email,
                        3, 50, "Email Address")
                        && StringValidator.ValidateEmail(SignUp.this, email)
                        && StringValidator.lengthValidator(SignUp.this,
                        password, 8, 25, "Password")
                        && StringValidator.lengthValidator(SignUp.this, cPass,
                        8, 25, "Confirm Password")
                        && StringValidator.match(SignUp.this, password, cPass)) {
                    if(country.equals("-1"))
                    {
                        new GenericDialogBox(SignUp.this,"Please Select A Country.","","Alert!");
                        return;
                    }
                    final CustomProgressDialog bar = new CustomProgressDialog(
                            SignUp.this);
                    bar.setCancelable(false);
                    bar.show("");
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                            params.add(new BasicNameValuePair("user_name1",
                                    username));
                            params.add(new BasicNameValuePair("password1",
                                    password));
                            params.add(new BasicNameValuePair("email", email));
                            params.add(new BasicNameValuePair("lname", lname));
                            params.add(new BasicNameValuePair("fname", fname));
                            params.add(new BasicNameValuePair("country",
                                    country));
                            SignUpRequest o = new SignUpRequest(SignUp.this,
                                    "signup_client");
                            try {
                                String res = o.execute(params).get();
                                if (res.contains("1")) {
                                    Toast.makeText(SignUp.this, "Registered Sucessfully.", Toast.LENGTH_LONG).show();

                                    MainActivity.currentSession = new Session();
                                    params = new ArrayList<BasicNameValuePair>();
                                    params.add(new BasicNameValuePair(
                                            "user_name",
                                            username));
                                    params.add(new BasicNameValuePair(
                                            "password",
                                            password));
                                    try {
                                        res = new Login(
                                                SignUp.this,
                                                "loginProcess1/")
                                                .execute(params)
                                                .get();
                                        if (res.contains("Exception")) {
                                            new GenericDialogBox(
                                                    SignUp.this,
                                                    "Some Error Occured.",
                                                    "Alert",
                                                    "Alert");
                                        } else if (res
                                                .contains("Sorry, You account session is already active.Please contact administrator to restore your session.")
                                                || res.contains("Sorry your email is not verified!.")
                                                || res.contains("Invalid username and/or password!.")) {
                                            new GenericDialogBox(
                                                    SignUp.this,
                                                    res, "",
                                                    "Alert");
                                        } else {
                                            if (StringValidator
                                                    .isJSONValid(res)) {
                                                MainActivity.currentSession.data = new UserData();
                                                JSONObject jsonObj = new JSONObject(
                                                        res);
                                                MainActivity.currentSession.data
                                                        .setFirst_name(jsonObj
                                                                .getString("first_name"));
                                                MainActivity.currentSession.data
                                                        .setLast_name(jsonObj
                                                                .getString("last_name"));
                                                MainActivity.currentSession.data
                                                        .setUpload_picture(jsonObj
                                                                .getString("upload_picture"));
                                                MainActivity.currentSession.data
                                                        .setUserid(jsonObj
                                                                .getString("id"));
                                                MainActivity.currentSession.data
                                                        .setEmail(jsonObj
                                                                .getString("email"));
                                                MainActivity.currentSession.isFacebookLogin = false;
                                                MainActivity.currentSession.isGoogleLogin = false;
                                            }

                                            Intent i = new Intent(
                                                    "android.intent.action.Explore");
                                            startActivity(i);
                                        }

                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    } catch (ExecutionException e1) {
                                        e1.printStackTrace();
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (res
                                        .contains("Username already exists")) {
                                    new GenericDialogBox(SignUp.this,
                                            "Username already exists.", "",
                                            "Failure");
                                } else if (res.contains("Email already exists")) {
                                    new GenericDialogBox(SignUp.this,
                                            "Email already exists.", "",
                                            "Failure");
                                } else {
                                    new GenericDialogBox(SignUp.this,
                                            "Some Error Occured", "", "Alert!");
                                }
                                bar.dismiss("");
                            } catch (InterruptedException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (ExecutionException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    };
                    handler.postDelayed(runnable, 400);
                }
            }
        });
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ACB3")));
        int actionBarTitleId = Resources.getSystem().getIdentifier(
                "action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
                title.setText("Radio Ayah");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
