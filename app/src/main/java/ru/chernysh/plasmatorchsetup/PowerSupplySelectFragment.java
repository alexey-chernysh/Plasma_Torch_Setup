/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PowerSupplySelectFragment extends Fragment {

    final String LOG_TAG = "Metal Select: ";

    private TableWithSpinner model;
    private TableWithSpinner series;
    private TableWithSpinner brand;

    public PowerSupplySelectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_power_supply_select, container, false);
        initSpinners(v);
        return v;
    }

    private void initSpinners(View fragmentView){
        int model_selected;

        final String pref = getString(R.string.preference_);
        final String model_table_name = getString(R.string.model_table);
        final String series_table_name = getString(R.string.series_table);
        final String brand_table_name = getString(R.string.brand_table);

        Log.d(LOG_TAG, "initSpinners!!!");

        model_selected = (new StoredKey(pref + model_table_name)).get();
        model = new TableWithSpinner(fragmentView.findViewById(android.R.id.content),
                model_table_name,
                R.id.modelNameSpinner);

        series = new TableWithSpinner(fragmentView.findViewById(android.R.id.content),
                series_table_name,
                R.id.seriesNameSpinner);
        model.setUpperLevelSpinner(series);
        series.setLowerLevelSpinner(model);

        brand = new TableWithSpinner(fragmentView.findViewById(android.R.id.content),
                brand_table_name,
                R.id.brandNameSpinner);
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

}
