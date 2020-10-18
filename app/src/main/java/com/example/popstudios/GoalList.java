package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class GoalList extends AppCompatActivity {

    FeedReaderDbHelper dbHelper;
    TextView incompleteGoalsList;
    TextView completeGoalsList;
    String incompleteGoals;
    String[] s1, s2, s3, s4;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new FeedReaderDbHelper(this);
        setUpGoalList(dbHelper);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(this,s1,s2,s3,s4);
        recyclerView.setAdapter(myAdapter);
    }


    public void setUpGoalList(FeedReaderDbHelper dbHelper) {
        List<Goal> goalList = dbHelper.getGoalsFromDb();
        int num = 0;
        int listSize = goalList.size();
        s1 = new String[listSize];
        s2 = new String[listSize];
        s3 = new String[listSize];
        s4 = new String[listSize];
        for (Goal goal : goalList) {
           s1[num] = goal.name;
           s2[num] = "Importance: " + goal.getGoalImportance();
           s3[num] = "Difficulty: " + goal.getGoalDifficulty();
           s4[num] = goal.getDescription();
           num++;
        }
    }
}