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

    private TableWithSpinner material;
    private TableWithSpinner model;
    private TableWithSpinner series;
    private TableWithSpinner brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");
        initSpinners();

    }

    private void initSpinners(){
        final String model_table_name = getString(R.string.model_table);
        final String series_table_name = getString(R.string.series_table);
        final String brand_table_name = getString(R.string.brand_table);

        int model_selected = (new StoredKey(getString(R.string.preference_)+model_table_name)).get();
        if(model_selected == 0) model_selected = 1; // for first run
        model = new TableWithSpinner(this.findViewById(android.R.id.content),
                model_table_name,
                R.id.modelName,
                model_selected,
                series_table_name,
                0,
                null);

        int series_selected = model.getFilterKey(model_selected);
        series = new TableWithSpinner(this.findViewById(android.R.id.content),
                series_table_name,
                R.id.seriesName,
                series_selected,
                brand_table_name,
                0,
                model);

        int brand_selected = series.getFilterKey(series_selected);
        brand = new TableWithSpinner(this.findViewById(android.R.id.content),
                brand_table_name,
                R.id.brandName,
                brand_selected,
                null,
                0,
                series);
        series.updateList(series_selected,brand_selected);
        model.updateList(model_selected,series_selected);

        final String material_table_name = getString(R.string.material_table);
        material = new TableWithSpinner(this.findViewById(android.R.id.content),
                material_table_name,
                R.id.materialName,
                (new StoredKey(getString(R.string.preference_)+material_table_name)).get(),
                null,
                0,
                null);
    }

}
