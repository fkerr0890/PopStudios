package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class GoalList extends AppCompatActivity {

    FeedReaderDbHelper dbHelper;
    TextView incompleteGoalsList;
    TextView completeGoalsList;
    String incompleteGoals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_list);
        completeGoalsList = findViewById(R.id.completeGoalsList);
        incompleteGoalsList = findViewById(R.id.incompleteGoalsList);

        completeGoalsList.setText(getGoalsList());
    }


    GoalList(FeedReaderDbHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public String getGoalsList() {
        List<Goal> goalList = dbHelper.getGoalsFromDb();
        for (Goal goal : goalList) {
            incompleteGoals += "" + goal.name;
        }
        return incompleteGoals;
    }
}