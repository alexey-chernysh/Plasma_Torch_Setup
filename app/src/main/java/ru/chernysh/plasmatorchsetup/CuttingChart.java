/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import java.util.ArrayList;

public class CuttingChart {

    ArrayList<CuttingChartColumn> columns;

    public CuttingChart(){
        columns = new ArrayList<CuttingChartColumn>(9);
        columns.set(0, new CuttingChartColumn(R.string.process_header, R.string.process_table));
        columns.set(1, new CuttingChartColumn(R.string.current_header, R.string.current_column_name));
        columns.set(2, new CuttingChartColumn(R.string.thickness_header, R.string.thickness_column_name));
        columns.set(3, new CuttingChartColumn(R.string.purpose_header, R.string.purpose_table));
        columns.set(4, new CuttingChartColumn(R.string.arc_voltage_header, R.string.arc_voltage_column_name));
        columns.set(5, new CuttingChartColumn(R.string.arc_height_header, R.string.arc_height_column_name));
        columns.set(6, new CuttingChartColumn(R.string.pierce_height_header, R.string.pierce_height_column_name));
        columns.set(7, new CuttingChartColumn(R.string.pierce_time_header, R.string.pierce_time_column_name));
        columns.set(8, new CuttingChartColumn(R.string.kerf_offset_header, R.string.kerf_offset_column_name));
    }
}
