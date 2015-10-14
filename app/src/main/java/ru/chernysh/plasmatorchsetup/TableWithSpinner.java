/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class TableWithSpinner {

    private static final String LOG_TAG = "Table with spinner: ";
    private static final String[] stringNullArray = {""};
    private static final int[] intNullArray = {0};

    private int selected_ = 0;

    public TableWithSpinner(View view,
                            final String table_name,
                            int spinnerId,
                            int selected){
        selected_ = selected;
        this.init(view,table_name,spinnerId);
    }

    public TableWithSpinner(View view,
                            final String table_name,
                            int spinnerId){
        this.init(view,table_name,spinnerId);
    }

    private void init(View view,
                      final String table_name,
                      int spinnerId){
        Spinner spinner = (Spinner)view.findViewById(spinnerId);
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
                    selected_ = brandAdapter.getKey(pos);
                    (new StoredKey(App.getInstance().getString(R.string.preference_) + table_name)).set(selected_);
                }
            }
        });

        Cursor cursor = db.query(table_name, null, null, null, null, null, null);
        int nOfRows = cursor.getCount();
        name = new String[nOfRows];
        key = new int[nOfRows];
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(App.getInstance().getString(R.string.name_field)
                    + "_" + App.language);
            int keyIndex = cursor.getColumnIndex(App.getInstance().getString(R.string.key_field));
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
                new CustomAdapter(view.getContext(), android.R.layout.simple_spinner_item, name, key);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt(view.getContext().getString(R.string.please_select_prompt));
        for (int i = 0; i < nOfRows; i++)
            if (key[i] == selected_) spinner.setSelection(i);
    }

    public int getSelected() {
        return selected_;
    }
}
