/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.database.Cursor;

import java.util.ArrayList;

public class CuttingChartColumn {

    private final String header;
    private final String dbAccessName;
    private ArrayList<String> data;
    private int queryIndex = -1;
    private final boolean externalKey;
    public final boolean needToScale;

    public String getTableName() {
        return dbAccessName;
    }

    public CuttingChartColumn(int headerStringId, int columnHeaderId, boolean exKey, boolean needToScale){
        this.needToScale = needToScale;
        this.header = App.getResourceString(headerStringId);
        this.dbAccessName = App.getResourceString(columnHeaderId);
        this.externalKey = exKey;
        this.data = new ArrayList<>();
    }

    public void setData(int recordNum, double value){
        Double tmp = value;
        data.ensureCapacity(recordNum + 1);
        data.add(recordNum, tmp.toString());
    }

    public void setData(int recordNum, String s){
        data.ensureCapacity(recordNum + 1);
        data.add(recordNum, s);
    }

    public String getHeader() {
        return header;
    }

    public int getDataLength() {
        return data.size();
    }

    public String getData(int i) {
        return data.get(i);
    }

    public void updateIndex(Cursor cursor) {
        queryIndex = cursor.getColumnIndex(dbAccessName);
    }

    public int getIndex() {
        return queryIndex;
    }

    public boolean isExternalKey() {
        return externalKey;
    }
}
