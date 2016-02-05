/*
 * Copyright (c) 2016. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;

public class ThicknessTable {

    private static final String LOG_TAG = "ThicknessTable: ";

    private final String pref = App.getResourceString(R.string.preference_);
    private final String materialThickness = App.getResourceString(R.string.material_thickness);
    private final String nameColumnHeader = "name_metric";
    private final String valueColumnHeader = "value_in_mm";

    private int    currentKey = -1;
    private int    thicknessKey[]   = null;
    public  String thicknessName[]  = null;
    public  double thicknessValue[] = null;

    public ThicknessTable(){
        currentKey = getCurrentThicknessKey();
        loadStringTables();
    }

    private void loadStringTables() {
        String filter = valueColumnHeader + " > 0.0";
        Log.d(LOG_TAG, "table name is " + materialThickness + "; filter string is " + filter + ";");
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        Cursor cursor = db.query(materialThickness, null, filter, null, null, null, valueColumnHeader);
        int tablesLength = cursor.getCount();
        if( tablesLength > 0 ){
            int keyIndex   = cursor.getColumnIndex(App.getResourceString(R.string.key_field));
            int nameIndex  = cursor.getColumnIndex(nameColumnHeader);
            int valueIndex = cursor.getColumnIndex(valueColumnHeader);
            thicknessName = new String[tablesLength];
            thicknessValue = new double[tablesLength];
            if (cursor.moveToFirst()) {
                for(int i=0; i<tablesLength; i++){
                    thicknessKey[i]   = cursor.getInt(keyIndex);
                    thicknessName[i]  = cursor.getString(nameIndex);
                    thicknessValue[i] = cursor.getDouble(valueIndex);
                    Log.d(LOG_TAG, "record [" + i + "]: " + thicknessName[i] + " = " + thicknessValue[i] + "mm");
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        db.close();
    }

    private int getCurrentThicknessKey(){
        return (new StoredKey(pref + materialThickness)).get();
    }

    public String getCurrentThicknessName(){
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        return DataBaseHelper.getNameByKey(db, materialThickness, getCurrentThicknessKey() );
    }

    public static void  setCurrentThickness(double value){

    }

}
