package com.example.nameit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class NDbhelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_NAME = "nameit.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Score";
    public static final String PLAYERS = "players";
    public static final String HIGH_SCORE = "highScores";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN1_ID = BaseColumns._ID;
    public static final String PLAYER_ID = BaseColumns._ID;
    public static final String  SCORE = "score";
    public static final String  SCORES = "scores";
    public static final String  DATE = "DATE";
    public static final String PLAYER1 = "player1";
    public static final String PLAYER2 = "player2";
    public static final String PLAYER3 = "player3";
    public static final String PLAYER4 = "player4";
    public static final String PLAYER5 = "player5";



    private final String CURRENT_PLAYER_SCORE = "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME + "(" + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," + SCORE + " INTEGER );";
    private final String HIGHSCORE = "CREATE TABLE " + HIGH_SCORE + "(" + COLUMN1_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," + SCORES + " INTEGER,"+ DATE +" TEXT"+");";
    private final String REMOTE_PLAYERS_SCORE ="CREATE TABLE "+PLAYERS + "(" + PLAYER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+PLAYER1+" INTEGER,"+PLAYER2+" INTEGER,"+PLAYER3+" INTEGER,"+PLAYER4+" INTEGER,"+PLAYER5+" INTEGER );";

    public NDbhelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CURRENT_PLAYER_SCORE);
        sqLiteDatabase.execSQL(HIGHSCORE);
        sqLiteDatabase.execSQL(REMOTE_PLAYERS_SCORE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
