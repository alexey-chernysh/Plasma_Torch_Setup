package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    private static final String[] SERIES = new String[] {
            "Powermax", "Autocut", "HPR"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataBaseHelper dbHelper = new DataBaseHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("manufacturers", null, null, null, null, null, null);
        int nOfBrands = cursor.getCount();
        String[] BRANDS = new String[nOfBrands];
        if (cursor.moveToFirst()) {
            int brandIndex = cursor.getColumnIndex("brand");
            for(int i=0; i<nOfBrands;i++){
                BRANDS[i] = cursor.getString(brandIndex);
                cursor.moveToNext();
            }
        } else Log.d(LOG_TAG, "0 rows");
        cursor.close();

        Spinner brandSpinner = (Spinner)findViewById(R.id.BrandName);
        ArrayAdapter<String> brandAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, BRANDS);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        Spinner seriesSpinner = (Spinner) findViewById(R.id.SeriesName);
        ArrayAdapter<String> seriesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SERIES);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesSpinner.setAdapter(seriesAdapter);    }

}
