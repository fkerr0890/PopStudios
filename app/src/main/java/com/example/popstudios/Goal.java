package com.example.popstudios;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Goal {

    public String name;
    public int goalImportance;
    public int goalDifficulty;
    public long id;
    public List<Integer> colorList;

    Goal(long id, String name, int goalImportance, int goalDifficulty){
        this.id = id;
        this.name = name;
        this.goalImportance = goalImportance;
        this.goalDifficulty = goalDifficulty;
    }

    public long getGoalID(){return id;}

    public String getName(){
        return name;
    }

    public int getGoalImportance(){
        return goalImportance;
    }

    public int getGoalDifficulty(){
        return goalDifficulty;
    }

    public int calculateRadius(){
        return (this.goalImportance + this.goalDifficulty) * 20;
    }

    public int calculateColor(){
        int color11 = Color.argb(255, 116, 0, 184);
        int color10 = Color.argb(255, 110, 23, 190);
        int color9 = Color.argb(255, 105, 48, 195);
        int color8 = Color.argb(255, 94, 96, 206);
        int color7 = Color.argb(255, 83, 144, 217);
        int color6 = Color.argb(255, 78, 168, 222);
        int color5 = Color.argb(255, 72, 191, 227);
        int color4 = Color.argb(255, 86, 207, 225);
        int color3 = Color.argb(255, 100, 223, 223);
        int color2 = Color.argb(255, 114, 239, 221);
        int color1 = Color.argb(255, 128, 255, 219);

        colorList = new ArrayList<>();
        colorList.add(color1);
        colorList.add(color2);
        colorList.add(color3);
        colorList.add(color4);
        colorList.add(color5);
        colorList.add(color6);
        colorList.add(color7);
        colorList.add(color8);
        colorList.add(color9);
        colorList.add(color10);
        colorList.add(color11);

        int rating = this.goalImportance + this.goalDifficulty;
        return colorList.get(rating);
    }

}
