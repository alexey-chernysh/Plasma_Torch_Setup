/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.widget.TextView;

import java.util.ArrayList;

public class CuttingChart {

    ArrayList<CuttingChartColumn> columns;

    public CuttingChart(){
        columns = new ArrayList<CuttingChartColumn>(9);
        columns.set(0, new CuttingChartColumn(R.string.process_header));
        columns.set(1, new CuttingChartColumn(R.string.current_header));
        columns.set(2, new CuttingChartColumn(R.string.thickness_header));
        columns.set(3, new CuttingChartColumn(R.string.purpose_header));
        columns.set(4, new CuttingChartColumn(R.string.arc_voltage_header));
        columns.set(5, new CuttingChartColumn(R.string.arc_height_header));
        columns.set(6, new CuttingChartColumn(R.string.pierce_height_header));
        columns.set(7, new CuttingChartColumn(R.string.pierce_time_header));
        columns.set(8, new CuttingChartColumn(R.string.kerf_offset_header));
    }
}
