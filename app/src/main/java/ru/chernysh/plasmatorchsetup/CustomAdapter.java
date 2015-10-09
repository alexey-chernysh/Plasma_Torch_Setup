/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by alexey on 25.09.15.
 */
public class CustomAdapter extends ArrayAdapter {
    private Context context;
    private int textViewResourceId;
    private String[] objects_;
    private int[] keys_;
    private boolean selected = false;

    public CustomAdapter(Context context, int textViewResourceId,
                         String[] objects,
                         int[] keys) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects_ = objects;
        this.keys_ = keys;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);
        TextView tv = (TextView) convertView;
        if (selected) tv.setText(objects_[position]);
        else tv.setText("Please select...");

        return convertView;
    }

    public void setUndefined(){
        selected = false;
    }
    public void setSelected(){ selected = true; }

    public int getKey(int pos){
        if(keys_ == null) return 0;
        if(pos >= keys_.length) return 0;
        if(pos < 0) return 0;
        return keys_[pos];
    }
}