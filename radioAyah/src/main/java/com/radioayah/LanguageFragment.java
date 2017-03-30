package com.radioayah;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cybus.radioayah.R;

public class LanguageFragment extends Fragment {
    Context context;
    FragmentManager mng;
    Bundle b = null;
    RadioButton rb,arb;
    Button dismiss;
    String title_value;

    public LanguageFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        mng = activity.getFragmentManager();
        b = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.empty_language, container, false);
        ((Explore) context).setActionBarTitle("Change Language");
        showdialog();
        return rootView;
    }
    public void showdialog()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.lang_change, null);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Select Language");

        final AlertDialog b = dialogBuilder.create();
        b.show();

        SharedPreferences pref = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        RadioGroup radioGroup=(RadioGroup) dialogView.findViewById(R.id.lang_group);
        dismiss = (Button) dialogView.findViewById(R.id.dismiss);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (Build.VERSION.SDK_INT > 20) {
                    switch (checkedId){
                        case R.id.english:
                            rb=(RadioButton)dialogView.findViewById(R.id.english);
                            //  title_value=rb.getText().toString();
                            editor.putString("user_id", "eng");
                            editor.apply();
                            Intent intent = new Intent(getActivity(),MainActivity.class);
                            b.dismiss();
                            startActivity(intent);
                            getActivity().finish();

                            break;
                        case R.id.persian:
                            arb=(RadioButton) dialogView.findViewById(R.id.persian);
                            //    title_value=arb.getText().toString();
                            editor.putString("user_id", "fas");
                            editor.apply();
                            b.dismiss();
                            Intent intent1 = new Intent(getActivity(),MainActivity.class);
                            startActivity(intent1);
                            getActivity().finish();
                            break;
                    }
                }
                else
                {
                    Toast.makeText(context, "Language change not supported on your device", Toast.LENGTH_SHORT).show();
                }

            }
        });


        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = null;
                f = new ExploreFragment();
                b.dismiss();
                mng.beginTransaction().replace(R.id.content_frame, f)
                        .commit();

            }
        });
    }


    }
