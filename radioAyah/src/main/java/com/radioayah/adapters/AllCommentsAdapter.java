package com.radioayah.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybus.radioayah.R;
import com.radioayah.MainActivity;

import com.radioayah.data.Comments;
import com.radioayah.util.ASyncRequest;
import com.radioayah.util.CircularImageView;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AllCommentsAdapter extends ArrayAdapter<Comments> {
    public Context context;
    ArrayList<Comments> rec;
    boolean showOptions = true;
    FragmentManager mng;
    boolean showdel = false;

    public AllCommentsAdapter(Context context, int textViewResourceId,
                              ArrayList<Comments> records) {
        super(context, textViewResourceId);
        rec = new ArrayList<Comments>();
        rec = records;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rec.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.comment_listview_item, parent,
                    false);
        } else {
            row = convertView;
        }
        final Comments temp = rec.get(position);
        if (temp != null) {
            CircularImageView img = (CircularImageView) row
                    .findViewById(R.id.showuserprofilepicincommentslistview);

            String url = temp.getPicture();

            if(url.startsWith("https://lh3.googleusercontent.com/-") || url.startsWith("https://graph.facebook.com/"))
            {
                url = temp.getPicture();
            }

            else
            {
                url = MainActivity.currentSession.admin_base_url
                    + temp.getPicture();
            }


            if (temp.getPicture().equals("default.gif") && img != null) {
                Picasso.with(context).load(R.drawable.silhouttee)
                        .placeholder(R.drawable.silhouttee).into(img);
            } else if (url != null || !url.isEmpty() || img != null) {
                Picasso.with(context).load(url)
                        .placeholder(R.drawable.silhouttee).into(img);
            }
            if (temp.getFlag().equals("1")) {
                ImageView img1 = (ImageView) row.findViewById(R.id.flag_show);
                img1.setVisibility(View.VISIBLE);
            } else {
                ImageView img1 = (ImageView) row.findViewById(R.id.flag_show);
                img1.setVisibility(View.GONE);
            }
            TextView t = (TextView) row
                    .findViewById(R.id.showCommenttimestamphere);
            String newDateString = temp.getTime();
            /*try {
                java.util.Date date = new SimpleDateFormat(
                        "yyyy-MM-dd").parse(newDateString);
                newDateString = date.toString();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
            String a = temp.getUsername() + " says at " + newDateString;
            t.setText(a);
            t = (TextView) row.findViewById(R.id.showCommentListview);
            t.setText(temp.getComment());
            final View finalRow = row;
            row.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setMessage("Do you want to flag this comment as inappropriate.")
                            .setTitle("Alert");
                    builder.setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    try {
                                        if (!temp.getUser_id().equals(
                                                MainActivity.currentSession.data
                                                        .getUserid())) {
                                            ASyncRequest obj = new ASyncRequest(
                                                    context, "flag/" + temp.getId());
                                            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                                            String res = obj.execute(params).get();
                                            if(res.equals("-1"))
                                            {
                                                Toast.makeText(context,"Comment already flagged.",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(context,
                                                        "Comment Flagged",
                                                        Toast.LENGTH_LONG).show();
                                                ImageView img1 = (ImageView) finalRow.findViewById(R.id.flag_show);
                                                img1.setVisibility(View.VISIBLE);
                                                temp.setFlag("1");
                                            }
                                            notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(
                                                    context,
                                                    "You cannot flag your own comment.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    catch (InterruptedException e) {
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
                    builder.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                }
                            });
                    builder.create();
                    builder.show();

                    return false;
                }
            });
        }
        return row;
    }
}