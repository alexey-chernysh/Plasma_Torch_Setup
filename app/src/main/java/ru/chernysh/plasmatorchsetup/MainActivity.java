package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    private static final String[] BRANDS = new String[] {
            "Hypertherm", "Thermodyne", "Lincoln Electric", "Trafimet", "ESAB"
    };


    private static final String[] SERIES = new String[] {
            "Powermax", "Autocut", "HPR"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
