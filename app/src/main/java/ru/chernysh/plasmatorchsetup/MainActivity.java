package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataBaseHelper dbHelper = new DataBaseHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int seriesKey = prepareModelSpinner(db);
        int brandKey = prepareSeriesSpinner(db, seriesKey);
        prepareBrandSpinner(db, brandKey);
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
        CustomAdapter brandAdapter =
                new CustomAdapter(this, android.R.layout.simple_spinner_item, brandName);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);
        for(int i=0;i<nOfBrands;i++)
            if(brandKey[i] == selectedBrandKey) brandSpinner.setSelection(i);
    }

    private int prepareSeriesSpinner(SQLiteDatabase db, int seriesKey) {
        // find brand for selected series
        String selection = "key == " + seriesKey;
        Cursor seriesCursor = db.query("series", null, selection, null, null, null, null);
        Log.d(LOG_TAG, "Cursor position in series table " + seriesCursor);
        int brandKey = 0;
        if (seriesCursor.moveToFirst()) {
            int brandIndex = seriesCursor.getColumnIndex("manufacturer");
            Log.d(LOG_TAG, "brandIndex " + brandIndex);
            brandKey = seriesCursor.getInt(brandIndex);
        };

        updateSeriesSpinner(db, seriesKey, brandKey);
        ((Spinner) findViewById(R.id.seriesName))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        return brandKey;
    }

    private void updateSeriesSpinner(SQLiteDatabase db, int seriesKey, int brandKey){
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
        CustomAdapter seriesAdapter =
                new CustomAdapter(this, android.R.layout.simple_spinner_item, seriesNames);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesSpinner.setAdapter(seriesAdapter);
        for(int i=0; i<nOfSeries; i++)
            if(seriesKeys[i] == seriesKey) seriesSpinner.setSelection(i);
    };

    /*
        preparing spinner for models list
        all items in list from same series such selected model
        return - series key field value for selected model
     */
    private int prepareModelSpinner(SQLiteDatabase db) {
        StoredKey lastModelKey = new StoredKey("models");

        // restore last saved model selection or choose model #1
        int modelKey = lastModelKey.get();
        if(modelKey <= 0) modelKey = 1;

        // find series of selected model
        String selection = "key == " + modelKey;
        Cursor modelCursor = db.query("models", null, selection, null, null, null, null);
        Log.d(LOG_TAG, "Cursor position in models table " + modelCursor);
        int seriesKey = 0;
        if (modelCursor.moveToFirst()) {
            int seriesIndex = modelCursor.getColumnIndex("series");
            seriesKey = modelCursor.getInt(seriesIndex);
        };
        Log.d(LOG_TAG, "Model key = " + modelKey + " Series key = " + seriesKey);

        updateModelSpinner(db, modelKey, seriesKey);
        ((Spinner)findViewById(R.id.modelName))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        StoredKey lastModelKey = new StoredKey("models");
                        lastModelKey.set(pos);
                        Log.d(LOG_TAG, "Model spinner selection listener activated");
                        Log.d(LOG_TAG, "with position = " + pos);

                    }
                });
        return seriesKey;
    }

    private void updateModelSpinner(SQLiteDatabase db, int selectedKey, int seriesKey){
        String selection = "series == " + seriesKey;
        Cursor modelCursor = db.query("models", null, selection, null, null, null, null);
        int nOfModels = modelCursor.getCount();
        String[] modelName;
        int[] modelKey;
        if(nOfModels > 0){
            modelName = new String[nOfModels];
            modelKey = new int[nOfModels];
            if (modelCursor.moveToFirst()) {
                int brandIndex = modelCursor.getColumnIndex("brand");
                int keyIndex = modelCursor.getColumnIndex("key");
                for(int i=0; i<nOfModels;i++){
                    modelName[i] = modelCursor.getString(brandIndex);
                    modelKey[i] = modelCursor.getInt(keyIndex);
                    modelCursor.moveToNext();
                }
            };
        }
        modelCursor.close();

        Spinner modelSpinner = (Spinner)findViewById(R.id.modelName);
        CustomAdapter modelAdapter =
                new CustomAdapter(this, android.R.layout.simple_spinner_item, modelName);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(modelAdapter);
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
