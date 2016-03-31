/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class ThicknessPickerDialog extends Activity {

    private final static String LOG_TAG = "Thickness picker:";

    private MaterialThickness materialThickness;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thickness_picker);

        NumberPicker np = (NumberPicker)findViewById(R.id.thickness_picker);
        materialThickness = MaterialThickness.getInstance();

        np.setMinValue(0);
        np.setMaxValue(materialThickness.thicknessName.length - 1);
        np.setDisplayedValues(materialThickness.thicknessName);
        np.setWrapSelectorWheel(false);
        // set current selection according stored value key
        int num = 0;
        int currentKey = materialThickness.getCurrentThicknessKey();
        while(currentKey > materialThickness.thicknessKey[num])num++;
        np.setValue(num);
        np.setEnabled(true);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                materialThickness.setCurrentThickness(newVal);
            }
        });

        Button hiddenButton = (Button)findViewById(R.id.hidden_button);
        hiddenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}

