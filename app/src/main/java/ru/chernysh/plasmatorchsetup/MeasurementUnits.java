/*
 * Copyright (c) 2016. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MeasurementUnits {

    final static String pref = App.getResourceString(R.string.preference_);
    final static String metricSystemTableName = App.getResourceString(R.string.metric_system_table);

    public static int getUnitsKey(){
        StoredKey storeUnits = new StoredKey(pref + metricSystemTableName);
        int unitsSelected = storeUnits.get();
        if(unitsSelected == 0){
            if(App.language.equals(App.getResourceString(R.string.english_language))){
                // imperial system
                unitsSelected = 2;
            } else {
                // metric system
                unitsSelected = 1;
            }
            storeUnits.set(unitsSelected);
        }
        return unitsSelected;
    }

    public static String getCurrentMetricSystemName(){
        return MainDB.getNameByKey(metricSystemTableName, MeasurementUnits.getUnitsKey());
    }

    public static double getCurrentScale(){
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
}
