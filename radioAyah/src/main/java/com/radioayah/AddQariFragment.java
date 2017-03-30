package com.radioayah;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import org.apache.http.message.BasicNameValuePair;

import com.cybus.radioayah.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.radioayah.data.Countries;
import com.radioayah.data.Session;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.DB;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.ProgressDialog;
import com.radioayah.util.StringValidator;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Noor Ahmed on 10/1/2015.
 */
public class AddQariFragment extends Fragment {
    Context context;
    FragmentManager mng;
    ArrayList<Countries> c;
    String[] c_str;
    Uri selectedImageUri = null;
    String imagepath_server = null;
    boolean checked = false;
    private ImageView thumb_img = null;
    final Calendar myCalendar = Calendar.getInstance();
    boolean opened = false;
    boolean opened1 = false;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    Uri filePath;
    boolean values = false;

    public AddQariFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();
        ((Explore) getActivity()).setActionBarTitle("Add Qari");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_qari,
                container, false);
        DB db = new DB(context);
        c = db.getCountriesDB("");
        if (!c.isEmpty()) {
            c_str = new String[c.size()];
            for (int i = 0; i < c.size(); i++) {
                c_str[i] = c.get(i).name;
            }
            Spinner sp = (Spinner) rootView.findViewById(R.id.countries_dropdown);
            ArrayAdapter<String> a = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, c_str);
            a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(a);
        }
        ImageView img = (ImageView) rootView.findViewById(R.id.thumb_profile_pic);
        thumb_img = img;
        Picasso.with(context).load(R.drawable.silhouttee)
                .into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context);
                builder.setMessage(
                        "Do you want to change Qari profile image.")
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

        Button b = (Button) rootView.findViewById(R.id.submit_signup);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                ProgressDialog.showDialog(context);

                        uploadImage(rootView);


            }
        });



        final EditText et =(EditText) rootView.findViewById(R.id.et_qari_dob);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDOB(rootView);
            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                showDOB(rootView);
            }
        });
        final EditText et1 =(EditText) rootView.findViewById(R.id.et_qari_dod);
    et1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDOD(rootView);
        }
    });
        et1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    showDOD(rootView);
            }
        });
        return rootView;
    }
    public void showDOD(final View rootView)
    {
        final EditText et1 = (EditText) rootView.findViewById(R.id.et_qari_dod);
        if(!opened1) {
            opened1 = true;
            new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String myFormat = "yyyy-MM-dd"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    et1.setText(sdf.format(myCalendar.getTime()));
                    opened1 = false;
                }
            }, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }


        opened1=false;


    }
    public void showDOB(final View rootView)
    {
        final EditText et =(EditText) rootView.findViewById(R.id.et_qari_dob);

        if(!opened)
        {
            opened = true;
            new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String myFormat = "yyyy-MM-dd"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    et.setText(sdf.format(myCalendar.getTime()));
                    opened = false;
                }
            }, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }

            opened=false;

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
                thumb_img.setImageBitmap(bitmap);
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
                                .into(thumb_img);
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

    public void uploadImage(final View rootView) {
        if (filePath != null) {

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
                    .findViewById(R.id.et_qari_city);
            String city = et
                    .getText()
                    .toString();
            et = (EditText) rootView
                    .findViewById(R.id.et_qari_dob);
            String dob = et
                    .getText()
                    .toString();
            et = (EditText) rootView
                    .findViewById(R.id.et_qari_dod);
            String dod = et
                    .getText()
                    .toString();
            et = (EditText) rootView
                    .findViewById(R.id.et_qari_link);
            String link = et
                    .getText()
                    .toString();
            et = (EditText) rootView
                    .findViewById(R.id.et_qari_bio);
            final Button submit = (Button) rootView.findViewById(R.id.submit_signup);
            String bio = et
                    .getText()
                    .toString();

                if (StringValidator.lengthValidator(context, fname, 0, 100, "First Name")
                        && StringValidator.lengthValidator(context, lname, 0, 100, "Last Name")
                        && StringValidator.lengthValidator(context, city, 0, 100, "Qari City")
                        && StringValidator.lengthValidator(context, dob, 0, 100, "Qari Date of Birth")
                        && StringValidator.validateDateYMD(dob, context, "Date of Birth")
                        && StringValidator.lengthValidator(context, link, 0, 300, "Qari Profile Link")
                        && StringValidator.lengthValidator(context, bio, 0, 700, "Qari Biography")
                        ) {

                    if (country.equals("-1")) {
                        new GenericDialogBox(context, "Please Select A Country.", "", "Alert!");
                        return;
                    }

                    Bitmap bmp = null;
                    if (Build.VERSION.SDK_INT < 19) {
                        submit.setClickable(false);
                        submit.setEnabled(false);
                        String selectedImagePath = getPath(filePath);
                        bmp = BitmapFactory
                                .decodeFile(selectedImagePath);
                        bmp = Bitmap.createScaledBitmap(bmp, 640, 640,
                                false);
                    } else {
                        ParcelFileDescriptor parcelFileDescriptor;
                        try {
                            submit.setClickable(false);
                            submit.setEnabled(false);
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
                        Ion.with(context)
                                .load(Session.base_url
                                        + "uploadfile/1")
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
                                                if (result.equals("-1")) {
                                                    imagepath_server = null;
                                                    Toast.makeText(
                                                            context,
                                                            "Qari Image Not Updated.",
                                                            Toast.LENGTH_LONG)
                                                            .show();
                                                } else {




                                                    imagepath_server = result;
                                                    Toast.makeText(
                                                            context,
                                                            "Qari Added !.",
                                                            Toast.LENGTH_LONG)
                                                            .show();

                                                    final Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            updateData(rootView);
                                                        }
                                                    }, 2000);



                                                }

                                            }
                                        });


                    }

                }

                } else {

            new GenericDialogBox(context, "Please add a qari image before saving.", "", "Alert!");
            ProgressDialog.dismissDialog();


            }



        }


    public void updateData(View rootView) {


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
                .findViewById(R.id.et_qari_city);
        String city = et
                .getText()
                .toString();
        et = (EditText) rootView
                .findViewById(R.id.et_qari_dob);
        String dob = et
                .getText()
                .toString();
        et = (EditText) rootView
                .findViewById(R.id.et_qari_dod);
        String dod = et
                .getText()
                .toString();
        et = (EditText) rootView
                .findViewById(R.id.et_qari_link);
        String link = et
                .getText()
                .toString();
        et = (EditText) rootView
                .findViewById(R.id.et_qari_bio);
        String bio = et
                .getText()
                .toString();

            if (StringValidator.lengthValidator(context, fname, 0, 100, "First Name")
                    && StringValidator.lengthValidator(context, lname, 0, 100, "Last Name")
                    && StringValidator.lengthValidator(context, city, 0, 100, "Qari City")
                    && StringValidator.lengthValidator(context, dob, 0, 100, "Qari Date of Birth")
                    && StringValidator.validateDateYMD(dob, context, "Date of Birth")
                    && StringValidator.lengthValidator(context, link, 0, 100, "Qari Profile Link")
                    && StringValidator.lengthValidator(context, bio, 0, 100, "Qari Biography")
                    ) {

                if (country.equals("-1")) {
                    new GenericDialogBox(context, "Please Select A Country.", "", "Alert!");
                    return;
                }

                {


                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair(
                            "lname",
                            lname));
                    params.add(new BasicNameValuePair(
                            "fname",
                            fname));
                    params.add(new BasicNameValuePair(
                            "country",
                            country));
                    params.add(new BasicNameValuePair(
                            "link",
                            link));
                    params.add(new BasicNameValuePair(
                            "dob",
                            dob));
                    params.add(new BasicNameValuePair(
                            "dod",
                            dod));
                    params.add(new BasicNameValuePair(
                            "city",
                            city));
                    params.add(new BasicNameValuePair(
                            "bio",
                            bio));
                    if (imagepath_server == null) {
                        params.add(new BasicNameValuePair(
                                "image",
                                "default.gif"));
                    } else {
                        params.add(new BasicNameValuePair(
                                "image",
                                imagepath_server));
                    }
                    ASyncRequest o = new ASyncRequest(
                            context,
                            "addArtistEntry");
                    try {
                        o.execute(params).get();
                        mng.popBackStack();

                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    } catch (ExecutionException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                new GenericDialogBox(context, "Qari info missing.", "", "Alert!");
            }
            ProgressDialog.dismissDialog();


    }
}
