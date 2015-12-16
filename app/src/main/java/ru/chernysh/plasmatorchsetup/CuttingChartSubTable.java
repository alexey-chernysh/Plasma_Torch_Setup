/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class CuttingChartSubTable {

    private final static String LOG_TAG = "SubTable:";

    private Cursor subTableCursor;
    private int nOfRows = 0;
    private Object[][] dataBuffer;
    private ArrayList<CuttingChartColumn> columns;

    public CuttingChartSubTable(SQLiteDatabase db,
                                String settingTableName,
                                String filter,
                                int purposeKey,
                                ArrayList<CuttingChartColumn> col){
        columns = col;
        String filterWithPurpose = filter + " AND " + DataBaseHelper.getFilterEqualTo(R.string.purpose_table, purposeKey);
        subTableCursor = db.query(settingTableName, null, filterWithPurpose, null, null, null, null);
        nOfRows = subTableCursor.getCount();
        if(nOfRows>0){
            dataBuffer = new Object[nOfRows][columns.size()];
            if(subTableCursor.moveToFirst()){
                for(CuttingChartColumn column:columns)
                    column.updateIndex(subTableCursor);
                for(int j=0; j<nOfRows; j++){
                    // fill table data
                    for(CuttingChartColumn column:columns) {
                        int k = columns.indexOf(column);
                        if (column.isExternalKey()) {
                            int id = subTableCursor.getInt(column.getIndex());
                            String name = DataBaseHelper.getNameByKey(db, column.getTableName(), id);
                            dataBuffer[j][k] = name;
                        } else {
                            Double data = subTableCursor.getDouble(column.getIndex());
//                            Log.d(LOG_TAG, "Double data=" + data.toString() + " at j=" + j + ",k=" + k);
                            dataBuffer[j][k] = data;
                        }
                    }
                    subTableCursor.moveToNext();
                }
            }
        }
    }

    public void fillRow(double thickness){
        int nOfSettings = columns.get(0).getDataLength();
        nOfRows = subTableCursor.getCount();
        if(nOfRows>0){
            if(subTableCursor.moveToFirst()){
                for(int j=0; j<1; j++){
//                for(int j=0; j<nOfRows; j++){
                    // fill table data
                    int pos = nOfSettings + j;
                    for(CuttingChartColumn column:columns) {
                        int k = columns.indexOf(column);
                        if (column.isExternalKey()) {
                            String name = (String)dataBuffer[j][k];
                            column.setData(pos, name);
                        } else {
                            double data = (Double)dataBuffer[j][k];
                            column.setData(pos, data);
                        }
                    }
                    subTableCursor.moveToNext();
                }
            }
        }
    }
}
