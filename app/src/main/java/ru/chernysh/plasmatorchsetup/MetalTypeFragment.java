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

        initTexts(v);

        return v;
    }

    private void initTexts(View parentView){
        final String pref = getString(R.string.preference_);
        final String materialTableName = getString(R.string.material_table);
        final String materialThickness = getString(R.string.material_thickness);


        SQLiteDatabase db = (new DataBaseHelper()).getWritableDatabase();

        int materialSelected = (new StoredKey(pref + materialTableName)).get();
        if(materialSelected == 0) materialSelected = 1;
        String metalName = DataBaseHelper.getNameByKey(db, materialTableName, materialSelected);
        ((TextView)parentView.findViewById(R.id.metalNameText)).setText(metalName);

        db.close();

        int thickness_х_100 = (new StoredKey(pref + materialThickness)).get();
        if(thickness_х_100 <= 0){
            thickness_х_100 = 100;
            (new StoredKey(pref + materialThickness)).set(thickness_х_100);
        }
        Double thickness = ((double)thickness_х_100)/100.0;
        String thicknessString = thickness.toString();
        ((TextView)parentView.findViewById(R.id.metalThicknessText)).setText(thicknessString);

    }

}
