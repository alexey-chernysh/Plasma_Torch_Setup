/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PowerSupplyNameFragment extends Fragment {

    public PowerSupplyNameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_power_supply_name, container, false);
        initTexts(v);
        return v;
    }

    private void initTexts(View v) {

        final String pref = getString(R.string.preference_);

        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        final String brandTableName = getString(R.string.brand_table);
        int brandSelected = (new StoredKey(pref + brandTableName)).get();
        if(brandSelected == 0) brandSelected = 1;
        String brandName = DataBaseHelper.getNameByKey(db, brandTableName, brandSelected);

        final String seriesTableName = getString(R.string.series_table);
        int seriesSelected = (new StoredKey(pref + seriesTableName)).get();
        if(seriesSelected == 0) seriesSelected = 1;
        String seriesName = DataBaseHelper.getNameByKey(db, seriesTableName, seriesSelected);

        final String modelTableName = getString(R.string.model_table);
        int modelSelected = (new StoredKey(pref + modelTableName)).get();
        if(modelSelected == 0) modelSelected = 1;
        String modelName = DataBaseHelper.getNameByKey(db, modelTableName, modelSelected);

        TextView modelView = (TextView)v.findViewById(R.id.PSUNameText);
        modelView.setText(brandName + " " + seriesName + " " + modelName);

        db.close();

    }

}
