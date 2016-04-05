/*
 * Copyright (c) 2016. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sales on 05.04.2016.
 */
public class MeasurementUnits {

    final static String pref = App.getResourceString(R.string.preference_);
    final static String unitsTableName = App.getResourceString(R.string.units_table);

    public static String getCurrentName(){
        int unitsSelected = (new StoredKey(pref + unitsTableName)).get();
        return MainDB.getNameByKey(unitsTableName, unitsSelected);
    }

    public static double getCurrentScale(){
        int unitsSelected = (new StoredKey(pref + unitsTableName)).get();
        SQLiteDatabase db_ = MainDB.getInstance().getDb();
        String filter = App.getResourceString(R.string.key_field)
                      + App.getResourceString(R.string.is_equal_to)
                      + unitsSelected;
        Cursor cursor = db_.query(unitsTableName, null, filter, null, null, null, null);
        int scaleIndex = cursor.getColumnIndex(App.getResourceString(R.string.scale_column_name));
        double result = 1.0;
        if (cursor.moveToFirst()) result = cursor.getDouble(scaleIndex);
        cursor.close();
        return result;
    }
}
