/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class TableWithSpinner {

    private static final String LOG_TAG = "Table with spinner: ";
    private static final String[] stringNullArray = {""};
    private static final int[] intNullArray = {0};

    private View parentView_;
    private int selectedKey_;
    private int spinnerId_;
    private final String table_name_;
    private String filterColumnName_;
    private TableWithSpinner dependent_;

    public TableWithSpinner(@NonNull View parentView,
                            @NonNull String table_name,
                            int spinnerId,
                            int selectedKey,
                            String filterColumnName,
                            int filterColumnKey,
                            TableWithSpinner dependent){
        parentView_ = parentView;
        table_name_ = table_name;
        spinnerId_ = spinnerId;
        filterColumnName_ = filterColumnName;
        dependent_ = dependent;
        init();
        updateList(selectedKey, filterColumnKey);
    }

    private void init(){
        Spinner spinner = (Spinner)parentView_.findViewById(spinnerId_);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CustomAdapter brandAdapter = (CustomAdapter) parent.getAdapter();
                if (brandAdapter != null) {
                    selectedKey_ = brandAdapter.getKey(pos);
                    String assStr = (String)brandAdapter.getItem(pos);
                    Log.d(LOG_TAG, " selection in spinner for table " + table_name_ + " by key = " + selectedKey_ + " associated with string value " + assStr);

                    (new StoredKey(App.getResourceString(R.string.preference_) + table_name_)).set(selectedKey_);
                }
                if (dependent_ != null) {
                    dependent_.updateList(0, selectedKey_);
                }
            }
        });
        spinner.setPrompt(App.getResourceString(R.string.please_select_prompt));
    }

    public void updateList(int selectedKey, int filterColumnKey){
        String[] name  = stringNullArray;
        int[] key = intNullArray;

        selectedKey_ = selectedKey;
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        String filter = null;
        if(filterColumnName_ != null)
            filter = filterColumnName_
                      + App.getResourceString(R.string.is_equal_to)
                      + filterColumnKey;
        Log.d(LOG_TAG, "table name is " + table_name_ + "; selected key is " + selectedKey + ", filter value is " + filter + ";");
        Cursor cursor = db.query(table_name_, null, filter, null, null, null, null);
        int nOfRows = cursor.getCount();
        if(nOfRows > 0){
            name = new String[nOfRows];
            key = new int[nOfRows];
            String nameHeader = App.getResourceString(R.string.name_field)
                    + "_" + App.language;
            int nameIndex = cursor.getColumnIndex(nameHeader);
            if(nameIndex<0){ // for multi language names without localization
                nameHeader = App.getResourceString(R.string.name_field);
            }
            cursor.close();
            cursor = db.query(table_name_, null, filter, null, null, null, nameHeader);
            nameIndex = cursor.getColumnIndex(nameHeader);
            int keyIndex = cursor.getColumnIndex(App.getResourceString(R.string.key_field));
            if (cursor.moveToFirst()) {
                for(int i=0; i<nOfRows; i++){
                    name[i] = cursor.getString(nameIndex);
                    key[i] = cursor.getInt(keyIndex);
                    Log.d(LOG_TAG, "record [" + i + "] : name = " + name[i] + ", key = " + key[i]);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        db.close();

        CustomAdapter adapter =
                new CustomAdapter(parentView_.getContext(), android.R.layout.simple_spinner_item, name, key);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner)parentView_.findViewById(spinnerId_);
        spinner.setAdapter(adapter);
        spinner.setEnabled(nOfRows > 0);
        for (int i = 0; i < nOfRows; i++){
            int k = key[i];
            if ( k == selectedKey_) spinner.setSelection(i);
        }
        spinner.invalidate();
    }

    public int getFilterKey(int selectionKey){
        // find filter key for selected item
        if(filterColumnName_ == null) return 0;
        if(selectionKey == 0) return 0;

        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        String filter = App.getResourceString(R.string.key_field)
                      + App.getResourceString(R.string.is_equal_to)
                      + selectionKey;
        Cursor cursor = db.query(table_name_, null, filter, null, null, null, null);
        int result = 0;
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(filterColumnName_);
            result = cursor.getInt(index);
        };
        cursor.close();
        db.close();
        Log.d(LOG_TAG, "getFilterKey result =" + result +  " for table " + table_name_ + " row with key =  " + selectionKey);
        return result;
    }

    public int getSelected() {
        return selectedKey_;
    }
}
