/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class InitialActivity extends Activity {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_iniitial);

        progressDialog = showInitProgressDialog();
        new AppInitializerTask().execute((Void) null);
    }

    private ProgressDialog showInitProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Инициализация...");
        dialog.show();
        return dialog;
    }

    private void showInitErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ошибка =(")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        InitialActivity.this.finish();
                    }})
                .create();
        dialog.show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class AppInitializerTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                DataBaseHelper.Initialize();
            } catch (SQLiteException ex) {
                Log.d("SQLiteException: ", ex.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            if (result) {
                InitialActivity.this.finish();
                startMainActivity();
            } else showInitErrorDialog("Сбой инициализации приложения");
        }
    }
}