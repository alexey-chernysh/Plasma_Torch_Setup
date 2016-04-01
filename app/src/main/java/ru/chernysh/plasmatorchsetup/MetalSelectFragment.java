/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MetalSelectFragment extends Fragment {

    final String LOG_TAG = "Metal Select: ";

    private View fragmentView;

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

        return fragmentView;
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
        String thicknessString = MaterialThickness.getInstance().getCurrentThicknessName();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        initSpinners(fragmentView);
        fragmentView.invalidate();
        CuttingChartActivity activity = (CuttingChartActivity)getActivity();
        activity.startMetalTimer();
    }
}
