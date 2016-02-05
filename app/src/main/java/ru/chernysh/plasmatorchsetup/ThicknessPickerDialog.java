/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class ThicknessPickerDialog extends Activity {

    private static final String LOG_TAG = "Thickness Picker: ";

    private final String pref = App.getResourceString(R.string.preference_);
    private final String materialThickness = App.getResourceString(R.string.material_thickness);

    private int value;
    private static String thicknessName[]  = null;
    private static double thicknessValue[] = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thickness_picker);

        value = (new StoredKey(pref + materialThickness)).get()/100;

        NumberPicker np = (NumberPicker)findViewById(R.id.thickness_picker);
        np.setMinValue(0);
        np.setMaxValue(thicknessName.length - 1);
        np.setDisplayedValues(thicknessName);
        np.setValue(value);
        np.setWrapSelectorWheel(true);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                valueChanged(newVal);
            }
        });

        Button button = (Button)findViewById(R.id.apply_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void valueChanged(double newValue){
        int thickness_х_100 = (int)(100.0*newValue);
        (new StoredKey(pref + materialThickness)).set(thickness_х_100);
    }

}

