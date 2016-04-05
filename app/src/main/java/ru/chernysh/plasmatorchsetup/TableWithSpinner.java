/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
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
    private CuttingChartActivity parentActivity_;
    private int spinnerId_;
    private CustomAdapter adapter;
    private final String table_name_;
    private TableWithSpinner upper_level_;
    private TableWithSpinner lower_level_;

    public TableWithSpinner(@NonNull View parentView,
                            @NonNull String table_name,
                            int spinnerId){
        parentView_ = parentView;
        parentActivity_ = (CuttingChartActivity)parentView_.getContext();
        table_name_ = table_name;
        spinnerId_  = spinnerId;
        init();
        updateList();
    }

    public TableWithSpinner setUpperLevelSpinner(TableWithSpinner upper_level){
        upper_level_ = upper_level;
        if(upper_level.getLowerLevelSpinner() != this )
            upper_level_.setLowerLevelSpinner(this);
        updateList();
        return this;
    }

    public TableWithSpinner setLowerLevelSpinner(TableWithSpinner lower_level){
        lower_level_ = lower_level;
        if(lower_level_.getUpperLevelSpinner() != this)
            lower_level_.setUpperLevelSpinner(this);
        return this;
    }


    private TableWithSpinner getLowerLevelSpinner() {
        return lower_level_;
    }

    private TableWithSpinner getUpperLevelSpinner() {
        return upper_level_;
    }

    private void init(){
        Spinner spinner = (Spinner)parentView_.findViewById(spinnerId_);
        Log.d(LOG_TAG, " Init - spinnerId " + spinnerId_ + "; parentView - " + parentView_ + "; spinner - " + spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CustomAdapter adapter = (CustomAdapter) parent.getAdapter();
                if (adapter != null) {
                    int selectedKey_ = adapter.getKey(pos);
                    String assStr = (String) adapter.getItem(pos);
                    (new StoredKey(App.getResourceString(R.string.preference_) + table_name_)).set(selectedKey_);
                    Log.d(LOG_TAG, " selection at pos " + pos + " in spinner for table " + table_name_ + " by key = " + selectedKey_ + " associated with string value " + assStr);
                    if (lower_level_ != null)
                        if(lower_level_.getFilterKey() != selectedKey_)
                            lower_level_.updateList(selectedKey_);
                    parentActivity_.updateChart();
                }
            }
        });
    }


    public void updateList(){
        int filterColumnKey = this.getFilterKey();
        updateList(filterColumnKey);
    }

    private void updateList(int filterColumnKey) {
        String[] name  = stringNullArray;
        int[] key = intNullArray;

        String filter = null;
        if(upper_level_ != null){
            String filterColumnName_ = upper_level_.getTableName();
            if(filterColumnName_ != null){
                if(filterColumnKey != 0)
                    filter = filterColumnName_
                           + App.getResourceString(R.string.is_equal_to)
                           + filterColumnKey;
            }
        }

        Log.d(LOG_TAG, "table name is " + table_name_ + "; filter string is " + filter + ";");
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        Cursor cursor = db.query(table_name_, null, filter, null, null, null, null);
        int nOfRows = cursor.getCount();
        if(nOfRows > 0){
            name = new String[nOfRows];
            key = new int[nOfRows];
            String nameHeader = DataBaseHelper.getNameColumnHeader(cursor);
            cursor.close();
            cursor = db.query(table_name_, null, filter, null, null, null, nameHeader);
            int nameIndex = cursor.getColumnIndex(nameHeader);
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

        adapter = new CustomAdapter(parentView_.getContext(), android.R.layout.simple_spinner_item, name, key);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner)parentView_.findViewById(spinnerId_);
        spinner.setAdapter(adapter);
        spinner.setEnabled(nOfRows > 0);
        spinner.invalidate();
    }

    public int getFilterKey(){
        // find filter key for selected item
        int result = 0;

        if(upper_level_ == null) return 0;
        String filterColumnName = upper_level_.getTableName();
        if(filterColumnName == null) return 0;
        int selectedKey = this.getSelected();
        if(selectedKey == 0) return 0;

        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        String filter = DataBaseHelper.getFilterEqualTo(R.string.key_field, selectedKey);
        Cursor cursor = db.query(table_name_, null, filter, null, null, null, null);
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(filterColumnName);
            result = cursor.getInt(index);
        }
        cursor.close();
        db.close();
        Log.d(LOG_TAG, "getFilterKey result = " + result + " for table " + table_name_ + " row with key =  " + selectedKey);
        return result;
    }

    public String getTableName() {
        return table_name_;
    }

    public void setSelected(int keySelected) {
        int pos = adapter.getPos(keySelected);
        Spinner spinner = (Spinner)parentView_.findViewById(spinnerId_);
        Log.d(LOG_TAG, " Init - spinnerId " + spinnerId_ + "; parentView - " + parentView_ + "; spinner - " + spinner);
        spinner.setSelection(pos);
        String name = (String)spinner.getSelectedItem();
        Log.d(LOG_TAG, "setSelected in " + table_name_ + " spinner to key = " + keySelected + ", that associated with pos = " + pos + "," + name);
    }

    public int getSelected() {
        Spinner spinner = (Spinner)parentView_.findViewById(spinnerId_);
        int pos = spinner.getSelectedItemPosition();
        int result = adapter.getKey(pos);
        String name = (String)spinner.getSelectedItem();
        Log.d(LOG_TAG, "getSelected in " + table_name_ + ", result = " + result + "," + name);
        return result;
    }
}
