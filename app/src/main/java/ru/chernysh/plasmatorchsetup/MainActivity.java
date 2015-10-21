package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";
//    private static final String[] stringNullArray = {""};
//    private static final int[] intNullArray = {0};

    private TableWithSpinner material;
    private TableWithSpinner model;
    private TableWithSpinner series;
    private TableWithSpinner brand;

    final String model_table_name = getString(R.string.model_table);
    final String series_table_name = getString(R.string.series_table);
    final String brand_table_name = getString(R.string.brand_table);
    final String pref = getString(R.string.preference_);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");
        initSpinners();
        initThicknessEdit();
    }

    private void initSpinners(){

        final int model_selected = (new StoredKey(pref + model_table_name)).get();
        model = new TableWithSpinner(this.findViewById(android.R.id.content),
                model_table_name,
                R.id.modelName,
                model_selected,
                series_table_name,
                0,
                null);

        final int series_selected = model.getFilterKey(model_selected);
        series = new TableWithSpinner(this.findViewById(android.R.id.content),
                series_table_name,
                R.id.seriesName,
                series_selected,
                brand_table_name,
                0,
                model);

        final int brand_selected = series.getFilterKey(series_selected);
        brand = new TableWithSpinner(this.findViewById(android.R.id.content),
                brand_table_name,
                R.id.brandName,
                brand_selected,
                null,
                0,
                series);

        series.updateList(series_selected, brand_selected);
        model.updateList(model_selected, series_selected);
        (new StoredKey(pref + model_table_name)).set(model_selected);

    }

    private void initThicknessEdit(){
        final String material_table_name = getString(R.string.material_table);
        final int materialSelected = (new StoredKey(getString(R.string.preference_)+material_table_name)).get();
        material = new TableWithSpinner(this.findViewById(android.R.id.content),
                material_table_name,
                R.id.materialName,
                materialSelected,
                null,
                0,
                null);

        final EditText materialThicknessEdit = (EditText)findViewById(R.id.materialThickness);
        int thickness_х_100 = (new StoredKey(App.getResourceString(R.string.preference_)
                                                    +App.getResourceString(R.string.material_thickness) )).get();
        Double thickness = ((double)thickness_х_100)/100.0;
        materialThicknessEdit.setText(thickness.toString());
        materialThicknessEdit.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // сохраняем текст, введенный до нажатия Enter в переменную
                    String str = materialThicknessEdit.getText().toString();
                    try {
                        double thickness = Double.parseDouble(str);
                        int thickness_x_100 = (int)(thickness*100.0);
                        (new StoredKey(App.getResourceString(R.string.preference_)
                                     + App.getResourceString(R.string.material_thickness) )).set(thickness_x_100);
                    } catch (NumberFormatException nfe) {
                    };
                    return true;
                }
                return false;
            }
        });
    }

}
