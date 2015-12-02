/*
 * Copyright (c) 2015. Alexey Chernysh? Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.content.Context;
import android.database.Cursor;
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

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DataBaseHelper.class.getName() + ": ";

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
    public static void Initialize() {
        if (!isInitialized()) {
            copyInitialDBFromAssets();
        }
    }

    private static boolean isInitialized() {

        String db_path =  getDBPath();

        if(new File(db_path).isFile()){
            SQLiteDatabase checkDB = null;

            try {
                checkDB = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
                Log.w(LOG_TAG, e.getMessage());
            }
            if (checkDB != null) checkDB.close();
            return (checkDB != null);
        } else return false;

    }

    /**
     * Копирует файл базы данных из Assets в директорию для баз данных этого
     * приложения
     */
    private static void copyInitialDBFromAssets() {

        Context appContext = App.getInstance().getApplicationContext();
        InputStream inStream = null;
        OutputStream outStream = null;

        try {
            String db_assets_path = getAssetPath();
            inStream = new BufferedInputStream(appContext.getAssets().open(db_assets_path), DB_FILES_COPY_BUFFER_SIZE);
            String db_folder = getDBDir();
            File dbDir = new File(db_folder);
            if (!dbDir.exists())
                if(dbDir.mkdir())
                    throw new IOException("Can't create database dir");
            String dbPath = getDBPath();
            outStream = new BufferedOutputStream(new FileOutputStream(dbPath), DB_FILES_COPY_BUFFER_SIZE);

            byte[] buffer = new byte[DB_FILES_COPY_BUFFER_SIZE];
            int length;
            while ((length = inStream.read(buffer)) > 0)
                outStream.write(buffer, 0, length);

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

    public static String getNameColumnHeader(Cursor cursor){
        String neutralNameHeader = App.getResourceString(R.string.name_field);
        String nameHeader = neutralNameHeader
                + "_"
                + App.language;
        int nameIndex = cursor.getColumnIndex(nameHeader);
        if(nameIndex >= 0) return nameHeader;
        nameHeader = neutralNameHeader
                + "_"
                + App.getResourceString(R.string.english_language);
        nameIndex = cursor.getColumnIndex(nameHeader);
        if(nameIndex >= 0) return nameHeader;
        nameHeader = neutralNameHeader;
        nameIndex = cursor.getColumnIndex(nameHeader);
        if(nameIndex >= 0) return nameHeader;
        else return null;
    }

    public static String getFilterEqualTo(int nameId, int value){
        return App.getResourceString(nameId) + "==" + value;
    }

    public static String getNameByKey(SQLiteDatabase db, String tableName, int key) {
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        int processNameIndex = cursor.getColumnIndex(getNameColumnHeader(cursor));
        String filter = App.getResourceString(R.string.key_field)
                      + App.getResourceString(R.string.is_equal_to)
                      + key;
        cursor = db.query(tableName, null, filter, null, null, null, null);
        String result = null;
        if (cursor.moveToFirst()) result = cursor.getString(processNameIndex);
        cursor.close();
        return result;
    }

    private static String getDBDir(){
        return    App.getInstance().getString(R.string.sd_card)
                + App.getInstance().getPackageName()
                + "/"
                + App.getInstance().getString(R.string.db_folder);
    }

    private static String getDBPath(){
        return    getDBDir()
                + "/"
                + getDBName();
    }

    private static String getDBName() {
        return App.getInstance().getString(R.string.db_name);
    }

    private static String getAssetDir(){
        return App.getInstance().getString(R.string.db_folder);
    }

    private static String getAssetPath(){
        return    getAssetDir()
                + "/"
                + getDBName();
    }
}
