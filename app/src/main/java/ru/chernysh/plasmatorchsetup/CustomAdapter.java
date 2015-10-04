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
    private String[] objects;
    private boolean flag = false;

    public CustomAdapter(Context context, int textViewResourceId,
                         String[] objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);
        TextView tv = (TextView) convertView;
        if (flag != false) tv.setText(objects[position]);
        else tv.setText("Please select...");

        return convertView;
    }

    public void setUndefined(){
        flag = false;
    }
    public void setSelected(){ flag = true; }
}