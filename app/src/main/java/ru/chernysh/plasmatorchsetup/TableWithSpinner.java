/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Created by Sales on 13.10.2015.
 */
public class TableWithSpinner {

    private static final String LOG_TAG = "Table with spinner: ";
    private static final String[] stringNullArray = {""};
    private static final int[] intNullArray = {0};

    View parent_;
    private int selected = 0;

    public TableWithSpinner(Context context, View parent, final String table_name, int spinnerId){
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
                    selected = brandAdapter.getKey(pos);
                    (new StoredKey(App.getInstance().getString(R.string.PREFERENCE_) + table_name)).set(selected);
                }
            }
        });

        Cursor cursor = db.query(table_name, null, null, null, null, null, null);
        int nOfRows = cursor.getCount();
        name = new String[nOfRows];
        key = new int[nOfRows];
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int keyIndex = cursor.getColumnIndex("key");
            for(int i=0; i<nOfRows; i++){
                name[i] = cursor.getString(nameIndex);
                key[i] = cursor.getInt(keyIndex);
                Log.d(LOG_TAG, "record [" + i + "] : name = " + name[i] + ", key = " + key[i]);
                cursor.moveToNext();
            }
        } else Log.d(LOG_TAG, "0 rows");
        cursor.close();
        db.close();

        CustomAdapter adapter =
                new CustomAdapter(context, android.R.layout.simple_spinner_item, name, key);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt(context.getString(R.string.please_select_prompt));
        for (int i = 0; i < nOfRows; i++)
            if (key[i] == selected) spinner.setSelection(i);
    }

    public int getSelected() {
        return selected;
    }
}
