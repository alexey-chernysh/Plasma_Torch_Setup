/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CustomAdapter extends ArrayAdapter {
    private int[] keys_;

    public CustomAdapter(Context context,
                         int textViewResourceId,
                         String[] objects,
                         int[] keys) {
        super(context, textViewResourceId, objects);
        this.keys_ = keys;
    }

    public int getKey(int pos){
        if(keys_ == null) return 0;
        if(pos >= keys_.length) return 0;
        if(pos < 0) return 0;
        return keys_[pos];
    }
}