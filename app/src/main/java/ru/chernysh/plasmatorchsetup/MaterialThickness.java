/*
 * Copyright (c) 2016. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;

public class MaterialThickness {

//    private static final String LOG_TAG = "MaterialThickness: ";

    private final String pref = App.getResourceString(R.string.preference_);
    private final String materialThickness = App.getResourceString(R.string.material_thickness);
    private final String nameColumnHeaderMetric = App.getResourceString(R.string.metric_column_name);
    private final String nameColumnHeaderImperial = App.getResourceString(R.string.imperial_column_name);
    private       String currentNameColumnHeader;
    private final String valueColumnHeader = App.getResourceString(R.string.thickness_value_column_name);

    public int    thicknessKey[]   = null;
    public String thicknessName[]  = null;
    public double thicknessValue[] = null;

    private static MaterialThickness ourInstance;

    public static synchronized MaterialThickness getInstance() {
        if(ourInstance == null) ourInstance = new MaterialThickness();
        return ourInstance;
    }

    private MaterialThickness() {
        loadStringTables();
    }

    public void loadStringTables() {
        String filter = valueColumnHeader + " > 0.0";
//        Log.d(LOG_TAG, "table name is " + materialThickness + "; filter string is " + filter + ";");
        SQLiteDatabase db = MainDB.getInstance().getDb();
        Cursor cursor = db.query(materialThickness, null, filter, null, null, null, valueColumnHeader);
        if(MeasurementUnits.getUnitsKey() == 1)
            currentNameColumnHeader = nameColumnHeaderMetric;
        else
            currentNameColumnHeader = nameColumnHeaderImperial;
        int tablesLength = cursor.getCount();
        if( tablesLength > 0 ){
            int keyIndex   = cursor.getColumnIndex(App.getResourceString(R.string.key_field));
            int nameIndex  = cursor.getColumnIndex(currentNameColumnHeader);
            int valueIndex = cursor.getColumnIndex(valueColumnHeader);
            thicknessKey = new int[tablesLength];
            thicknessName = new String[tablesLength];
            thicknessValue = new double[tablesLength];
            if (cursor.moveToFirst()) {
                for(int i=0; i<tablesLength; i++){
                    thicknessKey[i]   = cursor.getInt(keyIndex);
                    thicknessName[i]  = cursor.getString(nameIndex);
                    thicknessValue[i] = cursor.getDouble(valueIndex);
//                    Log.d(LOG_TAG, "record [" + i + "]: " + thicknessName[i] + " = " + thicknessValue[i] + "mm");
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
    }

    public int getCurrentThicknessKey(){
        return (new StoredKey(pref + materialThickness)).get();
    }

    public void  setCurrentThickness(int num){ // num is selection number in value array
        int key = thicknessKey[num];
        (new StoredKey(pref + materialThickness)).set(key);
    }

    public String getCurrentThicknessName(){
        return MainDB.getStringByKey(materialThickness, currentNameColumnHeader, getCurrentThicknessKey());
    }

    public double getCurrentThicknessMM(){
        SQLiteDatabase db = MainDB.getInstance().getDb();
        String filter = App.getResourceString(R.string.key_field)
                + App.getResourceString(R.string.is_equal_to)
                + getCurrentThicknessKey();
        Cursor cursor = db.query(materialThickness, null, filter, null, null, null, null);
        double result = 0.0;
        int valueIndex = cursor.getColumnIndex(valueColumnHeader);
        if (cursor.moveToFirst()) result = cursor.getDouble(valueIndex);
        cursor.close();
        return result;
    }

}
