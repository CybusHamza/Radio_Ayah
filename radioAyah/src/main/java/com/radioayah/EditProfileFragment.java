package com.radioayah;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.radioayah.adapters.NavDrawerListAdapter;
import com.radioayah.adapters.NavDrawerOnItemClickListener;
import com.radioayah.data.Countries;
import com.radioayah.data.Session;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.DB;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.StringValidator;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class EditProfileFragment extends Fragment {
    Context context;
    FragmentManager mng;
    ArrayList<Countries> c;
    String[] c_str;
    Uri selectedImageUri = null;
    String imagepath_server = null;
    boolean checked = false;
    DrawerLayout d;
    ActionBarDrawerToggle mDrawerToggle;
    ImageView img;
    private int PICK_IMAGE_REQUEST = 1;
    private String[] mMenuTitles;
    private ListView mDrawerList;
    private ImageView thumb_img = null;
    private Bitmap bitmap;
    Uri filePath;
    public EditProfileFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_editprofile,
                container, false);
        ((Explore) context).setActionBarTitle("Edit Profile");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        DB o1 = new DB(context);
        c = o1.getCountriesDB("");
        if (!c.isEmpty()) {
            c_str = new String[c.size()];
            for (int i = 0; i < c.size(); i++) {
                c_str[i] = c.get(i).name;
            }
            Spinner sp = (Spinner) rootView
                    .findViewById(R.id.countries_dropdown);
            ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, c_str);
            a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(a);
        }
        ASyncRequest obj = new ASyncRequest(context, "editUser/"
                + MainActivity.currentSession.data.getUserid());
        try {
            String res = obj.execute(params).get();
            if (res != null && StringValidator.isJSONValid(res)) {
                String fname, lname, email;
                JSONObject o = new JSONObject(res);
                fname = o.getString("first_name");
                lname = o.getString("last_name");
                email = o.getString("email");
                String set_country = o.getString("country_id");
                for (int i = 0; i < c.size(); i++) {
                    if (set_country.equals(c.get(i).getId())) {
                        Spinner sp = (Spinner) rootView
                                .findViewById(R.id.countries_dropdown);
                        sp.setSelection(i);
                    }
                }
                EditText et = (EditText) rootView.findViewById(R.id.et_fname);
                et.setText(fname);
                et = (EditText) rootView.findViewById(R.id.et_lname);
                et.setText(lname);
                et = (EditText) rootView.findViewById(R.id.et_email);
                et.setText(email);
                final Button b = (Button) rootView
                        .findViewById(R.id.submit_signup);
                b.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (filePath != null) {
                            Bitmap bmp = null;
                            if (Build.VERSION.SDK_INT < 19) {
                                String selectedImagePath = getPath(filePath);
                                bmp = BitmapFactory
                                        .decodeFile(selectedImagePath);
                                bmp = Bitmap.createScaledBitmap(bmp, 640, 640,
                                        false);
                            } else {
                                ParcelFileDescriptor parcelFileDescriptor;
                                try {
                                    parcelFileDescriptor = getActivity()
                                            .getContentResolver()
                                            .openFileDescriptor(
                                                    filePath, "r");
                                    FileDescriptor fileDescriptor = parcelFileDescriptor
                                            .getFileDescriptor();
                                    Bitmap image = BitmapFactory
                                            .decodeFileDescriptor(fileDescriptor);
                                    parcelFileDescriptor.close();
                                    bmp = Bitmap.createScaledBitmap(image, 640,
                                            640, false);
                                } catch (FileNotFoundException e1) {
                                    e1.printStackTrace();
                                } catch (IOException e1) {
                                    // TODO
                                    // Auto-generated
                                    // catch block
                                    e1.printStackTrace();
                                }
                            }
                            if (bmp != null) {
                                ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100,
                                        bmpStream);
                                byte[] byte_arr = bmpStream.toByteArray();
                                FileOutputStream outputStream;
                                File f = null;
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat(
                                            "yyMMddHHmmss");
                                    String date = sdf.format(new Date());
                                    f = new File(context.getCacheDir(), date
                                            + ".jpg");
                                    outputStream = new FileOutputStream(f);
                                    outputStream.write(byte_arr);
                                    outputStream.close();
                                } catch (IOException e1) {
                                    // TODO
                                    // Auto-generated
                                    // catch block
                                    e1.printStackTrace();
                                }
                                final ProgressBar pb = (ProgressBar) rootView
                                        .findViewById(R.id.upload_progress);
                                pb.setVisibility(View.VISIBLE);
                                Ion.with(context)
                                        .load(Session.base_url
                                                + "uploadfile/1")
                                        .uploadProgressBar(pb)
                                        .uploadProgressHandler(
                                                new ProgressCallback() {

                                                    @Override
                                                    public void onProgress(
                                                            long downloaded,
                                                            long total) {
                                                    }
                                                })
                                        .setMultipartFile("uploadedfile", f)
                                        .asString()
                                        .setCallback(
                                                new FutureCallback<String>() {

                                                    @Override
                                                    public void onCompleted(
                                                            Exception e,
                                                            String result) {
                                                        pb.setVisibility(View.VISIBLE);
                                                        if (result.equals("-1")) {
                                                            imagepath_server = null;
                                                            Toast.makeText(
                                                                    context,
                                                                    "User Image Not Updated.",
                                                                    Toast.LENGTH_LONG)
                                                                    .show();
                                                        } else {
                                                            imagepath_server = result;

                                                            SharedPreferences sharedPref = context.getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedPref.edit();
                                                            editor.remove("upload_picture");
                                                            editor.putString("upload_picture",imagepath_server);

                                                            editor.commit();


                                                            Toast.makeText(
                                                                    context,
                                                                    "User Image Updated.",
                                                                    Toast.LENGTH_LONG)
                                                                    .show();
                                                        }
                                                        Spinner sp = (Spinner) rootView
                                                                .findViewById(R.id.countries_dropdown);
                                                        String country = c
                                                                .get(sp.getSelectedItemPosition())
                                                                .getId();
                                                        EditText et = (EditText) rootView
                                                                .findViewById(R.id.et_fname);
                                                        String fname = et
                                                                .getText()
                                                                .toString();
                                                        et = (EditText) rootView
                                                                .findViewById(R.id.et_lname);
                                                        String lname = et
                                                                .getText()
                                                                .toString();
                                                        et = (EditText) rootView
                                                                .findViewById(R.id.et_email);
                                                        String email = et
                                                                .getText()
                                                                .toString();
                                                        if (StringValidator
                                                                .lengthValidator(
                                                                        context,
                                                                        fname,
                                                                        3, 50,
                                                                        "First Name")
                                                                && StringValidator
                                                                .lengthValidator(
                                                                        context,
                                                                        lname,
                                                                        3,
                                                                        50,
                                                                        "Last Name")
                                                                && StringValidator
                                                                .lengthValidator(
                                                                        context,
                                                                        email,
                                                                        3,
                                                                        50,
                                                                        "Email Address")
                                                                && StringValidator
                                                                .ValidateEmail(
                                                                        context,
                                                                        email)) {
                                                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                                            params.add(new BasicNameValuePair(
                                                                    "email",
                                                                    email));
                                                            params.add(new BasicNameValuePair(
                                                                    "lname",
                                                                    lname));
                                                            params.add(new BasicNameValuePair(
                                                                    "fname",
                                                                    fname));
                                                            params.add(new BasicNameValuePair(
                                                                    "country",
                                                                    country));
                                                            if (imagepath_server == null) {
                                                                params.add(new BasicNameValuePair(
                                                                        "ppath",
                                                                        MainActivity.currentSession.data
                                                                                .getUpload_picture()));
                                                            } else {
                                                                params.add(new BasicNameValuePair(
                                                                        "ppath",
                                                                        imagepath_server));
                                                            }
                                                            ASyncRequest o = new ASyncRequest(
                                                                    context,
                                                                    "update_client");
                                                            try {
                                                                String res = o
                                                                        .execute(
                                                                                params)
                                                                        .get();
                                                                if (res.equals("Email already exists!")) {
                                                                    new GenericDialogBox(
                                                                            context,
                                                                            "Email already exists. Please type another email address.",
                                                                            "",
                                                                            "Alert!");
                                                                } else {
                                                                    StringTokenizer st = new StringTokenizer(
                                                                            res,
                                                                            "%");
                                                                    if (st.hasMoreTokens()) {
                                                                        MainActivity.currentSession.data
                                                                                .setFirst_name(st
                                                                                        .nextToken());
                                                                    }
                                                                    if (st.hasMoreTokens()) {
                                                                        MainActivity.currentSession.data
                                                                                .setLast_name(st
                                                                                        .nextToken());
                                                                    }
                                                                    if (st.hasMoreTokens()) {
                                                                        MainActivity.currentSession.data
                                                                                .setUpload_picture(st
                                                                                        .nextToken());
                                                                    }
                                                                    Toast.makeText(
                                                                            context,
                                                                            "User Data Updated.",
                                                                            Toast.LENGTH_LONG)
                                                                            .show();
                                                                    d = (DrawerLayout) getActivity()
                                                                            .findViewById(
                                                                                    R.id.drawer_layout);
                                                                    mMenuTitles = getResources()
                                                                            .getStringArray(
                                                                                    R.array.LeftMenuItems);
                                                                    mDrawerList = (ListView) getActivity()
                                                                            .findViewById(
                                                                                    R.id.left_drawer);
                                                                    NavDrawerListAdapter adapter = new NavDrawerListAdapter(
                                                                            context,
                                                                            R.layout.drawer_layout,
                                                                            mMenuTitles);
                                                                    mDrawerList
                                                                            .setOnItemClickListener(new NavDrawerOnItemClickListener(
                                                                                    context,
                                                                                    mng,
                                                                                    d));
                                                                    mDrawerList
                                                                            .invalidate();
                                                                    mDrawerList
                                                                            .setAdapter(adapter);

                                                                    ActionBar bar = getActivity()
                                                                            .getActionBar();
                                                                    bar.setBackgroundDrawable(new ColorDrawable(
                                                                            Color.parseColor("#00ACB3")));

                                                                    int actionBarTitleId = Resources
                                                                            .getSystem()
                                                                            .getIdentifier(
                                                                                    "action_bar_title",
                                                                                    "id",
                                                                                    "android");
                                                                    if (actionBarTitleId > 0) {
                                                                        TextView title = (TextView) getActivity()
                                                                                .findViewById(
                                                                                        actionBarTitleId);
                                                                        if (title != null) {
                                                                            title.setTextColor(Color.WHITE);
                                                                        }
                                                                    }
                                                                    // getActionBar().setDisplayHomeAsUpEnabled(true);
                                                                    getActivity()
                                                                            .getActionBar()
                                                                            .setHomeButtonEnabled(
                                                                                    true);
                                                                    getActivity()
                                                                            .getActionBar()
                                                                            .setIcon(
                                                                                    R.drawable.ic_drawer);

                                                                    mng.popBackStack();
                                                                }
                                                            } catch (InterruptedException e1) {
                                                                e1.printStackTrace();
                                                            } catch (ExecutionException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                });
                            }
                        } else {
                            Spinner sp = (Spinner) rootView
                                    .findViewById(R.id.countries_dropdown);
                            String country = c
                                    .get(sp.getSelectedItemPosition())
                                    .getId();
                            EditText et = (EditText) rootView
                                    .findViewById(R.id.et_fname);
                            String fname = et.getText()
                                    .toString();
                            et = (EditText) rootView
                                    .findViewById(R.id.et_lname);
                            String lname = et.getText()
                                    .toString();
                            et = (EditText) rootView
                                    .findViewById(R.id.et_email);
                            String email = et.getText()
                                    .toString();
                            if (StringValidator
                                    .lengthValidator(context,
                                            fname, 3, 50,
                                            "First Name")
                                    && StringValidator
                                    .lengthValidator(
                                            context,
                                            lname, 3,
                                            50,
                                            "Last Name")
                                    && StringValidator
                                    .lengthValidator(
                                            context,
                                            email, 3,
                                            50,
                                            "Email Address")
                                    && StringValidator
                                    .ValidateEmail(
                                            context,
                                            email)) {
                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                params.add(new BasicNameValuePair(
                                        "email", email));
                                params.add(new BasicNameValuePair(
                                        "lname", lname));
                                params.add(new BasicNameValuePair(
                                        "fname", fname));
                                params.add(new BasicNameValuePair(
                                        "country", country));
                                params.add(new BasicNameValuePair(
                                        "ppath",
                                        MainActivity.currentSession.data
                                                .getUpload_picture()));
                                ASyncRequest o = new ASyncRequest(
                                        context,
                                        "update_client");
                                try {
                                    String res = o.execute(
                                            params).get();
                                    if (res.equals("Email already exists!")) {
                                        new GenericDialogBox(
                                                context,
                                                "Email already exists. Please type another email address.",
                                                "", "Alert!");
                                    } else {
                                        StringTokenizer st = new StringTokenizer(
                                                res, "%");
                                        if (st.hasMoreTokens()) {
                                            MainActivity.currentSession.data
                                                    .setFirst_name(st
                                                            .nextToken());
                                        }
                                        if (st.hasMoreTokens()) {
                                            MainActivity.currentSession.data
                                                    .setLast_name(st
                                                            .nextToken());
                                        }
                                        if (st.hasMoreTokens()) {
                                            MainActivity.currentSession.data
                                                    .setEmail(st
                                                            .nextToken());
                                        }
                                        Toast.makeText(
                                                context,
                                                "User Data Updated.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        SharedPreferences sharedPref = context.getSharedPreferences("RadioAyahPreferences", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();

                                        editor.putString("email",email );
                                        editor.putString("upload_picture",imagepath_server);
                                        editor.putString("last_name",lname);
                                        editor.putString("first_name",fname);

                                        editor.commit();




                                        d = (DrawerLayout) getActivity()
                                                .findViewById(
                                                        R.id.drawer_layout);
                                        mMenuTitles = getResources()
                                                .getStringArray(
                                                        R.array.LeftMenuItems);
                                        mDrawerList = (ListView) getActivity()
                                                .findViewById(
                                                        R.id.left_drawer);
                                        NavDrawerListAdapter adapter = new NavDrawerListAdapter(
                                                context,
                                                R.layout.drawer_layout,
                                                mMenuTitles);
                                        mDrawerList
                                                .setOnItemClickListener(new NavDrawerOnItemClickListener(
                                                        context,
                                                        mng, d));

                                        mDrawerList
                                                .setAdapter(adapter);
                                        ActionBar bar = getActivity()
                                                .getActionBar();
                                        bar.setBackgroundDrawable(new ColorDrawable(
                                                Color.parseColor("#00ACB3")));

                                        int actionBarTitleId = Resources
                                                .getSystem()
                                                .getIdentifier(
                                                        "action_bar_title",
                                                        "id",
                                                        "android");
                                        if (actionBarTitleId > 0) {
                                            TextView title = (TextView) getActivity()
                                                    .findViewById(
                                                            actionBarTitleId);
                                            if (title != null) {
                                                title.setTextColor(Color.WHITE);
                                            }
                                        }
                                        // getActionBar().setDisplayHomeAsUpEnabled(true);
                                        getActivity()
                                                .getActionBar()
                                                .setHomeButtonEnabled(
                                                        true);
                                        getActivity()
                                                .getActionBar()
                                                .setIcon(
                                                        R.drawable.ic_drawer);

                                        mng.popBackStack();
                                    }
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch
                                    // block
                                    e1.printStackTrace();
                                } catch (ExecutionException e1) {
                                    // TODO Auto-generated catch
                                    // block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });

            }
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        img = (ImageView) rootView.findViewById(R.id.thumb_profile_pic);
        thumb_img = img;
        String url = MainActivity.currentSession.admin_base_url
                + MainActivity.currentSession.data.getUpload_picture();
        Picasso.with(context).load(url)
                .placeholder(R.drawable.silhouttee).into(img);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context);
                builder.setMessage(
                        "Do you want to change your profile image.")
                        .setTitle("Alert");
                builder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                if (selectedImageUri == null) {
                                    checked = true;
                                    openGallery();
                                }
                            }
                        });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialog, int id) {
                    }
                });
                builder.create();
                builder.show();
            }
        });
        return rootView;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri,
                projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == PICK_IMAGE_REQUEST && data != null &&
                data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                selectedImageUri = null;
                if (Build.VERSION.SDK_INT < 19) {
                    selectedImageUri = data.getData();
                    selectedImageUri = getOutputMediaFileUri(1);
                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        selectedImageUri = data.getData();
                        parcelFileDescriptor = getActivity()
                                .getContentResolver().openFileDescriptor(
                                        selectedImageUri, "r");
                        parcelFileDescriptor.getFileDescriptor();
                        parcelFileDescriptor.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (selectedImageUri != null) {
                        Picasso.with(context).load(selectedImageUri)
                                .placeholder(R.drawable.silhouttee).into(thumb_img);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date(type));
        File file = null;
        try {
            String fileName = Uri.parse(timeStamp).getLastPathSegment();
            file = File.createTempFile(fileName, ".jpg",
                    context.getExternalCacheDir());
        } catch (IOException e) {
            new GenericDialogBox(context,
                    "There was an error initializing camera", "", "Error");
        }
        return file;
    }
}
