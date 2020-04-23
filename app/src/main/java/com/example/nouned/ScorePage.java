package com.example.nouned;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScorePage extends Fragment {


    public ScorePage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View inf = inflater.inflate(R.layout.fragment_score_page,container,false);
        ListView naration = inf.findViewById(R.id.naration);
        TextView ScoreCount = inf.findViewById(R.id.scoreCount);


        //Retrives data from play fragment

        String Text1 = getArguments().getString("Country");
        String Text2 = getArguments().getString("City");
        String Text3 = getArguments().getString("Name");
        String Text4 = getArguments().getString("Color");
        String Text5 = getArguments().getString("Food");
        String Text6 = getArguments().getString("Animal");
        String Text7 = getArguments().getString("Car");
        String Score = getArguments().getString("Score");



        ScoreCount.setText(Score);






        //Data set for out ListView and Adapter
        ArrayList<String> input = new ArrayList<>();

        input.add(Text1);
        input.add(Text2);
        input.add(Text3);
        input.add(Text4);
        input.add(Text5);
        input.add(Text6);
        input.add(Text7);

        ArrayAdapter inputAdapter = new ArrayAdapter(getContext(),R.layout.listview,input);


        naration.setAdapter(inputAdapter);

        return inf;


    }




}

