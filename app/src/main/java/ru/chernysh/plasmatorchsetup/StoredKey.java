/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Sales on 24.09.2015.
 */

public class StoredKey {

    private final static String LOG_TAG = "Selection field";

    private String requestTag;
    private final static String prefix = "last_selection_in_";
    private final static String suffix = "_table";

    public StoredKey(@NonNull String name){
        if(name == null) throw new SQLiteException("Call SelectionField constructor with NULL field name. Real table name needed.");
        if(name.length() <= 0) throw new SQLiteException("Call SelectionField constructor with NULL field name. Real table name needed.");
        requestTag = prefix + name + suffix;
    }

    public void set(int newKey){
        SharedPreferences sPref = App.getInstance().getSharedPreferences(null, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(requestTag, new Integer(newKey).toString());
        ed.commit();
        Log.d(LOG_TAG, "Value " + newKey + " is saved in " + requestTag + " field in shared preferences");
    }

    public int get(){
        SharedPreferences sPref = App.getInstance().getSharedPreferences(null, Context.MODE_PRIVATE);
        String savedText = sPref.getString(requestTag, "");
        Log.d(LOG_TAG, "Value " + savedText + " is loaded from " + requestTag + " field in shared preferences");
        if(savedText.length() <= 0) return 0;
        return new Integer(savedText);
    }

}
