package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    private boolean powerSupplyFragExpanded = false;
    private boolean metalFragExpanded = false;

    private TableWithSpinner material;

    private CuttingChartTable cuttingChartTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");

        findViewById(R.id.power_supply_placeholder).setOnClickListener(this);
        findViewById(R.id.metal_placeholder).setOnClickListener(this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.power_supply_placeholder, new PowerSupplyNameFragment());
        ft.replace(R.id.metal_placeholder, new MetalTypeFragment());
        ft.addToBackStack(null);
        ft.commit();

        initThicknessEdit();

        cuttingChartTable = new CuttingChartTable(this);
    }

    private void initThicknessEdit(){
        final String material_table_name = getString(R.string.material_table);
        final int materialSelected = (new StoredKey(getString(R.string.preference_)+material_table_name)).get();

        material = new TableWithSpinner(this.findViewById(android.R.id.content),
                material_table_name,
                R.id.materialName);
        material.setSelected(materialSelected);

        final EditText materialThicknessEdit = (EditText)findViewById(R.id.materialThickness);
        int thickness_х_100 = (new StoredKey(App.getResourceString(R.string.preference_)
                                                    +App.getResourceString(R.string.material_thickness) )).get();
        Double thickness = ((double)thickness_х_100)/100.0;
        String thicknessString = thickness.toString();
        materialThicknessEdit.setText(thicknessString);
        materialThicknessEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // сохраняем текст, введенный до нажатия Enter в переменную
                    String str = materialThicknessEdit.getText().toString();
                    try {
                        double thickness = Double.parseDouble(str);
                        int thickness_x_100 = (int) (thickness * 100.0);
                        (new StoredKey(App.getResourceString(R.string.preference_)
                                + App.getResourceString(R.string.material_thickness))).set(thickness_x_100);
                    } catch (NumberFormatException nfe) {
                        // nothing to do
                        // waiting for suitable number
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.power_supply_placeholder:
                if(powerSupplyFragExpanded) squeezePowerSupplyFrag();
                else {
                    expandPowerSupplyFrag();
                    squeezeMetalFrag();
                }
                break;
            case R.id.metal_placeholder:
                if(metalFragExpanded) squeezeMetalFrag();
                else {
                    expandMetalFrag();
                    squeezePowerSupplyFrag();
                }
                break;
            default:
        }
    }

    private void expandPowerSupplyFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.power_supply_placeholder, new PowerSupplySelectFragment());
        ft.addToBackStack(null);
        ft.commit();
        powerSupplyFragExpanded = true;
    }

    private void squeezePowerSupplyFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.power_supply_placeholder, new PowerSupplyNameFragment());
        ft.addToBackStack(null);
        ft.commit();
        powerSupplyFragExpanded = false;
    }

    private void expandMetalFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.metal_placeholder, new MetalSelectFragment());
        ft.addToBackStack(null);
        ft.commit();
        metalFragExpanded = true;
    }

    private void squeezeMetalFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.metal_placeholder, new MetalTypeFragment());
        ft.addToBackStack(null);
        ft.commit();
        metalFragExpanded = false;
    }

}
