/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class CuttingChart {

    private ArrayList<CuttingChartColumn> columns;
    private TableLayout table;
    private Context context;

    public CuttingChart(TableLayout tab, Context cntxt){
        table = tab;
        context = cntxt;
        
        columns = new ArrayList<CuttingChartColumn>();
        columns.add(new CuttingChartColumn(R.string.process_header, R.string.process_table,true));
        columns.add(new CuttingChartColumn(R.string.current_header, R.string.current_column_name,false));
        columns.add(new CuttingChartColumn(R.string.thickness_header, R.string.thickness_column_name,false));
        columns.add(new CuttingChartColumn(R.string.purpose_header, R.string.purpose_table,true));
        columns.add(new CuttingChartColumn(R.string.arc_voltage_header, R.string.arc_voltage_column_name,false));
        columns.add(new CuttingChartColumn(R.string.arc_height_header, R.string.arc_height_column_name,false));
        columns.add(new CuttingChartColumn(R.string.pierce_height_header, R.string.pierce_height_column_name,false));
        columns.add(new CuttingChartColumn(R.string.pierce_time_header, R.string.pierce_time_column_name,false));
        columns.add(new CuttingChartColumn(R.string.kerf_offset_header, R.string.kerf_offset_column_name,false));
    }
    
    public void fillHeaderView(){
        //clear obsolete content
        table.removeAllViews();
        // create header row
        TableRow headerRow = new TableRow(context);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        headerRow.setLayoutParams(lp);

        // fill header row
        for (CuttingChartColumn column:columns) {
            TextView columnHeader = new TextView(context);
            columnHeader.setText(column.getHeader());
            int columnNum = columns.indexOf(column);
            headerRow.addView(columnHeader, columnNum);
        }

        // add row to layout
        headerRow.setBackgroundColor(Color.parseColor("#FFFFFFDD"));
        table.addView(headerRow);
    }

    public void fillContentView() {
        //get row number
        int rowNumber = columns.get(0).getDataLength();
        //create & fill data rows
        for(int i=0; i<rowNumber; i++){
            TableRow dataRow = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            dataRow.setLayoutParams(lp);

            // fill header row
            for (CuttingChartColumn column:columns) {
                TextView dataCell = new TextView(context);
                dataCell.setText(column.getData(i));
                int columnNum = columns.indexOf(column);
                dataRow.addView(dataCell, columnNum);
            }

            // add row to layout
            dataRow.setBackgroundColor(Color.parseColor("#FFFFFFDD"));
            table.addView(dataRow);
        }
    }

    public void fillData() {
        final String pref = App.getResourceString(R.string.preference_);
        final String model_table_name = App.getResourceString(R.string.model_table);
        final String material_table_name = App.getResourceString(R.string.material_table);

        int modelKey = (new StoredKey(pref + model_table_name)).get();
        int materialKey = (new StoredKey(pref + material_table_name)).get();

        int[] processKey = getProcessList(modelKey);
        if(processKey != null){
            for (int aProcessKey : processKey)
                fillTableForProcess(materialKey, aProcessKey);
        }
    }

    private int[] getProcessList(int inverter) {

        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        String tableName = App.getResourceString(R.string.process_usage_table);
        String filter = DataBaseHelper.getFilterEqualTo(R.string.model_table, inverter);
        String process_column_header = App.getResourceString(R.string.process_table);
        String[] columnList = {process_column_header};
        Cursor cursor = db.query(true, tableName, columnList, filter, null, null, null, process_column_header, null, null);
        int nOfRows = cursor.getCount();
        int[] result = null;
        if(nOfRows > 0){
            result = new int[nOfRows];
            int processKeyIndex = cursor.getColumnIndex(process_column_header);
            if (cursor.moveToFirst()) {
                for(int i=0; i<nOfRows; i++){
                    result[i] = cursor.getInt(processKeyIndex);
//                    Log.d(LOG_TAG, "getProcessList : process key[" + i + "]= " + result[i]);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    private void fillTableForProcess(int materialKey, int processKey) {
        if(materialKey <= 0) return;
        String materialFilter = DataBaseHelper.getFilterEqualTo(R.string.material_table, materialKey);
        if(processKey <= 0) return;
        String processFilter = DataBaseHelper.getFilterEqualTo(R.string.process_table, processKey);
        // find all "purpose" cases
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        String processName = DataBaseHelper.getNameByKey(db, App.getResourceString(R.string.process_table), processKey);
        String tableName = App.getResourceString(R.string.settings_table);
        String filter = processFilter + " AND " + materialFilter;
//        Log.d(LOG_TAG, "fillTableForProcess : filter 1: " + filter);
        String purpose_column_name = App.getResourceString(R.string.purpose_table);
        String[] columnList = {purpose_column_name};
        Cursor cursor = db.query(true, tableName, columnList, filter, null, null, null, purpose_column_name, null, null);
        int nOfPurpose = cursor.getCount();
        if(nOfPurpose > 0){
            int purposeKeyIndex = cursor.getColumnIndex(purpose_column_name);
            if (cursor.moveToFirst()) {
                for(int i=0; i<nOfPurpose; i++){
                    int purposeKey = cursor.getInt(purposeKeyIndex);
                    String purposeName = DataBaseHelper.getNameByKey(db, purpose_column_name, purposeKey);
                    String filterWithPurpose = filter + " AND " + DataBaseHelper.getFilterEqualTo(R.string.purpose_table, purposeKey);
//                    Log.d(LOG_TAG, "fillTableForProcess : filter 2: " + filterWithPurpose);
                    Cursor purposeCursor = db.query(tableName, null, filterWithPurpose, null, null, null, null);
                    int nOfSettings = columns.get(0).getDataLength();
                    int nOfNewSettings = purposeCursor.getCount();
                    if(nOfNewSettings>0){
                        if(purposeCursor.moveToFirst()){
                            for(CuttingChartColumn column:columns)
                                column.updateIndex(purposeCursor);
                            for(int j=0; j<nOfNewSettings; j++){
                                // fill table data
                                int pos = nOfSettings+j;
                                for(CuttingChartColumn column:columns)
                                    if(column.isExternalKey()){
                                        int id = purposeCursor.getInt(column.getIndex());
                                        String name = DataBaseHelper.getNameByKey(db, column.getTableName(), id);
                                        column.setData(pos,name);
                                    } else {
                                        double data = purposeCursor.getDouble(column.getIndex());
                                        column.setData(pos,data);
                                    }
                                purposeCursor.moveToNext();
                            }
                        }
                    }
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
    }
}
