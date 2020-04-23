package com.example.nouned;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static com.example.nouned.MainActivity.MyFragment;


public class Connect extends Fragment {

    public Button host;
    public Button join;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        join = view.findViewById(R.id.join_Button);
        host = view.findViewById(R.id.host_Button);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check to see if wifi is enabled

                //Wait for other players to join
                ((MainActivity) getActivity()).startHost();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //search for other players and connect to them
                ((MainActivity) getActivity()).startJoin();

            }
        });
        return view;
    }

}
