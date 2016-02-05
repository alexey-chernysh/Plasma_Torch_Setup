/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import java.util.Locale;

public class App extends android.app.Application {

    public final static String language = Locale.getDefault().getLanguage();

    private static App instance;

    public App() {
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public static String getResourceString(int id){
        return instance.getString(id);
    }

}