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

        initInverterSpinners();
    }

    private void initInverterSpinners(){
        int lastModel = (new StoredKey(getString(R.string.INVERTER_MODEL))).get();
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
        initModelSpinner(lastModel, lastSeries);
        initSeriesSpinner(lastSeries, lastBrand);
        initBrandSpinner(lastBrand);
    }

    private int getSeriesKeyForModel(int modelKey) {
        // find series for selected model
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

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
        db.close();
        return key;
    }

    private int getBrandKeyForSeries(int seriesKey){
        // find brand for selected series
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

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
        db.close();
        return key;
    }

    private void initBrandSpinner(int selectedBrandKey) {
        ((Spinner)findViewById(R.id.brandName))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        Log.d(LOG_TAG, "Selection in brand spinner with pos = " + pos);
                        CustomAdapter adapter = (CustomAdapter) parent.getAdapter();
                        if (adapter != null) {
                            adapter.setSelected();
                            int selectedKey = adapter.getKey(pos);
                            (new StoredKey(getString(R.string.INVERTER_BRAND))).set(selectedKey);
                            updateSeriesSpinnerList(0, selectedKey);
                            updateModelSpinnerList(0, 0);
                        }
                    }
                });
        updateBrandSpinnerList(selectedBrandKey);
    }

    private void initSeriesSpinner(int seriesKey, int brandKey) {
        ((Spinner) findViewById(R.id.seriesName))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        Log.d(LOG_TAG, "Selection in series spinner with pos = " + pos);
                        CustomAdapter adapter = (CustomAdapter) parent.getAdapter();
                        if (adapter != null) {
                            adapter.setSelected();
                            int selectedKey = adapter.getKey(pos);
                            (new StoredKey(getString(R.string.INVERTER_SERIES))).set(selectedKey);
                            updateModelSpinnerList(0, selectedKey);
                        }
                    }
                });
        updateSeriesSpinnerList(seriesKey, brandKey);
    }

    private void initModelSpinner(int modelKey, int seriesKey) {

        ((Spinner)findViewById(R.id.modelName))
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {
                            Log.d(LOG_TAG, "Selection in model spinner with pos = " + pos);
                            CustomAdapter adapter = (CustomAdapter) parent.getAdapter();
                            if (adapter != null) {
                                adapter.setSelected();
                                int selectedKey = adapter.getKey(pos);
                                (new StoredKey(getString(R.string.INVERTER_MODEL))).set(selectedKey);
                            }
                        }
                    });
        updateModelSpinnerList(modelKey, seriesKey);
    }

    private void updateBrandSpinnerList(int selectedBrandKey){
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

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
                new CustomAdapter(this, android.R.layout.simple_spinner_item, brandName, brandKey);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);
        if(selectedBrandKey > 0)
        for(int i=0; i<nOfBrands;i++){
            if(brandKey[i] == selectedBrandKey) {
                brandSpinner.setSelection(i);
                brandAdapter.setSelected();
            }
        } else brandAdapter.setUndefined();
        brandSpinner.invalidate();
        db.close();
    }

    private void updateSeriesSpinnerList(int selectedSeriesKey, int brandKey){
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        String[] seriesName = null;
        int[] seriesKey = null;
        int nOfSeries = 0;

        if(brandKey > 0) {
            Cursor cursor = db.query(getString(R.string.SERIES_TABLE_NAME),
                    null,
                    getString(R.string.BRAND_TABLE_NAME) + " == " + brandKey,
                    null, null, null, null);
            nOfSeries = cursor.getCount();
            if(nOfSeries > 0){
                seriesName = new String[nOfSeries];
                seriesKey = new int[nOfSeries];
                if (cursor.moveToFirst()) {
                    int seriesNameIndex = cursor.getColumnIndex("name");
                    int seriesKeyIndex = cursor.getColumnIndex("key");
                    for(int i=0; i<nOfSeries; i++){
                        seriesName[i] = cursor.getString(seriesNameIndex);
                        seriesKey[i] = cursor.getInt(seriesKeyIndex);
                        cursor.moveToNext();
                    }
                } else Log.d(LOG_TAG, "0 rows");
                cursor.close();
            }
        };

        Spinner seriesSpinner = (Spinner) findViewById(R.id.seriesName);
        if(seriesName != null){
            CustomAdapter seriesAdapter =
                    new CustomAdapter(this, android.R.layout.simple_spinner_item, seriesName, seriesKey);
            seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            seriesSpinner.setAdapter(seriesAdapter);
            if(selectedSeriesKey > 0){
                for(int i=0; i<nOfSeries; i++){
                    if(seriesKey[i] == selectedSeriesKey){
                        seriesSpinner.setSelection(i);
                        seriesAdapter.setSelected();
                    }
                }
            } else seriesAdapter.setUndefined();
        } else seriesSpinner.setEnabled(false);
        seriesSpinner.invalidate();
        db.close();
    };

    private void updateModelSpinnerList(int selectedModelKey, int seriesKey){
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        String[] modelName = null;
        int[] modelKey = null;
        int nOfModels = 0;

        if(seriesKey > 0){
            Cursor modelCursor = db.query(getString(R.string.MODEL_TABLE_NAME),
                    null,
                    "series == " + seriesKey,
                    null, null, null, null);
            nOfModels = modelCursor.getCount();
            Log.d(LOG_TAG, "updateModelSpinnerList, nOfModels = " + nOfModels);
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
        if(modelName != null){
            CustomAdapter modelAdapter =
                    new CustomAdapter(this, android.R.layout.simple_spinner_item, modelName, modelKey);
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
        } else modelSpinner.setEnabled(false);
        modelSpinner.invalidate();
        db.close();
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
