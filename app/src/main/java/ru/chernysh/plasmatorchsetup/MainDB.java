/*
 * Copyright (c) 2016. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MainDB {

    private static final String LOG_TAG = "Main db singleton: ";

    private static MainDB instance;
    private SQLiteDatabase db;

    private MainDB() {
        Log.d(LOG_TAG, "Opening db...");
        db = (new DataBaseHelper()).getReadableDatabase();
    }

    public static synchronized MainDB getInstance(){
        if(instance == null){
            instance = new MainDB();
        }
        return instance;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public static String getNameColumnHeader(Cursor cursor){
        String neutralNameHeader = App.getResourceString(R.string.name_field);
        String nameHeader = neutralNameHeader
                          + "_"
                          + App.language;
        int nameIndex = cursor.getColumnIndex(nameHeader);
        if(nameIndex >= 0) return nameHeader;
        nameHeader = neutralNameHeader
                + "_"
                + App.getResourceString(R.string.english_language);
        nameIndex = cursor.getColumnIndex(nameHeader);
        if(nameIndex >= 0) return nameHeader;
        nameHeader = neutralNameHeader;
        nameIndex = cursor.getColumnIndex(nameHeader);
        if(nameIndex >= 0) return nameHeader;
        else return null;
    }

    public static String getNameByKey(String tableName, int key) {
        SQLiteDatabase db_ = MainDB.getInstance().getDb();
        Cursor cursor = db_.query(tableName, null, null, null, null, null, null);
        int processNameIndex = cursor.getColumnIndex(getNameColumnHeader(cursor));
        String filter = App.getResourceString(R.string.key_field)
                + App.getResourceString(R.string.is_equal_to)
                + key;
        cursor = db_.query(tableName, null, filter, null, null, null, null);
        String result = null;
        if (cursor.moveToFirst()) result = cursor.getString(processNameIndex);
        cursor.close();
        return result;
    }

}
