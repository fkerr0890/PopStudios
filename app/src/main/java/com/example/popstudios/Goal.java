package com.example.popstudios;

public class Goal {

    public String name;
    public int goalImportance;
    public int goalDifficulty;

    Goal(String name, int goalImportance, int goalDifficulty){
        this.name = name;
        this.goalImportance = goalImportance;
        this.goalDifficulty = goalDifficulty;
    }

    public String getName(){
        return name;
    }

    public int getGoalImportance(){
        return goalImportance;
    }

    public int getGoalDifficulty(){
        return goalDifficulty;
    }

}
