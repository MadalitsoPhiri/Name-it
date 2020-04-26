package com.example.nameit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class play extends Fragment {
    private String Current_Score;
    private boolean timer_Is_Running = MainActivity.getTimerIsRunning();
    private boolean timer2_Is_Running = MainActivity.getTimer2IsRunning();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inf = inflater.inflate(R.layout.play_fragment, container, false);
        NDbhelper databasehelper = new NDbhelper(getActivity());

        MainActivity.Timer1.start();


// check for alphabet letter
        if (MainActivity.Alphabet == 'A') {
            Current_Score = "Score 0";
            TextView CurrentScore = inf.findViewById(R.id.score);
            CurrentScore.setText(Current_Score);
            SQLiteDatabase score2 = databasehelper.getWritableDatabase();
// insert initial score values
            ContentValues values = new ContentValues();
            values.put(NDbhelper.COLUMN_ID, 1);
            values.put(NDbhelper.SCORE, 0);
            score2.insert(NDbhelper.TABLE_NAME, null, values);


        } else {

            //check if current letter is x and block the Country Edittext field if it is x!

            if(MainActivity.Alphabet == 'X'){
             EditText Country = inf.findViewById(R.id.country);




                Country.setInputType(InputType.TYPE_NULL);
                Country.setCursorVisible(false);
                Country.setBackgroundColor(getResources().getColor(R.color.colorGray));
                Country.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"There is no country that starts with the Letter X",Toast.LENGTH_LONG).show();
                    }
                });


            }
          //Read from database what the current score is and display it on the play fragment

            SQLiteDatabase database = databasehelper.getReadableDatabase();
            String[] projection = {NDbhelper.SCORE};
            String selection = NDbhelper.COLUMN_ID + "=?";
            String[] args = {"1"};

            Cursor cursor = database.query(NDbhelper.TABLE_NAME, projection, selection, args, null, null, null);
            while (cursor.moveToNext()) {
                Current_Score = cursor.getString(cursor.getColumnIndex(NDbhelper.SCORE));
                TextView score = inf.findViewById(R.id.score);
                score.setText("Score " + Current_Score);
            }

            cursor.close();

        }


        TextView CurrentLetter = inf.findViewById(R.id.Letter);
        CurrentLetter.setText("The Current Letter is " + MainActivity.getLetter());


        return inf;


    }

    @Override
    public void onDestroyView() {


            super.onDestroyView();



    }

}


