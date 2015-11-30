/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import java.util.ArrayList;

public class CuttingChartColumn {

    private final String header;
    private final String dbAccessName;
    ArrayList<Double> data;

    public CuttingChartColumn(int headerStringId, int columnHeaderId){
        header = App.getResourceString(headerStringId);
        dbAccessName = App.getResourceString(columnHeaderId);
    }

    public void setData(int recordNum, double value){
        data.add(value);
    }

    public String getHeader() {
        return header;
    }

    public double getData(int recordNum){
        return data.get(recordNum);
    }

    public String getDbAccessName() {
        return dbAccessName;
    }
}
