/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CuttingChartSubTable {

//    private final static String LOG_TAG = "SubTable:";

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
        Cursor subTableCursor = db.query(settingTableName, null, filterWithPurpose, null, null, null, null);
        nOfRows = subTableCursor.getCount();
        if(nOfRows>0){
            dataBuffer = new Object[columns.size()][nOfRows];
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
                            dataBuffer[k][j] = name;
                        } else {
                            Double data = subTableCursor.getDouble(column.getIndex());
//                            Log.d(LOG_TAG, "Double data=" + data.toString() + " at j=" + j + ",k=" + k);
                            dataBuffer[k][j] = data;
                        }
                    }
                    subTableCursor.moveToNext();
                }
            }
        }
    }

    public void fillRow(double thickness){
        int pos = columns.get(0).getDataLength();
        if(nOfRows>0){
            double minThickness =  (Double)dataBuffer[0][0];
            double maxThickness =  (Double)dataBuffer[0][nOfRows-1];
//            Log.d(LOG_TAG, "Thickness=" + thickness + " min=" + minThickness + ",max=" + maxThickness);
            if(thickness >= minThickness)
                if(thickness <= maxThickness){
                    for(CuttingChartColumn column:columns) {
                        int k = columns.indexOf(column);
                        if (column.isExternalKey()) {
                            String name = (String)dataBuffer[k][0];
//                            Log.d(LOG_TAG, "String value found = " + name);
                            column.setData(pos, name);
                        } else {
                            if(k>0){
                                double data = getValueForThickness(thickness, dataBuffer[k], dataBuffer[0], column.needToScale);
//                                Log.d(LOG_TAG, "Double value found = " + data);
                                column.setData(pos, data);
                            } else {
//                                Log.d(LOG_TAG, "Thickness value found = " + thickness);
                                column.setData(pos, thickness);
                            }
                        }
                    }
                }
        }
    }

    private double getValueForThickness(double thickness,
                                        Object[] valueVector,
                                        Object[] thicknessVector,
                                        boolean needToScale) {
        int N = thicknessVector.length;

        int index1 = 0;
        double value1 = (Double)valueVector[index1];
        double thickness1 = (Double)thicknessVector[index1];

        int index2 = N-1;
        double value2 = (Double)valueVector[index2];
        double thickness2 = (Double)thicknessVector[index2];

        for(int i=0; i<N; i++){
            double tmp = (Double)thicknessVector[i];
            if(tmp <= thickness)
                if((Double)valueVector[i]>0.0){
                    index1 = i;
                    value1 = (Double)valueVector[index1];
                    thickness1 = (Double)thicknessVector[index1];
                }
        }

        for(int i=N-1; i>=0; i--){
            double tmp = (Double)thicknessVector[i];
            if(tmp >= thickness)
                if((Double)valueVector[i]>0.0){
                    index2 = i;
                    value2 = (Double)valueVector[index2];
                    thickness2 = (Double)thicknessVector[index2];
                }
        }

//        Log.d(LOG_TAG, "index1 = " + index1 + " thickness1=" + thickness1 + " value1=" + value1);
//        Log.d(LOG_TAG, "index2 = " + index2 + " thickness2=" + thickness2 + " value2=" + value2);
        if(value1 <= 0.0) return 0.0;
        if(value2 <= 0.0) return 0.0;
        if(thickness2 == thickness1) return value1;
        double averageValue = value1 + (value2 - value1)*(thickness - thickness1)/(thickness2 - thickness1);
        if(needToScale) averageValue /= MeasurementUnits.getCurrentScale();
        int tmp = (int)(averageValue*1000.0);
        return tmp/1000.0;
    }
}
