package com.radioayah;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.radioayah.data.Session;
import com.radioayah.data.UserData;
import com.radioayah.util.FBLoginAsync;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.GetAllCountries;
import com.radioayah.util.GetAllSurahParah;
import com.radioayah.util.LanguageChanger;
import com.radioayah.util.NetworkChecker;
import com.radioayah.util.ProgressDialog;
import com.radioayah.util.StringValidator;
import com.splunk.mint.Mint;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private static final int RC_SIGN_IN = 0;
    public static Session currentSession;
    TextView forgot_password;
    EditText username1, password1;
    Button singup;
    LoginButton loginButton;
    private CallbackManager callbackManager;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



            FacebookSdk.sdkInitialize(getApplicationContext());

            setContentView(R.layout.activity_main);

            forgot_password = (TextView) findViewById(R.id.forgot_password);
            username1 = (EditText) findViewById(R.id.et_username);
            password1 = (EditText) findViewById(R.id.et_password);
            singup = (Button) findViewById(R.id.register_with_radioayah);


            SpannableString content = new SpannableString(forgot_password.getText().toString());
            content.setSpan(new UnderlineSpan(), 0, forgot_password.getText().toString().length(), 0);
            forgot_password.setText(content);


            Mint.initAndStartSession(this, "e99de8f6");
        SharedPreferences settings = getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
        String logged_in = settings.getString("logged_in",null);
        if (logged_in != null && !(logged_in.equals("false")))
        {

            String f_name,l_name,PP,id,email;

            f_name = settings.getString("first_name","");
            l_name = settings.getString("last_name","");
            PP = settings.getString("upload_picture","");
            id = settings.getString("id","");
            email = settings.getString("email","");

            currentSession = new Session();
            currentSession.data = new UserData();
            currentSession.data.setFirst_name(f_name);
            currentSession.data.setLast_name(l_name);
            currentSession.data.setUpload_picture(PP);
            currentSession.data.setUserid(id);
            currentSession.data.setEmail(email);

             if(logged_in.equals("facebook"))
            {
                MainActivity.currentSession.isFacebookLogin = true;
            }
            else if(logged_in.equals("google"))
            {
                MainActivity.currentSession.isGoogleLogin = true;

            }

            Intent intent= new Intent(MainActivity.this,Explore.class);
            finish();
            startActivity(intent);


        }
            //LoginManager.getInstance().logOut();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult result) {
                            // TODO Auto-generated method stub
                            ProgressDialog.showDialog(MainActivity.this);

                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    result.getAccessToken();

                                    GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(),
                                            new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(
                                                        JSONObject object,
                                                        GraphResponse response) {
                                                    // Application code
                                                    try {
                                                        ProgressDialog.showDialog(MainActivity.this);
                                                        JSONObject obj = response.getJSONObject();
                                                        FBLoginAsync o = new FBLoginAsync(MainActivity.this, "fb_login");
                                                        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                                        params.add(new BasicNameValuePair("user_id", obj.getString("id")));
                                                        if (!obj.isNull("email"))
                                                            params.add(new BasicNameValuePair("email", obj.getString("email")));
                                                        else
                                                            params.add(new BasicNameValuePair("email", obj.getString("id") + "@facebook.com"));
                                                        params.add(new BasicNameValuePair("first_name", obj.getString("name")));
                                                        String res12 = o.execute(params).get();
                                                        StringTokenizer st = new StringTokenizer(res12, "%");
                                                        String username = "", password = "";
                                                        if (st.hasMoreTokens()) {
                                                            username = st.nextToken();
                                                        }
                                                        if (st.hasMoreTokens()) {
                                                            password = st.nextToken();
                                                        }
                                                        currentSession = new Session();
                                                        params = new ArrayList<BasicNameValuePair>();
                                                        params.add(new BasicNameValuePair("user_name", username));
                                                        params.add(new BasicNameValuePair("password", password));
                                                        params.add(new BasicNameValuePair("remember_me", "1"));
                                                        try {
                                                            String res = new Login(MainActivity.this, "loginProcess1/").execute(params).get();
                                                            if (res.contains("Exception")) {
                                                                new GenericDialogBox(MainActivity.this, "Some Error Occured.", "Alert", "Alert");
                                                            } else if (res.contains("Sorry, You account session is already active.Please contact administrator to restore your session.")
                                                                    || res.contains("Sorry your email is not verified!.")
                                                                    || res.contains("Invalid username and/or password!.")) {
                                                                new GenericDialogBox(MainActivity.this, res, "", "Alert");
                                                            } else {
                                                                if (StringValidator.isJSONValid(res)) {
                                                                    currentSession.data = new UserData();
                                                                    JSONObject jsonObj = new JSONObject(res);
                                                                    currentSession.data.setFirst_name(jsonObj.getString("first_name"));
                                                                    currentSession.data.setUserid(jsonObj.getString("id"));
                                                                    currentSession.data.setLast_name(jsonObj.getString("last_name"));
                                                                    currentSession.data.setUpload_picture(jsonObj.getString("upload_picture"));
                                                                    currentSession.data.setUserid(jsonObj.getString("id"));
                                                                    currentSession.data.setEmail(jsonObj.getString("email"));
                                                                    Toast.makeText(MainActivity.this, "Facebook Login Sucessfull", Toast.LENGTH_LONG).show();
                                                                    ProgressDialog.dismissDialog();
                                                                    MainActivity.currentSession.isFacebookLogin = true;

                                                                    SharedPreferences sharedPref = getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                                    editor.putString("token", jsonObj.getString("rememberme_token"));
                                                                    editor.putString("logged_in","facebook");
                                                                    editor.putString("user_name",jsonObj.getString("user_name"));
                                                                    editor.putString("password",jsonObj.getString("password"));
                                                                    editor.putString("id",jsonObj.getString("id"));
                                                                    editor.putString("email", jsonObj.getString("email"));
                                                                    editor.putString("upload_picture", jsonObj.getString("upload_picture"));
                                                                    editor.putString("last_name", jsonObj.getString("last_name"));
                                                                    editor.putString("first_name", jsonObj.getString("first_name"));
                                                                    editor.commit();

                                                                    Intent i = new Intent("android.intent.action.Explore");
                                                                    startActivity(i);
                                                                }
                                                            }
                                                        } catch (InterruptedException e1) {
                                                            // TODO Auto-generated
                                                            // catch
                                                            // block
                                                            e1.printStackTrace();
                                                            ProgressDialog.dismissDialog();
                                                        } catch (ExecutionException e1) {
                                                            // TODO Auto-generated
                                                            // catch
                                                            // block
                                                            e1.printStackTrace();
                                                            ProgressDialog.dismissDialog();
                                                        } catch (JSONException e1) {
                                                            // TODO Auto-generated
                                                            // catch
                                                            // block
                                                            e1.printStackTrace();
                                                            ProgressDialog.dismissDialog();
                                                        }
                                                    } catch (JSONException e) {
                                                        Toast.makeText(MainActivity.this, "Facebook Login Failed", Toast.LENGTH_LONG).show();
                                                        ProgressDialog.dismissDialog();
                                                    } catch (InterruptedException e) {
                                                        // TODO Auto-generated catch
                                                        // block
                                                        e.printStackTrace();
                                                        ProgressDialog.dismissDialog();
                                                    } catch (ExecutionException e) {
                                                        // TODO Auto-generated catch
                                                        // block
                                                        e.printStackTrace();
                                                        ProgressDialog.dismissDialog();
                                                    }
                                                }
                                            });

                                    Bundle parameters = new Bundle();
                                    parameters.putString("fields", "id,name,email,gender, birthday");
                                    request.setParameters(parameters);
                                    request.executeAsync();
                                }
                            };
                            handler.postDelayed(runnable, 400);
                        }

                        @Override
                        public void onCancel() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "Facebook Login Failed.", Toast.LENGTH_LONG).show();
                        }
                    });
            findViewById(R.id.signin).setOnClickListener(this);
            GetAllCountries o = new GetAllCountries(this);
            o.execute();
            GetAllSurahParah o1 = new GetAllSurahParah(this);
            o1.execute();



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


           /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(new Scope(Scopes.PLUS_LOGIN)).addScope(new Scope(Scopes.PLUS_ME)).build();*/


    }


    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                onSignInClicked();
            } else {


                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }



    }

    @SuppressWarnings("deprecation")
    public void login(final View v) {
        final String username = username1.getText().toString();

        final String password = password1.getText().toString();
        if (NetworkChecker.checkStatus(this)
                && StringValidator.lengthValidator(this, username, 0, 25, "Username")
                && StringValidator.lengthValidator(this, password, 0, 25, "Password")) {
            ProgressDialog.showDialog(this);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    currentSession = new Session();
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("user_name", username));
                    params.add(new BasicNameValuePair("password", password));
                    params.add(new BasicNameValuePair("remember_me", "1"));
                    try {
                        @SuppressWarnings("unchecked")
                        String obj = new Login(MainActivity.this, "loginProcess1/").execute(params).get();
                        if (obj.contains("Exception")) {
                            new GenericDialogBox(MainActivity.this, "Some Error Occured.", "Alert", "Alert");
                        } else if (obj
                                .contains("Sorry, You account session is already active.Please contact administrator to restore your session.")
                                || obj.contains("Sorry your email is not verified!.")
                                || obj.contains("Invalid username and/or password!.")) {
                            new GenericDialogBox(MainActivity.this, obj, "", "Alert");
                        }
                        else if (obj
                                .contains("Your account has been rejected, Please contact administration")){
                            new GenericDialogBox(MainActivity.this, obj, "", "Alert");
                        }
                        else {
                            if (StringValidator.isJSONValid(obj)) {
                                currentSession.data = new UserData();
                                JSONObject jsonObj = new JSONObject(obj);
                                currentSession.data.setFirst_name(jsonObj.getString("first_name"));
                                currentSession.data.setLast_name(jsonObj.getString("last_name"));
                                currentSession.data.setUpload_picture(jsonObj.getString("upload_picture"));
                                currentSession.data.setUserid(jsonObj.getString("id"));
                                currentSession.data.setEmail(jsonObj.getString("email"));

                                SharedPreferences sharedPref = getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("token", jsonObj.getString("rememberme_token"));
                                editor.putString("logged_in","true");
                                editor.putString("password",jsonObj.getString("password"));
                                editor.putString("user_name",jsonObj.getString("user_name"));
                                editor.putString("id",jsonObj.getString("id"));
                                editor.putString("email", jsonObj.getString("email"));
                                editor.putString("upload_picture", jsonObj.getString("upload_picture"));
                                editor.putString("last_name", jsonObj.getString("last_name"));
                                editor.putString("first_name", jsonObj.getString("first_name"));

                                editor.commit();
                                Intent i = new Intent("android.intent.action.Explore");
                                startActivity(i);
                            }
                        }
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (ExecutionException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    ProgressDialog.dismissDialog();
                }
            };
            handler.postDelayed(runnable, 400);

        } else if (!NetworkChecker.checkStatus(this)) {
            new GenericDialogBox(this, "No Internet Connection", "", "Alert");
        }
    }

    public void showDialog(View v) {
        new ForgotPasswordDialog(MainActivity.this);
    }

    public void radioAyahSignup(View v) {
        Intent i = new Intent("android.intent.action.SignUp");
        startActivity(i);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aa", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);


        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }



        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission

                onSignInClicked();
            }

        }


     /*   if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();



        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {

                    handleSignInResult(googleSignInResult);
                }
            });
        }


    }





    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d("", "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e("", "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //    showSignedOutUI();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signin) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this,  android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {


                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission
                                        .GET_ACCOUNTS},
                                REQUEST_PERMISSIONS);


                      /*  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);*/
                       // Toast.makeText(MainActivity.this.getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();


            }

            else
            {

                    onSignInClicked();

            }


        }
    }



    public void showErrorDialog(ConnectionResult result) {

    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
      /*  mShouldResolve = true;
        mGoogleApiClient.connect();*/


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        // Show a message to the user that we are signing in.
        //	mStatusTextView.setText(R.string.signing_in);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d("", "onConnected:" + bundle);
        mShouldResolve = false;


        // Show the signed-in UI

           // showSignedInUI();









    }


    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personPhotoUrl = null;

            assert acct != null;
            String personName = acct.getDisplayName();
            if(acct.getPhotoUrl() != null)
            {
                personPhotoUrl = acct.getPhotoUrl().toString();
            }

            String email = acct.getEmail();
            String id= acct.getId();

            FBLoginAsync o = new FBLoginAsync(MainActivity.this, "fb_login");
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_id", id));
            params.add(new BasicNameValuePair("email",email));
            params.add(new BasicNameValuePair("first_name", personName));
            params.add(new BasicNameValuePair("pp", personPhotoUrl));
            String res12 = null;
            try {
                res12 = o.execute(params).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            StringTokenizer st = new StringTokenizer(res12, "%");
            String username = "", password = "";
            if (st.hasMoreTokens()) {
                username = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                password = st.nextToken();
            }
            currentSession = new Session();
            params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_name", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("remember_me", "1"));
            try {
                String res = new Login(MainActivity.this, "loginProcess1/").execute(params).get();
                if (res.contains("Exception")) {
                    new GenericDialogBox( MainActivity.this, "Some Error Occured.", "Alert", "Alert");
                    ProgressDialog.dismissDialog();
                } else if (res
                        .contains("Sorry, You account session is already active.Please contact administrator to restore your session.")
                        || res.contains("Sorry your email is not verified!.")
                        || res.contains("Invalid username or password!.")) {
                    ProgressDialog.dismissDialog();
                    new GenericDialogBox(MainActivity.this, res, "", "Alert");
                } else {
                    if (StringValidator.isJSONValid(res)) {

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("Google Prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = pref.edit();
                        editor1.putString("is_loggedIn", "true");// Saving string
                        editor1.commit();


                        currentSession.data = new UserData();
                        JSONObject jsonObj = new JSONObject(res);
                        currentSession.data.setFirst_name(jsonObj.getString("first_name"));
                        currentSession.data.setLast_name(jsonObj.getString("last_name"));
                        currentSession.data.setUpload_picture(jsonObj.getString("upload_picture"));
                        currentSession.data.setUserid(jsonObj.getString("id"));
                        currentSession.data.setEmail(jsonObj.getString("email"));
                        currentSession.isGoogleLogin = true;

                        SharedPreferences sharedPref = getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("token", jsonObj.getString("rememberme_token"));
                        editor.putString("logged_in","google");
                        editor.putString("id",jsonObj.getString("id"));
                        editor.putString("user_name",jsonObj.getString("user_name"));
                        editor.putString("password",jsonObj.getString("password"));
                        editor.putString("email", jsonObj.getString("email"));
                        editor.putString("upload_picture", jsonObj.getString("upload_picture"));
                        editor.putString("last_name", jsonObj.getString("last_name"));
                        editor.putString("first_name", jsonObj.getString("first_name"));
                        editor.commit();

                        ProgressDialog.dismissDialog();
                        Intent i = new Intent("android.intent.action.Explore");
                        startActivity(i);
                    }
                }
            } catch (InterruptedException e1) {
                // TODO Auto-generated
                // catch
                // block
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                // TODO Auto-generated
                // catch
                // block
                e1.printStackTrace();
            } catch (JSONException e1) {
                // TODO Auto-generated
                // catch
                // block
                e1.printStackTrace();
            }
        }


         else {
            // Signed out, show unauthenticated UI.

        }
    }


    public void showSignedInUI() {
        try {
            //    Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);

                ProgressDialog.showDialog(this);
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String id = currentPerson.getId();
                FBLoginAsync o = new FBLoginAsync(MainActivity.this, "fb_login");
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("user_id", id));
                params.add(new BasicNameValuePair("email",email));
                params.add(new BasicNameValuePair("first_name", personName));
                params.add(new BasicNameValuePair("pp", personPhotoUrl));
                String res12 = o.execute(params).get();
                StringTokenizer st = new StringTokenizer(res12, "%");
                String username = "", password = "";
                if (st.hasMoreTokens()) {
                    username = st.nextToken();
                }
                if (st.hasMoreTokens()) {
                    password = st.nextToken();
                }
                currentSession = new Session();
                params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("user_name", username));
                params.add(new BasicNameValuePair("password", password));
                try {
                    String res = new Login(MainActivity.this, "loginProcess1/").execute(params).get();
                    if (res.contains("Exception")) {
                        new GenericDialogBox( MainActivity.this, "Some Error Occured.", "Alert", "Alert");
                        ProgressDialog.dismissDialog();
                    } else if (res
                            .contains("Sorry, You account session is already active.Please contact administrator to restore your session.")
                            || res.contains("Sorry your email is not verified!.")
                            || res.contains("Invalid username or password!.")) {
                        ProgressDialog.dismissDialog();
                        new GenericDialogBox(MainActivity.this, res, "", "Alert");
                    } else {
                        if (StringValidator.isJSONValid(res)) {

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("Google Prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = pref.edit();
                            editor1.putString("is_loggedIn", "true");// Saving string
                            editor1.commit();


                            currentSession.data = new UserData();
                            JSONObject jsonObj = new JSONObject(res);
                            currentSession.data.setFirst_name(jsonObj.getString("first_name"));
                            currentSession.data.setLast_name(jsonObj.getString("last_name"));
                            currentSession.data.setUpload_picture(jsonObj.getString("upload_picture"));
                            currentSession.data.setUserid(jsonObj.getString("id"));
                            currentSession.data.setEmail(jsonObj.getString("email"));
                            currentSession.isGoogleLogin = true;

                            SharedPreferences sharedPref = getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("token", jsonObj.getString("rememberme_token"));

                            editor.putString("id",jsonObj.getString("id"));
                            editor.putString("user_name",jsonObj.getString("user_name"));
                            editor.putString("password",jsonObj.getString("password"));
                            editor.putString("email", jsonObj.getString("email"));
                            editor.putString("upload_picture", jsonObj.getString("upload_picture"));
                            editor.putString("last_name", jsonObj.getString("last_name"));
                            editor.putString("first_name", jsonObj.getString("first_name"));
                            editor.commit();

                            ProgressDialog.dismissDialog();
                            Intent i = new Intent("android.intent.action.Explore");
                            startActivity(i);
                        }
                    }
                } catch (InterruptedException e1) {
                    // TODO Auto-generated
                    // catch
                    // block
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    // TODO Auto-generated
                    // catch
                    // block
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    // TODO Auto-generated
                    // catch
                    // block
                    e1.printStackTrace();
                }
            } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

}
