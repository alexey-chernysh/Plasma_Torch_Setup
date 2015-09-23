/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sales on 23.09.2015.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DataBaseHelper.class.getName();

    private static final String DB_NAME = "cut_chart.sqlite";
    private static final String DB_FOLDER = "/data/data/" + App.getInstance().getPackageName() + "/databases/";
    private static final String DB_PATH = DB_FOLDER + DB_NAME;
    private static final String DB_ASSETS_PATH = "db/" + DB_NAME;
    private static final int DB_VERSION = 1;
    private static final int DB_FILES_COPY_BUFFER_SIZE = 8192;

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        throw new SQLiteException(
                "Call DataBaseHelper.Initialize first. This method should never be called.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException(
                "Call DataBaseHelper.Initialize first. This method should never be called.");
    }
    /**
     * Инициализирует базу данных. Копирует базу из ресурсов приложения, если на не
     * существует или ее версия устарела. Вызвать перед тем как использовать базу.
     *
     * @throws ChainedSQLiteException
     *             если инициализацию не удалось выполнить
     */
    public static void Initialize() throws ChainedSQLiteException {
        if (isInitialized() == false) {
            copyInialDBfromAssets();
        }
    }

    private static boolean isInitialized() {

        SQLiteDatabase checkDB = null;
        Boolean correctVersion = false;

        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            correctVersion = checkDB.getVersion() == DB_VERSION;
        } catch (SQLiteException e) {
            Log.w(LOG_TAG, e.getMessage());
        } finally {
            if (checkDB != null)
                checkDB.close();
        }

        return checkDB != null && correctVersion;
    }

    /**
     * Копирует файл базы данных из Assets в директорию для баз данных этого
     * приложения
     *
     * @throws ChainedSQLiteException
     *             если что-то пошло не так при компировании
     */
    private static void copyInialDBfromAssets() throws ChainedSQLiteException {

        Context appContext = App.getInstance().getApplicationContext();
        InputStream inStream = null;
        OutputStream outStream = null;

        try {
            inStream = new BufferedInputStream(appContext.getAssets().open(DB_ASSETS_PATH), DB_FILES_COPY_BUFFER_SIZE);
            File dbDir = new File(DB_FOLDER);
            if (dbDir.exists() == false)
                dbDir.mkdir();
            outStream = new BufferedOutputStream(new FileOutputStream(DB_PATH), DB_FILES_COPY_BUFFER_SIZE);

            byte[] buffer = new byte[DB_FILES_COPY_BUFFER_SIZE];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }

            outStream.flush();
            outStream.close();
            inStream.close();

        } catch (IOException ex) {
            // Что-то пошло не так
            Log.e(LOG_TAG, ex.getMessage());
            throw new ChainedSQLiteException("Fail to copy initial db from assets", ex);
        } finally {
            IOUtils.closeSilent(outStream);
            IOUtils.closeSilent(inStream);
        }
    }
}
