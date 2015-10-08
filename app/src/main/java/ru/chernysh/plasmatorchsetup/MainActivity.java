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

        int seriesKey = 0;
        int brandKey = 0;
        int lastModel = (new StoredKey(getString(R.string.LAST_INVERTER_MODEL))).get();
        if(lastModel > 0){
            seriesKey = getSeriesKeyForModel(db, lastModel);
            brandKey = getBrandKeyForSeries(db, seriesKey);
        }
        prepareModelSpinner(db, lastModel, seriesKey);
        prepareSeriesSpinner(db, seriesKey, brandKey);
        prepareBrandSpinner(db, brandKey);
    }

    private int getSeriesKeyForModel(SQLiteDatabase db, int modelKey) {
        // find series for selected model
        Cursor cursor = db.query(getString(R.string.MODEL_TABLE_NAME),
                null,
                getString(R.string.KEY_IS_EQUAL_TO) + modelKey,
                null, null, null, null);
        int key = 0;
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(getString(R.string.SERIES_TABLE_NAME));
            key = cursor.getInt(index);
        };
        cursor.close();
        return key;
    }

    private int getBrandKeyForSeries(SQLiteDatabase db, int seriesKey){
        // find brand for selected series
        Cursor cursor = db.query(getString(R.string.SERIES_TABLE_NAME),
                                            null,
                                            getString(R.string.KEY_IS_EQUAL_TO) + seriesKey,
                                            null, null, null, null);
        int key = 0;
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(getString(R.string.BRAND_TABLE_NAME));
            key = cursor.getInt(index);
        };
        cursor.close();
        return key;
    }

    private void prepareModelSpinner(SQLiteDatabase db, int modelKey, int seriesKey) {

        ((Spinner)findViewById(R.id.modelName))
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {
                            Log.d(LOG_TAG, "Model spinner selection listener activated");
                            Log.d(LOG_TAG, "with position = " + pos);
                            Log.d(LOG_TAG, "with name = " + parent.getAdapter().getItem(pos));
                            ((CustomAdapter) parent.getAdapter()).setSelected();
                        }
                    });
        updateModelSpinner(db, modelKey, seriesKey);
   }

    private void prepareSeriesSpinner(SQLiteDatabase db, int seriesKey, int brandKey) {
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
                            ((CustomAdapter) parent.getAdapter()).setSelected();
                        }
                    });
        updateSeriesSpinnerList(db, seriesKey, brandKey);
    }

    private void prepareBrandSpinner(SQLiteDatabase db, int selectedBrandKey) {
        ((Spinner)findViewById(R.id.brandName))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        Log.d(LOG_TAG, "Brand spinner selection listener activated");
                        Log.d(LOG_TAG, "with position = " + pos);
                        ((CustomAdapter) parent.getAdapter()).setSelected();
                    }
                });
        updateBrandSpinnerList(db, selectedBrandKey);
    }

    private void updateBrandSpinnerList(SQLiteDatabase db, int selectedBrandKey){
        Cursor cursor = db.query(getString(R.string.BRAND_TABLE_NAME), null, null, null, null, null, null);
        int nOfBrands = cursor.getCount();
        String[] brandName = new String[nOfBrands];
        int[] brandKey = new int[nOfBrands];
        if (cursor.moveToFirst()) {
            int brandNameIndex = cursor.getColumnIndex("name");
            int brandKeyIndex = cursor.getColumnIndex("key");
            for(int i=0; i<nOfBrands;i++){
                brandName[i] = cursor.getString(brandNameIndex);
                brandKey[i] = cursor.getInt(brandKeyIndex);
                cursor.moveToNext();
            }
        } else Log.d(LOG_TAG, "0 rows");
        cursor.close();

        Spinner brandSpinner = (Spinner)findViewById(R.id.brandName);
        CustomAdapter brandAdapter =
                new CustomAdapter(this, android.R.layout.simple_spinner_item, brandName);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);
        if(selectedBrandKey > 0)
        for(int i=0; i<nOfBrands;i++){
            if(brandKey[i] == selectedBrandKey) {
                brandSpinner.setSelection(i);
                brandAdapter.setSelected();
            }
        } else brandAdapter.setUndefined();
    }

    private void updateSeriesSpinnerList(SQLiteDatabase db, int selectedSeriesKey, int brandKey){
        String[] seriesNames = null;
        int[] seriesKeys = null;
        int nOfSeries = 0;

        if(brandKey > 0) {
            Cursor cursor = db.query(getString(R.string.SERIES_TABLE_NAME),
                    null,
                    getString(R.string.BRAND_TABLE_NAME) + " == " + brandKey,
                    null, null, null, null);
            nOfSeries = cursor.getCount();
            if(nOfSeries > 0){
                seriesNames = new String[nOfSeries];
                seriesKeys = new int[nOfSeries];
                if (cursor.moveToFirst()) {
                    int seriesNameIndex = cursor.getColumnIndex("name");
                    int seriesKeyIndex = cursor.getColumnIndex("key");
                    for(int i=0; i<nOfSeries; i++){
                        seriesNames[i] = cursor.getString(seriesNameIndex);
                        seriesKeys[i] = cursor.getInt(seriesKeyIndex);
                        cursor.moveToNext();
                    }
                } else Log.d(LOG_TAG, "0 rows");
                cursor.close();
            }
        };

        Spinner seriesSpinner = (Spinner) findViewById(R.id.seriesName);
        CustomAdapter seriesAdapter =
                new CustomAdapter(this, android.R.layout.simple_spinner_item, seriesNames);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesSpinner.setAdapter(seriesAdapter);
        if(selectedSeriesKey > 0){
            for(int i=0; i<nOfSeries; i++){
                if(seriesKeys[i] == selectedSeriesKey){
                    seriesSpinner.setSelection(i);
                    seriesAdapter.setSelected();
                }
            }
        } else seriesAdapter.setUndefined();

    };

    private void updateModelSpinner(SQLiteDatabase db, int selectedModelKey, int seriesKey){
        String[] modelName = null;
        int[] modelKey = null;
        int nOfModels = 0;

        if(seriesKey > 0){
            String selection = "series == " + seriesKey;
            Cursor modelCursor = db.query("models", null, selection, null, null, null, null);
            nOfModels = modelCursor.getCount();
            Log.d(LOG_TAG, "updateModelSpinner, nOfModels = " + nOfModels);
            if(nOfModels > 0){
                modelName = new String[nOfModels];
                modelKey = new int[nOfModels];
                if (modelCursor.moveToFirst()) {
                    int nameIndex = modelCursor.getColumnIndex("name");
                    int keyIndex = modelCursor.getColumnIndex("key");
                    for(int i=0; i<nOfModels; i++){
                        modelName[i] = modelCursor.getString(nameIndex);
                        modelKey[i] = modelCursor.getInt(keyIndex);
                        modelCursor.moveToNext();
                    }
                };
            }
            modelCursor.close();
        }

        Spinner modelSpinner = (Spinner)findViewById(R.id.modelName);
        CustomAdapter modelAdapter =
                new CustomAdapter(this, android.R.layout.simple_spinner_item, modelName);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(modelAdapter);
        if(selectedModelKey > 0){
            for(int i=0; i<nOfModels; i++){
                if(modelKey[i] == selectedModelKey){
                    modelSpinner.setSelection(i);
                    modelAdapter.setSelected();
                }
            }
        } else modelAdapter.setUndefined();
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
