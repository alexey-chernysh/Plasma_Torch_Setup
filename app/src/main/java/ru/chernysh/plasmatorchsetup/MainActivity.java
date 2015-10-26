package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");
        initSpinners();
        initThicknessEdit();
        initTable();

    }

    private void initSpinners(){
        int model_selected;
        TableWithSpinner model;
        TableWithSpinner series;
        TableWithSpinner brand;

        final String model_table_name = getString(R.string.model_table);
        final String series_table_name = getString(R.string.series_table);
        final String brand_table_name = getString(R.string.brand_table);
        final String pref = getString(R.string.preference_);

        Log.d(LOG_TAG, "initSpinners!!!");

        model_selected = (new StoredKey(pref + model_table_name)).get();
        model = new TableWithSpinner(this.findViewById(android.R.id.content),
                model_table_name,
                R.id.modelName);

        series = new TableWithSpinner(this.findViewById(android.R.id.content),
                series_table_name,
                R.id.seriesName);
        model.setUpperLevelSpinner(series);
        series.setLowerLevelSpinner(model);

        brand = new TableWithSpinner(this.findViewById(android.R.id.content),
                brand_table_name,
                R.id.brandName);
        series.setUpperLevelSpinner(brand);
        brand.setLowerLevelSpinner(series);

        model.setSelected(model_selected);
        int series_selected = model.getFilterKey();
        series.setSelected(series_selected);
        int brand_selected = series.getFilterKey();

        brand.setSelected(brand_selected);
        series.setSelected(series_selected);
        model.setSelected(model_selected);
    }

    private void initThicknessEdit(){
        final String material_table_name = getString(R.string.material_table);
        final int materialSelected = (new StoredKey(getString(R.string.preference_)+material_table_name)).get();

        TableWithSpinner material = new TableWithSpinner(this.findViewById(android.R.id.content),
                material_table_name,
                R.id.materialName);
        material.setSelected(materialSelected);

        final EditText materialThicknessEdit = (EditText)findViewById(R.id.materialThickness);
        int thickness_х_100 = (new StoredKey(App.getResourceString(R.string.preference_)
                                                    +App.getResourceString(R.string.material_thickness) )).get();
        Double thickness = ((double)thickness_х_100)/100.0;
        String thicknessString = thickness.toString();
        materialThicknessEdit.setText(thicknessString);
        materialThicknessEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // сохраняем текст, введенный до нажатия Enter в переменную
                    String str = materialThicknessEdit.getText().toString();
                    try {
                        double thickness = Double.parseDouble(str);
                        int thickness_x_100 = (int) (thickness * 100.0);
                        (new StoredKey(App.getResourceString(R.string.preference_)
                                + App.getResourceString(R.string.material_thickness))).set(thickness_x_100);
                    } catch (NumberFormatException nfe) {
                        // nothing to do
                        // waiting for suitable number
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initTable() {
        TableLayout table = (TableLayout)findViewById(R.id.plasmaSettingTable);
        int nOfRow = 10;
        for( int i=1; i<nOfRow; i++){
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            int nOfColumns = 10;
            for( int j = 0; j< nOfColumns; j++){
                TextView tv = new TextView(this);
                if((j%2)==0)
                    tv.setText(Integer.toString(i*1000+j*10));
                row.addView(tv);
            }
            if((i%2)>0) row.setBackgroundColor(Color.parseColor("#FFFFFFAA"));
            else row.setBackgroundColor(Color.parseColor("#FFFFFFDD"));
            table.addView(row,i);
        }
    }

}
