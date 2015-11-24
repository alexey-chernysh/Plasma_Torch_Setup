package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = MainActivity.class.getName()+": ";

    private boolean powerSupplyFragExpanded = false;
    private boolean metalFragExpanded = false;

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

        cuttingChartTable = new CuttingChartTable(this);
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
