package com.cassie.sensorapp.sensorapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by cassiancorey on 10/5/16. <-- awww yisss
 */
public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private Drawable[] mThumbIcons;
    private String[] mNames;
    private Integer[] mUIDs;
    private String[] mSndRcv;

    public CustomAdapter(Context c, Drawable[] icons, Integer[] uids, String[] names, String[] sndrcv) {
        mContext = c;
        mNames = names;
        mUIDs = uids;
        mSndRcv = sndrcv;
        mThumbIcons = icons;
    }

    public int getCount() {
        return mThumbIcons.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rowView = inflater.inflate(R.layout.list_item, null);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_img);
        TextView itemName = (TextView) rowView.findViewById(R.id.item_name);
        TextView itemUID = (TextView) rowView.findViewById(R.id.item_uid);
        TextView itemSndRcv = (TextView) rowView.findViewById(R.id.item_snd_rcv);

        imageView.setImageDrawable(mThumbIcons[position]);
        itemName.setText(mNames[position]);
        itemUID.setText(mUIDs[position].toString());
        itemSndRcv.setText(mSndRcv[position]);

        return rowView;
    }

}
