package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

public class GoalList extends AppCompatActivity {

    FeedReaderDbHelper dbHelper;
    TextView incompleteGoalsList;
    TextView completeGoalsList;
    String incompleteGoals;
    String s1[];
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_list2);
        recyclerView = findViewById(R.id.recyclerView);
        s1 = getResources().getStringArray(R.array.incompleteGoalList);


        MyAdapter myAdapter = new MyAdapter(this,s1);
//        completeGoalsList = findViewById(R.id.completeGoalsList);
//        incompleteGoalsList = findViewById(R.id.incompleteGoalsList);

//        completeGoalsList.setText(getGoalsList());
    }


//    GoalList(FeedReaderDbHelper dbHelper){
//        this.dbHelper = dbHelper;
//    }
//    public String getGoalsList() {
//        List<Goal> goalList = dbHelper.getGoalsFromDb();
//        for (Goal goal : goalList) {
//            incompleteGoals += "" + goal.name;
//        }
//        return incompleteGoals;
//    }
}