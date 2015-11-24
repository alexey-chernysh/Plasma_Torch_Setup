/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CuttingChartFragment extends Fragment {

    private static final String LOG_TAG = "CuttingChartFragment: ";
    
    public CuttingChartFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cutting_chart, container, false);
        TableLayout table = (TableLayout)v.findViewById(R.id.plasmaSettingTable);
        prepareTable(table);
        fillTableContent(table);
        return v;
    }

    private void prepareTable(TableLayout table) {
        Log.d(LOG_TAG, "prepare table");
        // clear table
        table.removeAllViews();
        Context context = this.getActivity();
        // create header row
        TableRow headerRow = new TableRow(context);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        headerRow.setLayoutParams(lp);

        // fill table header
        TextView processHeader = new TextView(context);
        processHeader.setText(App.getResourceString(R.string.process_header));
        headerRow.addView(processHeader);

        TextView currentHeader = new TextView(context);
        currentHeader.setText(App.getResourceString(R.string.current_header));
        headerRow.addView(currentHeader);

        TextView thicknessHeader = new TextView(context);
        thicknessHeader.setText(App.getResourceString(R.string.thickness_header));
        headerRow.addView(thicknessHeader);

        TextView purposeHeader = new TextView(context);
        purposeHeader.setText(App.getResourceString(R.string.purpose_header));
        headerRow.addView(purposeHeader);

        TextView arcVoltageHeader = new TextView(context);
        arcVoltageHeader.setText(App.getResourceString(R.string.arc_voltage_header));
        headerRow.addView(arcVoltageHeader);

        TextView arcHeightHeader = new TextView(context);
        arcHeightHeader.setText(App.getResourceString(R.string.arc_height_header));
        headerRow.addView(arcHeightHeader);

        TextView pierceHeightHeader = new TextView(context);
        pierceHeightHeader.setText(App.getResourceString(R.string.pierce_height_header));
        headerRow.addView(pierceHeightHeader);

        TextView pierceTimeHeader = new TextView(context);
        pierceTimeHeader.setText(App.getResourceString(R.string.pierce_time_header));
        headerRow.addView(pierceTimeHeader);

        TextView kerfOffsetHeader = new TextView(context);
        kerfOffsetHeader.setText(App.getResourceString(R.string.kerf_offset_header));
        headerRow.addView(kerfOffsetHeader);

        // add row to layout
        headerRow.setBackgroundColor(Color.parseColor("#FFFFFFDD"));
        table.addView(headerRow);
    }

    private void fillTableContent(TableLayout table) {

        final String pref = App.getResourceString(R.string.preference_);
        final String model_table_name = App.getResourceString(R.string.model_table);
        final String material_table_name = App.getResourceString(R.string.material_table);

        int modelKey = (new StoredKey(pref + model_table_name)).get();
        int materialKey = (new StoredKey(pref + material_table_name)).get();

        int[] processKey = getProcessList(modelKey);
        if(processKey != null){
            for (int aProcessKey : processKey)
                fillTableForProcess(table, materialKey, aProcessKey);
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
                    Log.d(LOG_TAG, "getProcessList : process key[" + i + "]= " + result[i]);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    private void fillTableForProcess(TableLayout table, int materialKey, int processKey) {
        if(materialKey <= 0) return;
        String materialFilter = DataBaseHelper.getFilterEqualTo(R.string.material_table, materialKey);
        if(processKey <= 0) return;
        String processFilter = DataBaseHelper.getFilterEqualTo(R.string.process_table, processKey);
        // find all "purpose" cases
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        String processName = DataBaseHelper.getNameByKey(db, App.getResourceString(R.string.process_table), processKey);
        String tableName = App.getResourceString(R.string.settings_table);
        String filter = processFilter + " AND " + materialFilter;
        Log.d(LOG_TAG, "fillTableForProcess : filter 1: " + filter);
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
                    Log.d(LOG_TAG, "fillTableForProcess : filter 2: " + filterWithPurpose);
                    Cursor purposeCursor = db.query(tableName, null, filterWithPurpose, null, null, null, null);
                    int thicknessIndex = purposeCursor.getColumnIndex(App.getResourceString(R.string.thickness_column_name));
                    int currentIndex = purposeCursor.getColumnIndex(App.getResourceString(R.string.current_column_name));
                    int arcVoltageIndex = purposeCursor.getColumnIndex(App.getResourceString(R.string.arc_voltage_column_name));
                    int arcHeightIndex = purposeCursor.getColumnIndex(App.getResourceString(R.string.arc_height_column_name));
                    int pierceHeightIndex = purposeCursor.getColumnIndex(App.getResourceString(R.string.pierce_height_column_name));
                    int pierceTimeIndex = purposeCursor.getColumnIndex(App.getResourceString(R.string.pierce_time_column_name));
                    int kerfOffsetIndex = purposeCursor.getColumnIndex(App.getResourceString(R.string.kerf_offset_column_name));
                    int nOfSettings = purposeCursor.getCount();
                    if(nOfSettings>0){
                        if(purposeCursor.moveToFirst()){
                            Context context = this.getActivity();
                            for(int j=0; j<nOfSettings; j++){
                                // create content row
                                TableRow row = new TableRow(context);
                                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                                row.setLayoutParams(lp);

                                // fill table data
                                TextView processData = new TextView(context);
                                Log.d(LOG_TAG, "fillTableForProcess : process name: " + processName);
                                processData.setText(processName);
                                row.addView(processData);

                                TextView currentData = new TextView(context);
                                double current = purposeCursor.getDouble(currentIndex);
                                String currentText = Double.toString(current);
                                Log.d(LOG_TAG, "fillTableForProcess : current, A: " + currentText);
                                currentData.setText(currentText);
                                row.addView(currentData);

                                TextView thicknessData = new TextView(context);
                                double thickness = purposeCursor.getDouble(thicknessIndex);
                                String thicknessText = Double.toString(thickness);
                                Log.d(LOG_TAG, "fillTableForProcess : thickness, mm: " + thicknessText);
                                thicknessData.setText(thicknessText);
                                row.addView(thicknessData);

                                TextView purposeData = new TextView(context);
                                Log.d(LOG_TAG, "fillTableForProcess : purpose name: " + purposeName);
                                purposeData.setText(purposeName);
                                row.addView(purposeData);

                                TextView arcVoltageData = new TextView(context);
                                double arcVoltage = purposeCursor.getDouble(arcVoltageIndex);
                                String arcVoltageText = Double.toString(arcVoltage);
                                Log.d(LOG_TAG, "fillTableForProcess : arcVoltage: " + arcVoltage);
                                arcVoltageData.setText(arcVoltageText);
                                row.addView(arcVoltageData);

                                TextView arcHeightData = new TextView(context);
                                double arcHeight = purposeCursor.getDouble(arcHeightIndex);
                                String arcHeightText = Double.toString(arcHeight);
                                Log.d(LOG_TAG, "fillTableForProcess : arcHeight: " + arcHeight);
                                arcHeightData.setText(arcHeightText);
                                row.addView(arcHeightData);

                                TextView pierceHeightData = new TextView(context);
                                double pierceHeight = purposeCursor.getDouble(pierceHeightIndex);
                                String pierceHeightText = Double.toString(pierceHeight);
                                Log.d(LOG_TAG, "fillTableForProcess : pierceHeight: " + pierceHeight);
                                pierceHeightData.setText(pierceHeightText);
                                row.addView(pierceHeightData);

                                TextView pierceTimeData = new TextView(context);
                                double pierceTime = purposeCursor.getDouble(pierceTimeIndex);
                                String pierceTimeText = Double.toString(pierceTime);
                                Log.d(LOG_TAG, "fillTableForProcess : pierceTime: " + pierceTime);
                                pierceTimeData.setText(pierceTimeText);
                                row.addView(pierceTimeData);

                                TextView kerfOffsetData = new TextView(context);
                                double kerfOffset = purposeCursor.getDouble(kerfOffsetIndex);
                                String kerfOffsetText = Double.toString(kerfOffset);
                                Log.d(LOG_TAG, "fillTableForProcess : kerfOffset: " + kerfOffset);
                                kerfOffsetData.setText(kerfOffsetText);
                                row.addView(kerfOffsetData);

                                row.setBackgroundColor(Color.parseColor("#FFFFFFDD"));
                                table.addView(row);

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
