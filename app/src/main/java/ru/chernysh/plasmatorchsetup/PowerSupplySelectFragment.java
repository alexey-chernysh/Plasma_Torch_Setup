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

    public PowerSupplySelectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_power_supply_select, container, false);
        initSpinners(view);
        return view;
    }

    private void initSpinners(View fragmentView){
        final String pref = getString(R.string.preference_);
        final String model_table_name = getString(R.string.model_table);
        final String series_table_name = getString(R.string.series_table);
        final String brand_table_name = getString(R.string.brand_table);

        Log.d(LOG_TAG, "initSpinners!!!");

        int modelSelected = (new StoredKey(pref + model_table_name)).get();
        if(modelSelected == 0) modelSelected = 1;
        TableWithSpinner model = new TableWithSpinner(fragmentView,
                                                        model_table_name,
                                                        R.id.model_name_spinner);

        TableWithSpinner series = new TableWithSpinner(fragmentView,
                                                        series_table_name,
                                                        R.id.series_name_spinner);
        model.setUpperLevelSpinner(series);
        series.setLowerLevelSpinner(model);

        TableWithSpinner brand = new TableWithSpinner(fragmentView,
                                                        brand_table_name,
                                                        R.id.brand_name_spinner);
        series.setUpperLevelSpinner(brand);
        brand.setLowerLevelSpinner(series);

        model.setSelected(modelSelected);
        int series_selected = model.getFilterKey();
        series.setSelected(series_selected);
        int brand_selected = series.getFilterKey();

        brand.setSelected(brand_selected);
        series.setSelected(series_selected);
        model.setSelected(modelSelected);
    }
}
