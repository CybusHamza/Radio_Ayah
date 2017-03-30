package com.radioayah;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import com.cybus.radioayah.R;
import com.radioayah.adapters.AddToPlaylistAdapter;
import com.radioayah.data.Playlist;

import java.util.ArrayList;

public class AddToPlaylistDialog extends Activity {
    public static Bundle c;
    final Dialog dialog;

    public AddToPlaylistDialog(final Context context,
                               ArrayList<Playlist> records, String track_id) {
        dialog = new Dialog(context);
        // TODO Auto-generated constructor stub
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle(title);
        dialog.setContentView(R.layout.add_to_playlist_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ListView lv = (ListView) dialog
                .findViewById(R.id.lv_add_to_playlist_playlists);
        AddToPlaylistAdapter adp = new AddToPlaylistAdapter(context, records,
                track_id);
        lv.setAdapter(adp);
        dialog.show();
    }

    public void onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    }
}
