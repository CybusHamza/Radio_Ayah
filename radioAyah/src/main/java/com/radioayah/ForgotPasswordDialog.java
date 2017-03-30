package com.radioayah;

/**
 * Created by Noor Ahmed on 9/28/2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.cybus.radioayah.R;
import com.radioayah.util.GenericDialogBox;
import com.radioayah.util.StringValidator;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ForgotPasswordDialog extends Activity {
    public static Bundle c;
    final Dialog dialog;

    public ForgotPasswordDialog(final Context context
    ) {
        dialog = new Dialog(context);
        // TODO Auto-generated constructor stub
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle(title);
        dialog.setContentView(R.layout.forgot_password);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        Button b = (Button) dialog.findViewById(R.id.forgot_password_click);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) dialog.findViewById(R.id.forgot_email);
                String email = et.getText().toString();
                if (StringValidator.lengthValidator(context, email, 0, 100, "Email") && StringValidator.ValidateEmail(context, email)) {
                    SignUpRequest obj = new SignUpRequest(context,
                            "resetEmail/");
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("email_pass", email));
                    try {
                        String res = obj.execute(params).get();
                        if (res.equals("1")) {
                            dialog.dismiss();
                            new GenericDialogBox(context, "An Email has been sent to your email address.", "", "Alert!");
                        } else if (res.equals("2")) {
                            new GenericDialogBox(context, "No email found in database.", "", "Alert!");
                        } else if (res.equals("0")) {
                            new GenericDialogBox(context, "Email not sent.", "", "Alert!");
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.show();
    }

    public void onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    }
}
