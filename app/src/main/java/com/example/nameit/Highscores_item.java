package com.example.nameit;

public class Highscores_item {
  int mNumber;
  int mScore;
  String mDate;


  public Highscores_item(int Number,int Score,String Date){
      mNumber = Number;
      mDate = Date;
      mScore = Score;


  }

  public int getNumber(){


      return mNumber;
  }

  public  int getScore(){

      return mScore;
  }
public String getDate(){

      return mDate;
}



}
