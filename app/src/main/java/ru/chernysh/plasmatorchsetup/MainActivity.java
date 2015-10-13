package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";
    private static final String[] stringNullArray = {""};
    private static final int[] intNullArray = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");
        initInverterSpinners();
    }

    private void initInverterSpinners(){
        Log.d(LOG_TAG, "initInverterSpinners");
//        int lastModel = (new StoredKey(getString(R.string.INVERTER_MODEL))).get();
        int lastModel = 0;
        int lastSeries = 0;
        int lastBrand = 0;
        if(lastModel > 0) {
            lastSeries = getSeriesKeyForModel(lastModel);
            lastBrand = getBrandKeyForSeries(lastSeries);
        } else {
            lastSeries = (new StoredKey(getString(R.string.INVERTER_SERIES))).get();
            if(lastSeries > 0){
                lastBrand = getBrandKeyForSeries(lastSeries);
            } else {
                lastSeries = (new StoredKey(getString(R.string.INVERTER_BRAND))).get();
            };
        };
        initBrandSpinner(lastBrand);
        initSeriesSpinner(lastSeries, lastBrand);
        initModelSpinner(lastModel, lastSeries);
    }

    private int getSeriesKeyForModel(int modelKey) {
        // find series for selected model
        Log.d(LOG_TAG, "called getSeriesKeyForModel(" + modelKey + ")");
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        Cursor cursor = db.query(getString(R.string.MODEL_TABLE_NAME),
                                            null,
                                            getString(R.string.KEY_IS_EQUAL_TO) + modelKey,
                                            null, null, null, null);
        int seriesKey = 0;
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(getString(R.string.SERIES_TABLE_NAME));
            seriesKey = cursor.getInt(index);
        };
        cursor.close();
        db.close();
        Log.d(LOG_TAG, "getSeriesKeyForModel return value " + seriesKey);
        return seriesKey;
    }

    private int getBrandKeyForSeries(int seriesKey){
        // find brand for selected series
        Log.d(LOG_TAG, "called getBrandKeyForSeries(" + seriesKey + ")");
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        Cursor cursor = db.query(getString(R.string.SERIES_TABLE_NAME),
                                            null,
                                            getString(R.string.KEY_IS_EQUAL_TO) + seriesKey,
                                            null, null, null, null);
        int brandKey = 0;
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(getString(R.string.BRAND_TABLE_NAME));
            brandKey = cursor.getInt(index);
        };
        cursor.close();
        db.close();
        Log.d(LOG_TAG, "getBrandKeyForSeries return value " + brandKey);
        return brandKey;
    }

    private void initBrandSpinner(int selectedBrandKey) {
        Log.d(LOG_TAG, "called initBrandSpinner(" + selectedBrandKey + ")");
        ((Spinner)findViewById(R.id.brandName))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        CustomAdapter brandAdapter = (CustomAdapter) parent.getAdapter();
                        if (brandAdapter != null) {
                            int selectedKey = brandAdapter.getKey(pos);
                            (new StoredKey(getString(R.string.INVERTER_BRAND))).set(selectedKey);
                            updateSeriesSpinnerList(0, selectedKey);
                            updateModelSpinnerList(0, 0);
                        }
                    }
                });
        updateBrandSpinnerList(selectedBrandKey);
    }

    private void initSeriesSpinner(int selectedSeriesKey, int selectedBrandKey) {
        Log.d(LOG_TAG, "called initSeriesSpinner(" + selectedSeriesKey + "," + selectedBrandKey + ")");
        Spinner seriesSpinner = (Spinner) findViewById(R.id.seriesName);
        seriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                CustomAdapter adapter = (CustomAdapter) parent.getAdapter();
                if (adapter != null) {
                    int selectedKey = adapter.getKey(pos);
                    (new StoredKey(getString(R.string.INVERTER_SERIES))).set(selectedKey);
                    updateModelSpinnerList(0, selectedKey);
                }
            }
        });
        seriesSpinner.setEnabled(false);
        updateSeriesSpinnerList(selectedSeriesKey, selectedBrandKey);
    }

    private void initModelSpinner(int selectedModelKey, int selectedSeriesKey) {
        Log.d(LOG_TAG, "called initModelSpinner(" + selectedModelKey + "," + selectedSeriesKey + ")");
        Spinner modelSpinner = (Spinner) findViewById(R.id.modelName);
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(LOG_TAG, "onNothingSelect in modelSpinner");
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Log.d(LOG_TAG, "onSelect in modelSpinner");
                Log.d(LOG_TAG, "Selection in model spinner with pos = " + pos);
                CustomAdapter adapter = (CustomAdapter) parent.getAdapter();
                if (adapter != null) {
                    int selectedKey = adapter.getKey(pos);
                    (new StoredKey(getString(R.string.INVERTER_MODEL))).set(selectedKey);
                }
            }
        });
        updateModelSpinnerList(selectedModelKey, selectedSeriesKey);
    }

    private void updateBrandSpinnerList(int selectedBrandKey){
        Log.d(LOG_TAG, "called updateBrandSpinnerList(" + selectedBrandKey + ")");

        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        Cursor cursor = db.query(getString(R.string.BRAND_TABLE_NAME), null, null, null, null, null, null);
        int nOfBrands = cursor.getCount();
        Log.d(LOG_TAG, "nOfBrands = " + nOfBrands);
        String[] brandName = new String[nOfBrands];
        int[] brandKey = new int[nOfBrands];
        if (cursor.moveToFirst()) {
            int brandNameIndex = cursor.getColumnIndex("name");
            int brandKeyIndex = cursor.getColumnIndex("key");
            for(int i=0; i<nOfBrands;i++){
                brandName[i] = cursor.getString(brandNameIndex);
                brandKey[i] = cursor.getInt(brandKeyIndex);
                Log.d(LOG_TAG, "record [" + i + "] : name = " + brandName[i] + ", key = " + brandKey[i]);
                cursor.moveToNext();
            }
        } else Log.d(LOG_TAG, "0 rows");
        cursor.close();
        db.close();

        Spinner brandSpinner = (Spinner)findViewById(R.id.brandName);
        if(brandSpinner != null){
            CustomAdapter brandAdapter =
                    new CustomAdapter(this, android.R.layout.simple_spinner_item, brandName, brandKey);
            brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            brandSpinner.setAdapter(brandAdapter);
            for (int i = 0; i < nOfBrands; i++)
                if (brandKey[i] == selectedBrandKey) brandSpinner.setSelection(i);
        }
    }

    private void updateSeriesSpinnerList(int selectedSeriesKey, int selectedBrandKey){
        Log.d(LOG_TAG, "called updateSeriesSpinnerList(" + selectedSeriesKey + "," + selectedBrandKey + ")");
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        String[] seriesName = stringNullArray;
        int[] seriesKey = intNullArray;
        int nOfSeries = 0;

        if(selectedBrandKey > 0) {
            Cursor cursor = db.query(getString(R.string.SERIES_TABLE_NAME),
                    null,
                    getString(R.string.BRAND_TABLE_NAME) + " == " + selectedBrandKey,
                    null, null, null, null);
            nOfSeries = cursor.getCount();
            Log.d(LOG_TAG, "nOfSeries = " + nOfSeries);
            if(nOfSeries > 0){
                seriesName = new String[nOfSeries];
                seriesKey = new int[nOfSeries];
                if (cursor.moveToFirst()) {
                    int seriesNameIndex = cursor.getColumnIndex("name");
                    int seriesKeyIndex = cursor.getColumnIndex("key");
                    for(int i=0; i<nOfSeries; i++){
                        seriesName[i] = cursor.getString(seriesNameIndex);
                        seriesKey[i] = cursor.getInt(seriesKeyIndex);
                        Log.d(LOG_TAG, "record [" + i + "] : name = " + seriesName[i] + ", key = " + seriesKey[i]);
                        cursor.moveToNext();
                    }
                } else Log.d(LOG_TAG, "0 rows");
                cursor.close();
            }
        };
        db.close();

        Spinner seriesSpinner = (Spinner) findViewById(R.id.seriesName);
        if(seriesSpinner != null){
            CustomAdapter seriesAdapter =
                    new CustomAdapter(this, android.R.layout.simple_spinner_item, seriesName, seriesKey);
            seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seriesSpinner.setAdapter(seriesAdapter);
            if(selectedSeriesKey > 0){
                for(int i=0; i<nOfSeries; i++){
                    if(seriesKey[i] == selectedSeriesKey){
                        seriesSpinner.setSelection(i);
                    }
                }
            }
            seriesSpinner.setEnabled(selectedBrandKey > 0);
        }
    };

    private void updateModelSpinnerList(int selectedModelKey, int selectedSeriesKey){
        Log.d(LOG_TAG, "called updateModelSpinnerList(" + selectedModelKey + "," + selectedSeriesKey + ")");
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        String[] modelName = stringNullArray;
        int[] modelKey = intNullArray;
        int nOfModels = 0;

        if(selectedSeriesKey > 0){
            Cursor cursor = db.query(getString(R.string.MODEL_TABLE_NAME),
                                            null,
                                            getString(R.string.SERIES_TABLE_NAME) + " == " + selectedSeriesKey,
                                            null, null, null, null);
            nOfModels = cursor.getCount();
            if(nOfModels > 0){
                modelName = new String[nOfModels];
                modelKey = new int[nOfModels];
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex("name");
                    int keyIndex = cursor.getColumnIndex("key");
                    for(int i=0; i<nOfModels; i++){
                        modelName[i] = cursor.getString(nameIndex);
                        modelKey[i] = cursor.getInt(keyIndex);
                        Log.d(LOG_TAG, "record [" + i + "] : name = " + modelName[i] + ", key = " + modelKey[i]);
                        cursor.moveToNext();
                    }
                };
            }
            cursor.close();
        }
        db.close();

        Spinner modelSpinner = (Spinner)findViewById(R.id.modelName);
        if(modelSpinner != null){
            CustomAdapter modelAdapter =
                    new CustomAdapter(this, android.R.layout.simple_spinner_item, modelName, modelKey);
            modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            modelSpinner.setAdapter(modelAdapter);
            if(selectedModelKey > 0){
                for(int i=0; i<nOfModels; i++){
                    if(modelKey[i] == selectedModelKey){
                        modelSpinner.setSelection(i);
                    }
                }
            }
            modelSpinner.setEnabled(selectedSeriesKey > 0);
        }
    }
}
