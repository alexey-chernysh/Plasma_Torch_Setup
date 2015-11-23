/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.chernysh.plasmatorchsetup;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MetalPlaceholderFragment extends Fragment {

    public MetalPlaceholderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metal_placeholder, container, false);
    }

}
