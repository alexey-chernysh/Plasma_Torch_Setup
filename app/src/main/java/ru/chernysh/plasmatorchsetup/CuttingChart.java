/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
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
        columns.add(new CuttingChartColumn(R.string.process_header, R.string.process_table));
        columns.add(new CuttingChartColumn(R.string.current_header, R.string.current_column_name));
        columns.add(new CuttingChartColumn(R.string.thickness_header, R.string.thickness_column_name));
        columns.add(new CuttingChartColumn(R.string.purpose_header, R.string.purpose_table));
        columns.add(new CuttingChartColumn(R.string.arc_voltage_header, R.string.arc_voltage_column_name));
        columns.add(new CuttingChartColumn(R.string.arc_height_header, R.string.arc_height_column_name));
        columns.add(new CuttingChartColumn(R.string.pierce_height_header, R.string.pierce_height_column_name));
        columns.add(new CuttingChartColumn(R.string.pierce_time_header, R.string.pierce_time_column_name));
        columns.add(new CuttingChartColumn(R.string.kerf_offset_header, R.string.kerf_offset_column_name));
    }
    
    public void fillHeader(){
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
}
