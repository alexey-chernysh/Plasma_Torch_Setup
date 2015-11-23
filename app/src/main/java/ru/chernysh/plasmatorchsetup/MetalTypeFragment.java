/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MetalTypeFragment extends Fragment {

    final String LOG_TAG = "Metal Type: ";

    public MetalTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(LOG_TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_metal_type, container, false);

        initMetalName(v);
        initMetalThickness(v);

        return v;
    }

    private void initMetalName(View parentView){
        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        final String pref = getString(R.string.preference_);
        final String material_table_name = getString(R.string.material_table);
        final int materialSelected = (new StoredKey(pref + material_table_name)).get();
        String metalName = DataBaseHelper.getNameByKey(db, material_table_name, materialSelected);
        ((TextView)parentView.findViewById(R.id.metal_name_text)).setText(metalName);

        db.close();
    }

    private void initMetalThickness(View parentView) {
        int thickness_х_100 = (new StoredKey(App.getResourceString(R.string.preference_)
                +App.getResourceString(R.string.material_thickness) )).get();
        Double thickness = ((double)thickness_х_100)/100.0;
        String thicknessString = thickness.toString();
        ((TextView)parentView.findViewById(R.id.metal_thickness_text)).setText(thicknessString);
    }

}
