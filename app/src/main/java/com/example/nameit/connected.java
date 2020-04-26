package com.example.nameit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class connected extends Fragment {

    public connected() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_connected, container, false);
        ListView connectedList =  view.findViewById(R.id.connectedList);
        Button begin = view.findViewById(R.id.Begin);
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.MyFragment = new play();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,MainActivity.MyFragment).commit();
            }
        });
        return view;
    }
}
