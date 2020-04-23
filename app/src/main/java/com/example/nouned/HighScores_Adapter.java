package com.example.nouned;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class HighScores_Adapter extends ArrayAdapter<Highscores_item> {


    public HighScores_Adapter(@NonNull Context context, int resource, @NonNull List<Highscores_item> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.highscores_list_item_view,parent,false);

        }

        Highscores_item current_item = getItem(position);

        TextView Number = listItemView.findViewById(R.id.number);
        Number.setText(String.valueOf(current_item.getNumber()));

        TextView Score = listItemView.findViewById(R.id.Score);
        Score.setText(String.valueOf(current_item.getScore()));

        TextView Date = listItemView.findViewById(R.id.date);
        Date.setText(current_item.getDate());

        return listItemView;
    }
}
