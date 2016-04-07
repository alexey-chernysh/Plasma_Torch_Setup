/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class CuttingChart {

    private ArrayList<CuttingChartColumn> columns_;
    private TableLayout table_;
    private Context context_;

    public CuttingChart(TableLayout tab, Context context){
        table_ = tab;
        context_ = context;
        
        columns_ = new ArrayList<>();
        columns_.add(new CuttingChartColumn(R.string.thickness_header, R.string.thickness_column_name, false, true));
        columns_.add(new CuttingChartColumn(R.string.process_header, R.string.process_table, true, false));
        columns_.add(new CuttingChartColumn(R.string.current_header, R.string.current_column_name, false, false));
        columns_.add(new CuttingChartColumn(R.string.purpose_header, R.string.purpose_table, true, false));
        columns_.add(new CuttingChartColumn(R.string.cut_speed_header, R.string.cut_speed_column_name, false, true));
        columns_.add(new CuttingChartColumn(R.string.arc_voltage_header, R.string.arc_voltage_column_name, false, false));
        columns_.add(new CuttingChartColumn(R.string.arc_height_header, R.string.arc_height_column_name, false, true));
        columns_.add(new CuttingChartColumn(R.string.pierce_height_header, R.string.pierce_height_column_name, false, true));
        columns_.add(new CuttingChartColumn(R.string.pierce_time_header, R.string.pierce_time_column_name, false, false));
        columns_.add(new CuttingChartColumn(R.string.kerf_offset_header, R.string.kerf_offset_column_name, false, true));
    }
    
    public void fillHeaderView(){
        //clear obsolete content
        table_.removeAllViews();
        // create header row
        TableRow headerRow = new TableRow(context_);

        // fill header row
        for (CuttingChartColumn column: columns_) {
            int columnNum = columns_.indexOf(column);
            if(columnNum > 0) { // hide thickness column
                TextView columnHeader = new TextView(context_);
                columnHeader.setText(column.getHeader());
                columnHeader.setTextColor(ContextCompat.getColor(context_, R.color.HeaderText));
                columnHeader.setTextSize(14);
                columnHeader.setLines(3);
                columnHeader.setPadding(4,4,4,4);
                if((columnNum % 2) > 0)columnHeader.setBackgroundColor(ContextCompat.getColor(context_, R.color.TableHeaderOddBackground));
                else columnHeader.setBackgroundColor(ContextCompat.getColor(context_, R.color.TableHeaderEvenBackground));
                headerRow.addView(columnHeader, columnNum-1);
            }
        }

        // add row to layout
        table_.addView(headerRow);
    }

        public void fillContentView() {
            //get row number
        int rowNumber = columns_.get(0).getDataLength();
        //create & fill data rows
        for(int i=0; i<rowNumber; i++){
            TableRow dataRow = new TableRow(context_);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            dataRow.setLayoutParams(lp);

            // fill header row
            for (CuttingChartColumn column: columns_) {
                int columnNum = columns_.indexOf(column);
                if(columnNum > 0){ // hide thickness column
                    TextView dataCell = new TextView(context_);
                    CharSequence cellString = column.getData(i);
                    dataCell.setLines(2);
                    dataCell.setText(cellString);
                    dataCell.setBackgroundColor(ContextCompat.getColor(context_, R.color.TableItemEvenBackground));
                    dataCell.setTextColor(ContextCompat.getColor(context_, R.color.EditText));
                    if((columnNum % 2) > 0)dataCell.setBackgroundColor(ContextCompat.getColor(context_, R.color.TableItemOddBackground));
                    else dataCell.setBackgroundColor(ContextCompat.getColor(context_, R.color.TableItemEvenBackground));
                    dataRow.addView(dataCell, columnNum-1);
                }
            }

            // add row to layout
            table_.addView(dataRow);
        }
    }

    public void fillData() {
        final String pref = App.getResourceString(R.string.preference_);
        final String modelTableName = App.getResourceString(R.string.model_table);
        final String materialTableName = App.getResourceString(R.string.material_table);

        int modelKey = (new StoredKey(pref + modelTableName)).get();
        int materialKey = (new StoredKey(pref + materialTableName)).get();
        double thickness = MaterialThickness.getInstance().getCurrentThicknessMM();

        int[] processKey = getProcessList(modelKey);
        if(processKey != null){
            for (int aProcessKey : processKey)
                fillTableForProcess(materialKey, aProcessKey, thickness);
        }
    }

    private int[] getProcessList(int inverter) {

        SQLiteDatabase db = MainDB.getInstance().getDb();
        String tableName = App.getResourceString(R.string.process_usage_table);
        String filter = MainDB.getFilterEqualTo(R.string.model_table, inverter);
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
        return result;
    }

    private void fillTableForProcess(int materialKey, int processKey, double thickness) {
        if(materialKey <= 0) return;
        String materialFilter = MainDB.getFilterEqualTo(R.string.material_table, materialKey);
        if(processKey <= 0) return;
        String processFilter = MainDB.getFilterEqualTo(R.string.process_table, processKey);
        // find all "purpose" cases
        SQLiteDatabase db = MainDB.getInstance().getDb();
        String tableName = App.getResourceString(R.string.settings_table);
        String filter = processFilter + " AND " + materialFilter;
        String purpose_column_name = App.getResourceString(R.string.purpose_table);
        String[] columnList = {purpose_column_name};
        Cursor cursor = db.query(true, tableName, columnList, filter, null, null, null, purpose_column_name, null, null);
        int nOfPurpose = cursor.getCount();
        if(nOfPurpose > 0){
            int purposeKeyIndex = cursor.getColumnIndex(purpose_column_name);
            if (cursor.moveToFirst()) {
                for(int i=0; i<nOfPurpose; i++){
                    int purposeKey = cursor.getInt(purposeKeyIndex);
                    CuttingChartSubTable subTable = new CuttingChartSubTable(db, tableName, filter, purposeKey, columns_);
                    subTable.fillRow(thickness);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
    }

}
