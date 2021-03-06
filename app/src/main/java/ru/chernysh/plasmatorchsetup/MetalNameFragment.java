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
import android.widget.TextView;

public class MetalNameFragment extends Fragment {

    final String LOG_TAG = "Metal Type: ";

    public MetalNameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(LOG_TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_metal_name, container, false);

        initTexts(v);

        return v;
    }

    private void initTexts(View parentView){
        final String pref = getString(R.string.preference_);
        final String materialTableName = getString(R.string.material_table);

        int materialSelected = (new StoredKey(pref + materialTableName)).get();
        if(materialSelected == 0) materialSelected = 1;
        String metalName = MainDB.getNameByKey(materialTableName, materialSelected);

        String thicknessString = MaterialThickness.getInstance().getCurrentThicknessName();

        ((TextView)parentView.findViewById(R.id.metalTypeText)).setText(metalName + " " + thicknessString);

    }

}
