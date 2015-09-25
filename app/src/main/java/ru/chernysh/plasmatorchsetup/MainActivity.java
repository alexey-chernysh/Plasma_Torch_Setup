package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    private static final String[] SERIES = new String[] {
            "Powermax", "Autocut", "HPR"
    };

    StoredKey lastModelKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastModelKey = new StoredKey("models");

        DataBaseHelper dbHelper = new DataBaseHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // restore last saved model selection or choose model #1
        int modelKey = lastModelKey.get();
        if(modelKey <= 0) modelKey = 1; // костыль

        // find series of selected model
        String selection = "key == " + modelKey;
        Cursor modelCursor = db.query("models", null, selection, null, null, null, null);
        Log.d(LOG_TAG, "Cursor position in models table " + modelCursor.toString());
        int seriesKey = 0;
        if (modelCursor.moveToFirst()) {
            int seriesIndex = modelCursor.getColumnIndex("series");
            seriesKey = modelCursor.getInt(seriesIndex);
        } else Log.d(LOG_TAG, "Something gone wrong");
        if(seriesKey <= 0) seriesKey = 1; // костыль

        // find brand for selected series
        selection = "key == " + seriesKey;
        Cursor seriesCursor = db.query("series", null, selection, null, null, null, null);
        Log.d(LOG_TAG, "Cursor position in models table " + seriesCursor.toString());
        int brandKey = 0;
        if (modelCursor.moveToFirst()) {
            int brandIndex = modelCursor.getColumnIndex("manufacturers");
            brandKey = modelCursor.getInt(brandIndex);
        } else Log.d(LOG_TAG, "Something gone wrong");
        if(brandKey <= 0) brandKey = 1; // костыль

        prepareBrandSpinner(db, brandKey);
        prepareSeriesSpinner(db, seriesKey, brandKey);

    }

    private void prepareBrandSpinner(SQLiteDatabase db, int selectedBrandKey) {
        Spinner brandSpinner = (Spinner)findViewById(R.id.brandName);
        updateBrandSpinner(db, selectedBrandKey);
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Log.d(LOG_TAG, "Brand spinner selection listener activated");
                Log.d(LOG_TAG, "with position = " + pos);
            }
        });
    }

    private void updateBrandSpinner(SQLiteDatabase db, int selectedBrandKey){
        Cursor cursor = db.query("manufacturers", null, null, null, null, null, null);
        int nOfBrands = cursor.getCount();
        String[] brandName = new String[nOfBrands];
        int[] brandKey = new int[nOfBrands];
        if (cursor.moveToFirst()) {
            int brandIndex = cursor.getColumnIndex("brand");
            int keyIndex = cursor.getColumnIndex("key");
            for(int i=0; i<nOfBrands;i++){
                brandName[i] = cursor.getString(brandIndex);
                brandKey[i] = cursor.getInt(keyIndex);
                cursor.moveToNext();
            }
        } else Log.d(LOG_TAG, "0 rows");
        cursor.close();

        Spinner brandSpinner = (Spinner)findViewById(R.id.brandName);
        ArrayAdapter<String> brandAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, brandName);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);
        for(int i=0;i<nOfBrands;i++)
            if(brandKey[i] == selectedBrandKey) brandSpinner.setSelection(i);
    }

    private void prepareSeriesSpinner(SQLiteDatabase db, int seriesKey, int brandKey) {
        String selection = "manufacturer == " + brandKey;
        Cursor cursor = db.query("series", null, selection, null, null, null, null);
        int nOfSeries = cursor.getCount();
        String[] seriesNames = new String[nOfSeries];
        int[] seriesKeys = new int[nOfSeries];
        if (cursor.moveToFirst()) {
            int brandIndex = cursor.getColumnIndex("name");
            int keyIndex = cursor.getColumnIndex("key");
            for(int i=0; i<nOfSeries; i++){
                seriesNames[i] = cursor.getString(brandIndex);
                seriesKeys[i] = cursor.getInt(keyIndex);
                cursor.moveToNext();
            }
        } else Log.d(LOG_TAG, "0 rows");
        cursor.close();

        Spinner seriesSpinner = (Spinner) findViewById(R.id.seriesName);
        ArrayAdapter<String> seriesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, seriesNames);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesSpinner.setAdapter(seriesAdapter);
        for(int i=0; i<nOfSeries; i++)
            if(seriesKeys[i] == seriesKey) seriesSpinner.setSelection(i);
        seriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Log.d(LOG_TAG, "Series spinner selection listener activated");
                Log.d(LOG_TAG, "with position = " + pos);
            }
        });
    }

    private void prepareModelSpinner(SQLiteDatabase db) {
        Cursor cursor = db.query("models", null, null, null, null, null, null);
        int nOfBrands = cursor.getCount();
        String[] brandName = new String[nOfBrands];
        int[] brandKey = new int[nOfBrands];
        if (cursor.moveToFirst()) {
            int brandIndex = cursor.getColumnIndex("brand");
            int keyIndex = cursor.getColumnIndex("key");
            for(int i=0; i<nOfBrands;i++){
                brandName[i] = cursor.getString(brandIndex);
                brandKey[i] = cursor.getInt(keyIndex);
                cursor.moveToNext();
            }
        } else Log.d(LOG_TAG, "0 rows");
        cursor.close();

        Spinner brandSpinner = (Spinner)findViewById(R.id.brandName);
        ArrayAdapter<String> brandAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, brandName);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Log.d(LOG_TAG, "Brand spinner selection listener activated");
                Log.d(LOG_TAG, "with parent = " + parent.toString());
                Log.d(LOG_TAG, "with view = " + view.toString());
                Log.d(LOG_TAG, "with position = " + pos);
                Log.d(LOG_TAG, "and id = " + id);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addBrand:
                Log.d(LOG_TAG, "Add new brand");
                break;
            case R.id.deleteBrand:
                Log.d(LOG_TAG, "Delete brand brand");
                break;
            default:
                Log.d(LOG_TAG, "Unknown action!");
        }
    }
}
