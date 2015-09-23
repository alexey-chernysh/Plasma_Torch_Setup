/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

/**
 * Created by Sales on 23.09.2015.
 */
public class App extends android.app.Application {
    private static App instance;
    public App() {instance = this;}
    public static App getInstance() {return instance;}
}