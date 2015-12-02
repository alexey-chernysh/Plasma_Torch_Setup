/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

public class CuttingChartFragment extends Fragment {

    private View view;

    public CuttingChartFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cutting_chart, container, false);
        update();
        return view;
    }

    private TableUpdateTask updateTask;

    public void update(){
        if(updateTask != null) updateTask.cancel(true);
        updateTask = new TableUpdateTask();
        updateTask.execute();
    }

    class TableUpdateTask extends AsyncTask<Void, Void, Void> {

        TableLayout table;
        Context context;
        CuttingChart chart;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //retrieve references to table
            table = (TableLayout)view.findViewById(R.id.plasma_setting_table);
            context = view.getContext();
            chart = new CuttingChart(table, context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            chart.fillData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //fill new table
            chart.fillHeaderView();
            chart.fillContentView();
        }


    }

}
