/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MetalSelectFragment extends Fragment {

    final String LOG_TAG = "Metal Select: ";

    private View fragmentView;
    private Button buttonMetric;
    private Button buttonImperial;

    public MetalSelectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_metal_select, container, false);

        initSpinners(fragmentView);
        buttonMetric = (Button)fragmentView.findViewById(R.id.buttonMetric);
        buttonMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMetric();
            }
        });
        buttonImperial = (Button)fragmentView.findViewById(R.id.buttonImperial);
        buttonImperial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImperial();
            }
        });
        return fragmentView;
    }

    private void setMetric() {
        MeasurementUnits.setMetricSystem();
        fragmentView.invalidate();
    }

    private void setImperial() {
        MeasurementUnits.setImperialSystem();
        fragmentView.invalidate();
    }

    private void initSpinners(View parentView) {
        final String pref = getString(R.string.preference_);
        final String materialTableName = getString(R.string.material_table);

        int materialSelected = (new StoredKey(pref + materialTableName)).get();
        if(materialSelected == 0) materialSelected = 1;

        TableWithSpinner material = new TableWithSpinner(parentView,
                                                         materialTableName,
                                                         R.id.material_name_spinner);
        material.setSelected(materialSelected);

        final TextView materialThicknessText = (TextView)parentView.findViewById(R.id.material_thickness);
        MaterialThickness materialThickness = MaterialThickness.getInstance();
        materialThickness.loadStringTables();
        String thicknessString = materialThickness.getCurrentThicknessName();
        materialThicknessText.setText(thicknessString);
        materialThicknessText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuttingChartActivity activity = (CuttingChartActivity)getActivity();
                activity.cancelMetalTimer();
                Intent intent = new Intent(activity, ThicknessPickerDialog.class);
                startActivityForResult(intent,1);
            }
        });

        TableWithSpinner units = new TableWithSpinner(parentView,
                                                      getString(R.string.metric_system_table),
                                                      R.id.units_name_spinner);
        units.setSelected(MeasurementUnits.getUnitsKey());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        MaterialThickness materialThickness = MaterialThickness.getInstance();
//        materialThickness.loadStringTables();
        initSpinners(fragmentView);
        fragmentView.invalidate();
        CuttingChartActivity activity = (CuttingChartActivity)getActivity();
        activity.startMetalTimer();
    }

}
