/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CuttingChartSubTable {

    public CuttingChartSubTable(SQLiteDatabase db, String settingTableName, String filter, int purposeKey, ArrayList<CuttingChartColumn> columns){
        String filterWithPurpose = filter + " AND " + DataBaseHelper.getFilterEqualTo(R.string.purpose_table, purposeKey);
        Cursor subTableCursor = db.query(settingTableName, null, filterWithPurpose, null, null, null, null);
        int nOfSettings = columns.get(0).getDataLength();
        int nOfNewSettings = subTableCursor.getCount();
        if(nOfNewSettings>0){
            if(subTableCursor.moveToFirst()){
                for(CuttingChartColumn column:columns)
                    column.updateIndex(subTableCursor);
                for(int j=0; j<nOfNewSettings; j++){
                    // fill table data
                    int pos = nOfSettings+j;
                    for(CuttingChartColumn column:columns)
                        if(column.isExternalKey()){
                            int id = subTableCursor.getInt(column.getIndex());
                            String name = DataBaseHelper.getNameByKey(db, column.getTableName(), id);
                            column.setData(pos,name);
                        } else {
                            double data = subTableCursor.getDouble(column.getIndex());
                            column.setData(pos,data);
                        }
                    subTableCursor.moveToNext();
                }
            }
        }
    }
}
