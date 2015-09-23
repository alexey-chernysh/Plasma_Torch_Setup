/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import java.io.IOException;

/**
 * Created by Sales on 23.09.2015.
 */
public class ChainedSQLiteException extends Exception {
    public ChainedSQLiteException(String s, IOException ex) {
    }
}
