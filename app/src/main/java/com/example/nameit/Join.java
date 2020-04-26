package com.example.nameit;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Join extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join, container, false);
       // ListView List= view.findViewById(R.id.wifip2pDeviceListView2);
        //List.setVisibility(View.INVISIBLE);
        return view;
    }

}
