/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

public class CuttingChartColumn {

    private final String header;

    public CuttingChartColumn(int headerStringId){
        header = App.getResourceString(headerStringId);
    }
}
