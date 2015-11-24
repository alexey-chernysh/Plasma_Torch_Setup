/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

public class CustomAdapter extends ArrayAdapter {

    private int[] keys_;
    private static final String[] dummyArray = {""};

    public CustomAdapter(@NonNull Context context,
                         int textViewResourceId,
                         String[] objects,
                         int[] keys) {
        super(context, textViewResourceId, (objects == null) ? dummyArray : objects);
        this.keys_ = keys;
    }

    public int getKey(int pos){
        if(keys_ == null) return 0;
        if(pos >= keys_.length) return 0;
        if(pos < 0) return 0;
        return keys_[pos];
    }

    public int getPos(int keySelected) {
        if(keys_ == null) return 0;
        int N = keys_.length;
        if(N <= 0) return 0;
        for(int i=0; i<N; i++)
            if(keys_[i] == keySelected) return i;
        return 0;
    }
}