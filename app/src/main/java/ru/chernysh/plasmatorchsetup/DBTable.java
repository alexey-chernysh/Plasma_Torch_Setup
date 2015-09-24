/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

/**
 * Created by Sales on 24.09.2015.
 */

public class DBTable {

    private StoredKey lastKey;

    public DBTable(String table_name){
        lastKey = new StoredKey(table_name);
    }
}
