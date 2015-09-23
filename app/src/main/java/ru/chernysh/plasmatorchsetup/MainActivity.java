package ru.chernysh.plasmatorchsetup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

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

        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.BrandName);
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, BRANDS);
        textView.setAdapter(brandAdapter);

        Spinner spinner = (Spinner) findViewById(R.id.SeriesName);
        ArrayAdapter<String> seriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SERIES);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(seriesAdapter);    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
