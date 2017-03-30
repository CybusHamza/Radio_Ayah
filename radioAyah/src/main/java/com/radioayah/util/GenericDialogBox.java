package com.radioayah.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.cybus.radioayah.R;
import com.radioayah.AddPlaylistFragment;

public class GenericDialogBox extends Activity {
    public static Bundle c;
    Dialog dialog;
    FragmentManager mng;
    String msg = "";

    public GenericDialogBox(final Context context, String message,
                            final String type, String title) {
        mng = getFragmentManager();
        msg = message;
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (type.equals("registered_sucessfully")) {
                                    Intent i = new Intent(
                                            "android.intent.action.MainActivity");
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i);
                                }



                            }
                        })

                .create().show();
    }

    public void onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    }
}
