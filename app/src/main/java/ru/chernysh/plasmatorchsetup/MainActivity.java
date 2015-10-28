package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    private TableWithSpinner model;
    private TableWithSpinner series;
    private TableWithSpinner brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");
        initSpinners();
        initThicknessEdit();
        updateTable();

    }

    private void initSpinners(){
        int model_selected;

        final String pref = getString(R.string.preference_);
        final String model_table_name = getString(R.string.model_table);
        final String series_table_name = getString(R.string.series_table);
        final String brand_table_name = getString(R.string.brand_table);

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

    private void updateTable() {
        TableLayout table = (TableLayout)findViewById(R.id.plasmaSettingTable);
        prepareTable(table);
        fillTableContent(table);
        table.invalidate();
    }

    private void prepareTable(TableLayout table) {
        Log.d(LOG_TAG, "prepare table");
        // clear table
        table.removeAllViews();
        // create header row
        TableRow headerRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        headerRow.setLayoutParams(lp);

        // fill table header
        TextView processHeader = new TextView(this);
        processHeader.setText(getString(R.string.process_header));
        headerRow.addView(processHeader);

        TextView currentHeader = new TextView(this);
        currentHeader.setText(getString(R.string.current_header));
        headerRow.addView(currentHeader);

        TextView purposeHeader = new TextView(this);
        purposeHeader.setText(getString(R.string.purpose_header));
        headerRow.addView(purposeHeader);

        TextView arcVoltageHeader = new TextView(this);
        arcVoltageHeader.setText(getString(R.string.arc_voltage_header));
        headerRow.addView(arcVoltageHeader);

        TextView arcHeightHeader = new TextView(this);
        arcHeightHeader.setText(getString(R.string.arc_height_header));
        headerRow.addView(arcHeightHeader);

        TextView pierceHeightHeader = new TextView(this);
        pierceHeightHeader.setText(getString(R.string.pierce_height_header));
        headerRow.addView(pierceHeightHeader);

        TextView pierceTimeHeader = new TextView(this);
        pierceTimeHeader.setText(getString(R.string.pierce_time_header));
        headerRow.addView(pierceTimeHeader);

        TextView kerfOffsetHeader = new TextView(this);
        kerfOffsetHeader.setText(getString(R.string.kerf_offset_header));
        headerRow.addView(kerfOffsetHeader);

        // add row to layout
        headerRow.setBackgroundColor(Color.parseColor("#FFFFFFDD"));
        table.addView(headerRow);
    }

    private void fillTableContent(TableLayout table) {

        int currentInverter = 0;
        if(model != null) currentInverter = model.getSelected();
        if(currentInverter == 0) return;

        int[] processKey = getProcessList(currentInverter);
        if(processKey != null){
            String[] processName = getProcessNames(processKey);
            int N = processKey.length;
            for(int i=0; i<N; i++)
                fillTableForProcess(table, currentInverter, processKey[i], processName[i]);
        }

        int nOfRow = 30;
        for( int i=1; i<nOfRow; i++){
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            int nOfColumns = 8;
            for( int j = 0; j< nOfColumns; j++){
                TextView tv = new TextView(this);
                tv.setText(Integer.toString(i*1000+j*10));
                row.addView(tv);
            }
            if((i%2)>0) row.setBackgroundColor(Color.parseColor("#FFFFFFAA"));
            else row.setBackgroundColor(Color.parseColor("#FFFFFFDD"));
            table.addView(row,i);
        }
    }

    private int[] getProcessList(int inverter) {

        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
        String tableName = getString(R.string.process_usage_table);
        String filter = TableWithSpinner.getFilterEqualTo(R.string.model_table,inverter);
        String process_key = getString(R.string.process_table);
        String[] columnList = {process_key};
        Cursor cursor = db.query(true, tableName, columnList, filter, null, null, null, process_key, null, null);
        int nOfRows = cursor.getCount();
        int[] result = null;
        if(nOfRows > 0){
            result = new int[nOfRows];
            int processKeyIndex = cursor.getColumnIndex(process_key);
            if (cursor.moveToFirst()) {
                for(int i=0; i<nOfRows; i++){
                    result[i] = cursor.getInt(processKeyIndex);
                    Log.d(LOG_TAG, "process key[" + i + "]= " + result[i]);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    private String[] getProcessNames(int[] processKey) {
        String[] result = null;
        if(processKey == null) return null;
        int nOfRows = processKey.length;
        if(nOfRows > 0){
            SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();
            String tableName = getString(R.string.process_table);
            Cursor cursor = db.query(tableName, null, null, null, null, null, null);
            String nameHeader = TableWithSpinner.getNameColumnHeader(cursor);
            int processNameIndex = cursor.getColumnIndex(nameHeader);
            result = new String[nOfRows];
            for(int i=0; i<nOfRows; i++){
                String filter = TableWithSpinner.getFilterEqualTo(R.string.key_field,processKey[i]);
                cursor = db.query(tableName, null, filter, null, null, null, null);
                if (cursor.moveToFirst()) result[i] = cursor.getString(processNameIndex);
                else result[i] = null;
                Log.d(LOG_TAG, "process name[" + i + "]= " + result[i]);
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    private void fillTableForProcess(TableLayout table, int modelKey, int processKey, String processName) {
        if(modelKey > 0){
            String modelFilter = TableWithSpinner.getFilterEqualTo(R.string.model_table,modelKey);
        }
    }

}
