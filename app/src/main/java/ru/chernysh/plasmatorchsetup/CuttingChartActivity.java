package ru.chernysh.plasmatorchsetup;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

public class CuttingChartActivity extends Activity implements View.OnClickListener, ChartFragmentCommunicator, MetalFragmentCommunicator {

    private static final String LOG_TAG = CuttingChartActivity.class.getName()+": ";

    private boolean powerSupplyFragExpanded = false;
    private boolean metalFragExpanded = false;
    private CountDownTimer PSUTimer;
    private CountDownTimer MetalTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cutting_chart_activity);

        Log.d(LOG_TAG, "OnCreate");

        findViewById(R.id.power_supply_placeholder).setOnClickListener(this);
        findViewById(R.id.metal_placeholder).setOnClickListener(this);
        findViewById(R.id.cutting_chart_placeholder).setOnClickListener(this);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.power_supply_placeholder, new PowerSupplyNameFragment());
        ft.replace(R.id.metal_placeholder, new MetalNameFragment());
        ft.commit();

        PSUTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                squeezePowerSupplyFrag();
            }
        };
        MetalTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                squeezeMetalFrag();
            }
        };
    }

   @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.power_supply_placeholder:
                if(powerSupplyFragExpanded) squeezePowerSupplyFrag();
                else {
                    squeezeMetalFrag();
                    expandPowerSupplyFrag();
                }
                break;
            case R.id.metal_placeholder:
                if(metalFragExpanded) squeezeMetalFrag();
                else {
                    squeezePowerSupplyFrag();
                    expandMetalFrag();
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
        if(ft != null){
            ft.replace(R.id.power_supply_placeholder, new PowerSupplySelectFragment());
            ft.commit();
            powerSupplyFragExpanded = true;
            if(PSUTimer != null) PSUTimer.start();
        }
    }

    private void squeezePowerSupplyFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(ft != null){
            ft.replace(R.id.power_supply_placeholder, new PowerSupplyNameFragment());
            ft.commit();
            powerSupplyFragExpanded = false;
            if(PSUTimer != null) PSUTimer.cancel();
        }
    }

    private void expandMetalFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(ft != null){
            ft.replace(R.id.metal_placeholder, new MetalSelectFragment());
            ft.commit();
            metalFragExpanded = true;
            startMetalTimer();
        }
    }

    private void squeezeMetalFrag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(ft != null){
            ft.replace(R.id.metal_placeholder, new MetalNameFragment());
            ft.commit();
            metalFragExpanded = false;
            cancelMetalTimer();
        }
    }

    public void startMetalTimer(){
        if(MetalTimer != null) MetalTimer.start();
    }

    public void cancelMetalTimer(){
        if(MetalTimer != null) MetalTimer.cancel();
    }

    @Override
    public void updateChart(){
        CuttingChartFragment cuttingChartFragment = (CuttingChartFragment)getFragmentManager().findFragmentById(R.id.cutting_chart_placeholder);
        if(cuttingChartFragment != null) {
            cuttingChartFragment.update();
        }
    }

    @Override
    public void updateThickness() {
        updateChart();
    }
}
