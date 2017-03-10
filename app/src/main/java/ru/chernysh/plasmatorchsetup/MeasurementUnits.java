/*
 * Copyright (c) 2016. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class MeasurementUnits {

    private final static String pref = App.getResourceString(R.string.preference_);
    private final static String metricSystemTableName = App.getResourceString(R.string.metric_system_table);
    private final static int METRIC = 1;
    private final static int IMPERIAL = 2;

    static int getUnitsKey(){
        final StoredKey storedUnits = new StoredKey(pref + metricSystemTableName);
        int unitsSelected = storedUnits.get();
        if(unitsSelected == 0){
            if(App.language.equals(App.getResourceString(R.string.english_language))){
                // imperial system
                unitsSelected = IMPERIAL;
            } else {
                // metric system
                unitsSelected = METRIC;
            }
            storedUnits.set(unitsSelected);
        }
        return unitsSelected;
    }

    static String getCurrentMetricSystemName(){
        return MainDB.getNameByKey(metricSystemTableName, MeasurementUnits.getUnitsKey());
    }

    static double getCurrentScale(){
        SQLiteDatabase db_ = MainDB.getInstance().getDb();
        String filter = App.getResourceString(R.string.key_field)
                      + App.getResourceString(R.string.is_equal_to)
                      + MeasurementUnits.getUnitsKey();
        Cursor cursor = db_.query(metricSystemTableName, null, filter, null, null, null, null);
        int scaleIndex = cursor.getColumnIndex(App.getResourceString(R.string.scale_column_name));
        double result = 1.0;
        if (cursor.moveToFirst()) result = cursor.getDouble(scaleIndex);
        cursor.close();
        return result;
    }

    static void setMetricSystem(){
        final StoredKey storedUnits = new StoredKey(pref + metricSystemTableName);
        storedUnits.set(METRIC);
    }

    static void setImperialSystem(){
        final StoredKey storedUnits = new StoredKey(pref + metricSystemTableName);
        storedUnits.set(IMPERIAL);
    }
}
