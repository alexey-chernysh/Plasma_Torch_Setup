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

import org.apache.commons.io.IOUtils;

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

    private static final String LOG_TAG = DataBaseHelper.class.getName() + ": ";

//    private static final String DB_NAME = "cut_chart.sqlite";
//    private static final String DB_FOLDER = "/data/data/" + App.getInstance().getPackageName() + "/databases/";
//    private static final String DB_PATH = DB_FOLDER + DB_NAME;
//    private static final String DB_ASSETS_PATH = "database/" + DB_NAME;
    private static final int DB_VERSION = 1;
    private static final int DB_FILES_COPY_BUFFER_SIZE = 8192;

    public DataBaseHelper() {
        super(  App.getInstance().getBaseContext(),
                App.getInstance().getString(R.string.db_name),
                null,
                1);
    }

    private final static String msg = "Call DataBaseHelper.Initialize first. This method should never be called..";

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.d(LOG_TAG, msg);
//        throw new SQLiteException(msg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, msg);
        throw new SQLiteException(msg);
    }
    /**
     * Инициализирует базу данных. Копирует базу из ресурсов приложения, если на не
     * существует или ее версия устарела. Вызвать перед тем как использовать базу.
     *
     * @throws SQLiteException
     *             если инициализацию не удалось выполнить
     */
    public static void Initialize() throws SQLiteException {
        if (isInitialized() == false) {
            copyInialDBfromAssets();
        }
    }

    private static boolean isInitialized() {

        SQLiteDatabase checkDB = null;
        Boolean correctVersion = false;

        try {
            String db_path =  App.getInstance().getString(R.string.sd_card)
                            + App.getInstance().getPackageName()
                            + "/" + App.getInstance().getString(R.string.db_folder) + "/"
                            + App.getInstance().getString(R.string.db_name);
            checkDB = SQLiteDatabase.openDatabase(  db_path,
                                                    null,
                                                    SQLiteDatabase.OPEN_READONLY);
            int currentVersion = checkDB.getVersion();
            correctVersion = (currentVersion == DB_VERSION);
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
     * @throws SQLiteException
     *             если что-то пошло не так при компировании
     */
    private static void copyInialDBfromAssets() throws SQLiteException {

        Context appContext = App.getInstance().getApplicationContext();
        InputStream inStream = null;
        OutputStream outStream = null;

        try {
            String db_assets_path = App.getInstance().getString(R.string.db_folder)
                                  + "/"
                                  + App.getInstance().getString(R.string.db_name);
            inStream = new BufferedInputStream(appContext.getAssets().open(db_assets_path), DB_FILES_COPY_BUFFER_SIZE);
            String db_folder = App.getInstance().getString(R.string.sd_card)
                             + App.getInstance().getPackageName()
                             + "/" + App.getInstance().getString(R.string.db_folder) + "/";
            File dbDir = new File(db_folder);
            if (dbDir.exists() == false)
                dbDir.mkdir();
            String db_path = db_folder + App.getInstance().getString(R.string.db_name);
            outStream = new BufferedOutputStream(new FileOutputStream(db_path), DB_FILES_COPY_BUFFER_SIZE);

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
            throw new SQLiteException("Fail to copy initial database from assets", ex);
        } finally {
            IOUtils.closeQuietly(outStream);
            IOUtils.closeQuietly(inStream);
        }
    }
}
