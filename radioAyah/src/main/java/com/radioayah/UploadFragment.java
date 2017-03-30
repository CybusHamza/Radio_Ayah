package com.radioayah;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.builder.Builders;
import com.radioayah.data.Reciters;
import com.radioayah.data.Session;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.CustomProgressDialog;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.RefreshSuggestions;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
import java.util.concurrent.ExecutionException;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("NewApi")
public class UploadFragment extends Fragment {
    static public ArrayList<String> selected = new ArrayList<String>();
    static public ArrayList<String> selected_ayah_to = new ArrayList<String>();
    static public ArrayList<String> selected_ayah_from = new ArrayList<String>();
    private static final int REQUEST_PERMISSION_SETTING = 101;
    public static int record = 0;
    public String post_params = "";
    Uri file = null;
    Context context;
    boolean checked = false;
    String[] qaris;
    Uri selectedImageUri = null;
    String imagepath_server = null;
    String filename_server = "-1";
    ArrayList<Reciters> reciters;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    Uri filePath;
    public static int REQUEST_EXTERNAL_STORAGE =1;
    FragmentManager mng;
    String recorded_file = "AudioTemp/audio_file.m4a";
    private CustomProgressDialog bar;
    private String name = "Upload Track";
    private static final int REQUEST_PERMISSIONS = 20;
    View rootView;
    Boolean isOpen = true;
    Button b1;
    Bitmap bmp = null;
    public UploadFragment() {

    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();
        bar = new CustomProgressDialog(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        ((Explore) context).setActionBarTitle("Upload");
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.select_surah_group);
        RadioButton rb = (RadioButton) rootView.findViewById(R.id.Quran);
        RadioButton rb1 = (RadioButton) rootView.findViewById(R.id.ayah);
        RadioButton rb2= (RadioButton) rootView.findViewById(R.id.surah);
        RadioButton rb3 = (RadioButton) rootView.findViewById(R.id.juzz);


        ASyncRequest obj = new ASyncRequest(context, "getQarisForUpload");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        try {
            String response = obj.execute(params).get();
            reciters = new ArrayList<Reciters>();
            if (response.contains("Exception")) {

            } else {
                if (StringValidator.isJSONValid(response)) {
                    JSONArray arr = new JSONArray(response);
                    qaris = new String[arr.length() + 1];
                    qaris[0] = "Select Qari";
                    reciters.add(new Reciters());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        Reciters r = new Reciters();
                        r.setFname(o.getString("fname"));
                        r.setId(o.getString("id"));
                        r.setLname(o.getString("lname"));
                        qaris[i + 1] = o.getString("fname") + " " + o.getString("lname");
                        reciters.add(r);
                    }
                    Spinner sp = (Spinner) rootView.findViewById(R.id.select_qari_to_upload);
                    ArrayAdapter<String> a = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, qaris);
                    a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(a);
                    bar.dismiss("");
                    RadioGroup r = (RadioGroup) rootView.findViewById(R.id.select_surah_group);
                    r.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(RadioGroup group,
                                                     int checkedId) {
                            // TODO Auto-generated method stub
                            switch (checkedId) {
                                case R.id.juzz: {

                                        int i = selected.size();
                                        for (int j = 0; j < i; j++) {
                                            selected.remove(0);
                                        }
                                        i = selected_ayah_from.size();
                                        for (int j = 0; j < i; j++) {
                                            selected_ayah_from.remove(0);
                                        }
                                        i = selected_ayah_to.size();
                                        for (int j = 0; j < i; j++) {
                                            selected_ayah_to.remove(0);
                                        }

                                        new SelectJuzzDialog(context, "parah");

                                    break;
                                }
                                case R.id.surah: {
                                    int i = selected.size();
                                    for (int j = 0; j < i; j++) {
                                        selected.remove(0);
                                    }
                                    i = selected_ayah_from.size();
                                    for (int j = 0; j < i; j++) {
                                        selected_ayah_from.remove(0);
                                    }
                                    i = selected_ayah_to.size();
                                    for (int j = 0; j < i; j++) {
                                        selected_ayah_to.remove(0);
                                    }
                                    new SelectJuzzDialog(context, "surah");
                                    break;
                                }
                                case R.id.ayah: {
                                    int i = selected.size();
                                    for (int j = 0; j < i; j++) {
                                        selected.remove(0);
                                    }
                                    i = selected_ayah_from.size();
                                    for (int j = 0; j < i; j++) {
                                        selected_ayah_from.remove(0);
                                    }
                                    i = selected_ayah_to.size();
                                    for (int j = 0; j < i; j++) {
                                        selected_ayah_to.remove(0);
                                    }
                                    new SelectJuzzDialog(context, "ayah");
                                    break;
                                }
                                default:

                                    break;
                            }
                        }
                    });
                }

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
        Button b = (Button) rootView.findViewById(R.id.select_file_to_upload);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });
        ImageView t_img = (ImageView) rootView.findViewById(R.id.add_qari_button);
        t_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new AddQariFragment();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        b1 = (Button) rootView.findViewById(R.id.upload_track_button);
        b1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (ActivityCompat.checkSelfPermission(getActivity(),  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                    if (Build.VERSION.SDK_INT > 22) {

                        requestPermissions(new String[]{Manifest.permission
                                        .READ_EXTERNAL_STORAGE},
                                         REQUEST_PERMISSIONS);
                      /*  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);*/
                        // Toast.makeText(MainActivity.this.getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();

                    }

                }

                else
                {

                    upload();

                }



            }

        });



        b = (Button) rootView.findViewById(R.id.record_file_to_upload);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Fragment f = new RecordingFragment();
                mng.beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            }
        });
        return rootView;
    }



    public void requestReadPhoneStatePermission() {
      //  ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_INTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
    }


    public void upload()
    {

        Spinner sp = (Spinner) rootView.findViewById(R.id.select_qari_to_upload);
        if (sp.getSelectedItemPosition() == 0) {
            new GenericDialogBox(context, "Please Selece A Qari", "", "Alert!");
            return;
        }
        if (file != null || record == 1) {

            // new Upload().execute(path);
            final ProgressBar bar = (ProgressBar) rootView.findViewById(R.id.upload_progress);
            bar.setVisibility(View.VISIBLE);
            b1.setEnabled(false);
            File f;
            if (record == 1) {
                f = new File(Environment.getExternalStorageDirectory(), recorded_file);
            } else {
                String path = getPath(context, file);
                f = new File(path);
            }
            Ion.with(context).load(Session.base_url + "uploadfile/2").uploadProgressBar(bar)
                    .uploadProgressHandler(new ProgressCallback() {

                        @Override
                        public void onProgress(long downloaded,
                                               long total) {
                            // TODO Auto-generated method stub
                            int progress = (int) (downloaded / total * 100);
                            bar.setProgress(progress);
                        }
                    }).setMultipartFile("uploadedfile", f).asString()
                    .setCallback(new FutureCallback<String>() {

                        @Override
                        public void onCompleted(Exception e,
                                                String result) {
                            // TODO Auto-generated method stub

                            if (result.contains("-1")) {
                                filename_server = "-1";
                                bar.setVisibility(View.GONE);
                                Toast.makeText(context, "Error Uploading File", Toast.LENGTH_LONG).show();
                            }
                            else {

                                Toast.makeText(context, "File Uploaded", Toast.LENGTH_LONG).show();
                                bar.setVisibility(View.GONE);
                                filename_server = result;


                                            String type = "", title = "", qari_id = "", desc = "", tags = "", surahs = "", download, atype = "2", ayahto = "", ayahfrom = "";
                                            CheckBox ck = (CheckBox) rootView.findViewById(R.id.is_downloadable);
                                            if (ck.isChecked()) {
                                                download = "down";
                                            } else {
                                                download = "1";
                                            }
                                            EditText et = (EditText) rootView.findViewById(R.id.et_file_name);
                                            title = et.getText().toString();
                                            et = (EditText) rootView.findViewById(R.id.et_upload_file_description);
                                            desc = et.getText().toString();
                                            et = (EditText) rootView.findViewById(R.id.et_upload_file_tags);
                                            tags = et.getText().toString();
                                            Spinner sp = (Spinner) rootView.findViewById(R.id.select_qari_to_upload);
                                            qari_id = reciters.get(sp.getSelectedItemPosition()).getId();

                                            RadioButton rb = (RadioButton) rootView.findViewById(R.id.Quran);
                                            if (rb.isChecked()) {
                                                type = "3";
                                            }
                                            rb = (RadioButton) rootView.findViewById(R.id.ayah);
                                            if (rb.isChecked()) {
                                                type = "6";
                                            }
                                            rb = (RadioButton) rootView.findViewById(R.id.surah);
                                            if (rb.isChecked()) {
                                                type = "1";
                                            }
                                            rb = (RadioButton) rootView.findViewById(R.id.juzz);
                                            if (rb.isChecked()) {
                                                type = "4";
                                            }
                                            if (!type.equals("3") && selected.size() == 0) {
                                                new GenericDialogBox(context, "Please Select Verse/Surah/Juz for the uploaded file.", "", "Alert");
                                                b1.setEnabled(true);
                                                return;
                                            }
                                            if (!type.equals("3")) {
                                                StringBuilder sb = new StringBuilder();
                                                StringBuilder sb1 = new StringBuilder();
                                                StringBuilder sb2 = new StringBuilder();
                                                for (int i = 0; i < selected.size(); i++) {
                                                    if (i == selected.size() - 1) {
                                                        if (type.equals("6")) {
                                                            sb1.append(selected_ayah_from.get(i));
                                                            sb2.append(selected_ayah_to.get(i));
                                                        }
                                                        sb.append(selected.get(i));

                                                    } else {
                                                        if (type.equals("6")) {
                                                            sb1.append(selected_ayah_from.get(i)).append(",");
                                                            sb2.append(selected_ayah_to.get(i)).append(",");
                                                        }

                                                        sb.append(selected.get(i)).append(",");
                                                    }
                                                }
                                                int size = UploadFragment.selected.size();
                                                for (int i = 0; i < size; i++) {
                                                    selected.remove(0);
                                                    if (type.equals("6")) {
                                                        selected_ayah_from.remove(0);
                                                        selected_ayah_to.remove(0);
                                                    }
                                                }
                                                surahs = sb.toString();
                                                ayahfrom = sb1.toString();
                                                ayahto = sb2.toString();
                                            }

                                            if (!StringValidator.lengthValidator(context, title, 0,500, "Title")) {
                                                b1.setEnabled(true);
                                                return;
                                            }
                                            if (!StringValidator.lengthValidator(context, desc, 0, 500, "Description")) {
                                                b1.setEnabled(true);
                                                return;
                                            }
                                            if (!StringValidator.lengthValidator(context, tags, 0, 100, "Tags")) {
                                                b1.setEnabled(true);
                                                return;
                                            }
                                            ASyncRequest obj = new ASyncRequest(context, "addTrackEntry");
                                            try {
                                                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                                params.add(new BasicNameValuePair("type", type));
                                                params.add(new BasicNameValuePair("title", title));
                                                params.add(new BasicNameValuePair("qari", qari_id));
                                                params.add(new BasicNameValuePair("tags", tags));
                                                params.add(new BasicNameValuePair("desc", desc));
                                                params.add(new BasicNameValuePair("downloadable", download));
                                                params.add(new BasicNameValuePair("path", filename_server));
                                                if (selectedImageUri != null && imagepath_server != null && !imagepath_server.isEmpty()) {
                                                    params.add(new BasicNameValuePair("image", imagepath_server));
                                                } else {
                                                    params.add(new BasicNameValuePair("image", "default.gif"));
                                                }
                                                params.add(new BasicNameValuePair("duration", "00:11:00"));
                                                params.add(new BasicNameValuePair("surah", surahs));
                                                if (type.equals("6")) {
                                                    params.add(new BasicNameValuePair("atype", atype));
                                                    params.add(new BasicNameValuePair("ayah_from", ayahfrom));
                                                    params.add(new BasicNameValuePair("ayah_to", ayahto));
                                                }
                                                String res = obj.execute(params).get();
                                                if (res.equals("1")) {
                                                    new GenericDialogBox(context, "Uploaded File Saved.", "", "Alert");
                                                    mng.popBackStack();
                                                } else {
                                                    new GenericDialogBox(context, "There was an error saving file.", "", "Alert");
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

                                        // //////////////////////////////


                            }

                    });

        } else {
            new GenericDialogBox(context, "Please Select A File to Upload", "", "Alert");
            b1.setEnabled(true);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                upload();
            }

        }


        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {

                file = data.getData();
                Toast.makeText(context, "File selected", Toast.LENGTH_LONG).show();
                if (filePath == null) {
                    checked = true;
                 /*   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to upload an image for your track? Default Image will be added to your track.").setTitle("Alert");
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    openGallery();
                                }
                            });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    filePath = null;
                                }
                            });
                    builder.create();
                    builder.show();*/
                }

            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && data != null &&
                data.getData() != null) {
                 filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {




        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                upload();
            } else {


                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private File getOutputMediaFile(int type) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(type));
        File file = null;
        try {
            String fileName = Uri.parse(timeStamp).getLastPathSegment();
            file = File.createTempFile(fileName, ".jpg", context.getExternalCacheDir());
        } catch (IOException e) {
            new GenericDialogBox(context, "There was an error initializing camera", "", "Error");
        }
        return file;
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public boolean uploadImage(final ProgressBar bar) {
        if (selectedImageUri != null) {
            bar.setVisibility(View.VISIBLE);
            Bitmap bmp = null;
            if (Build.VERSION.SDK_INT < 19) {
                String selectedImagePath = getPath(selectedImageUri);
                bmp = BitmapFactory.decodeFile(selectedImagePath);
                bmp = Bitmap.createScaledBitmap(bmp, 640, 640, false);
            } else {
                ParcelFileDescriptor parcelFileDescriptor;
                try {
                    parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(selectedImageUri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                    bmp = Bitmap.createScaledBitmap(image, 640, 640, false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (bmp != null) {
                ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bmpStream);
                byte[] byte_arr = bmpStream.toByteArray();
                FileOutputStream outputStream;
                File f = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
                    String date = sdf.format(new Date());
                    f = new File(context.getCacheDir(), date + ".jpg");
                    outputStream = new FileOutputStream(f);
                    outputStream.write(byte_arr);
                    outputStream.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                Ion.with(this).load(Session.base_url + "uploadfile/1").uploadProgressBar(bar)
                        .uploadProgressHandler(new ProgressCallback() {

                            @Override
                            public void onProgress(long downloaded, long total) {
                                // TODO Auto-generated method stub
                                int progress = (int) (downloaded / total * 100);
                                bar.setProgress(progress);
                            }
                        }).setMultipartFile("uploadedfile", f).asString()
                        .setCallback(new FutureCallback<String>() {

                            @Override
                            public void onCompleted(Exception e, String result) {
                                // TODO Auto-generated method stub

                                if (result.equals("-1")) {
                                    imagepath_server = null;

                                } else {
                                    imagepath_server = result;

                                }
                            }
                        });
            }
        }
        return false;
    }
}
