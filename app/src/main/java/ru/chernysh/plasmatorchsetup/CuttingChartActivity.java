package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CuttingChartActivity extends Activity implements View.OnClickListener, ChartFragmentCommunicator {

    private static final String LOG_TAG = CuttingChartActivity.class.getName()+": ";

    private boolean powerSupplyFragExpanded = false;
    private boolean metalFragExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "OnCreate");

        findViewById(R.id.power_supply_placeholder).setOnClickListener(this);
        findViewById(R.id.metal_placeholder).setOnClickListener(this);
        findViewById(R.id.cutting_chart_placeholder).setOnClickListener(this);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.power_supply_placeholder, new PowerSupplyNameFragment());
        ft.replace(R.id.metal_placeholder, new MetalTypeFragment());
        ft.commit();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
            case R.id.cutting_chart_placeholder:
                if(powerSupplyFragExpanded) squeezePowerSupplyFrag();
                if(metalFragExpanded) squeezeMetalFrag();
                break;
            default:
        }
    }

    private void expandPowerSupplyFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.power_supply_placeholder, new PowerSupplySelectFragment());
        ft.commit();
        powerSupplyFragExpanded = true;
    }

    private void squeezePowerSupplyFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.power_supply_placeholder, new PowerSupplyNameFragment());
        ft.commit();
        powerSupplyFragExpanded = false;
    }

    private void expandMetalFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.metal_placeholder, new MetalSelectFragment());
        ft.commit();
        metalFragExpanded = true;
    }

    private void squeezeMetalFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.metal_placeholder, new MetalTypeFragment());
        ft.commit();
        metalFragExpanded = false;
    }

    @Override
    public void updateChart(){
        CuttingChartFragment cuttingChartFragment = (CuttingChartFragment)getFragmentManager().findFragmentById(R.id.cutting_chart_placeholder);
        if(cuttingChartFragment != null) {
            cuttingChartFragment.update();
            Toast.makeText(this, "Cutting chart updated!", Toast.LENGTH_SHORT).show();
        }
    }

}