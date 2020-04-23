package com.example.nouned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.StringCharacterIterator;
import java.util.ArrayList;

public class Highscores extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View inf =  inflater.inflate(R.layout.activity_highscores,container,false);
        NDbhelper databasehelper = new NDbhelper(getActivity());
        SQLiteDatabase database = databasehelper.getReadableDatabase();
        ListView listview = inf.findViewById(R.id.highscore);
        ArrayList<Highscores_item> list = new ArrayList<Highscores_item>();
        TextView Highscores = inf.findViewById(R.id.Sorry);


        Highscores.setText("HIGH SCORES");
        SQLiteDatabase database1 = databasehelper.getWritableDatabase();

        String count = "SELECT count(*) FROM ";
        Cursor mcursor = database1.rawQuery(count+NDbhelper.HIGH_SCORE, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount==0) {
//leave
            inf = inflater.inflate(R.layout.no_highscores, container, false);
            mcursor.close();



        }else{
            Cursor cursor = database.query(NDbhelper.HIGH_SCORE, null, null, null, null, null, NDbhelper.SCORES + " DESC");


            int Number = 1;

            while (cursor.moveToNext()) {



                // get data from database and populate the listview in the highscores fragment
                    int score = cursor.getInt(cursor.getColumnIndex(NDbhelper.SCORES));
                    String date = cursor.getString(cursor.getColumnIndex(NDbhelper.DATE));
                    list.add(new Highscores_item(Number, score, date));


                    Number++;

                }

           HighScores_Adapter adapter = new HighScores_Adapter(getContext(),R.layout.highscores_list_item_view,list);
           listview.setAdapter(adapter);
                cursor.close();
            }








        return inf;
    }
}
