/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Created by Sales on 13.10.2015.
 */
public class TableWithSpinner {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";
    private static final String[] stringNullArray = {""};
    private static final int[] intNullArray = {0};

    View parent_;

    public TableWithSpinner(View parent, final String table_name, int spinnerId){
        parent_ = parent;
        Spinner spinner = (Spinner)parent_.findViewById(spinnerId);
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        String[] name  = stringNullArray;
        int[] key = intNullArray;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CustomAdapter brandAdapter = (CustomAdapter) parent.getAdapter();
                if (brandAdapter != null) {
                    int selectedKey = brandAdapter.getKey(pos);
                    (new StoredKey(App.getInstance().getString(R.string.PREFERENCE_) + table_name)).set(selectedKey);
                }
            }
        });
    }
}
