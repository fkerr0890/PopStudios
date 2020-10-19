package com.example.popstudios;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoalListFragment extends Fragment {

    /*    FeedReaderDbHelper dbHelper;
        TextView incompleteGoalsList;
        TextView completeGoalsList;
        String incompleteGoals;*/
    String[] s1, s2, s3, s4;
    RecyclerView recyclerView;
    static List<Long> addedGoals;
/*
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
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        addedGoals = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh();
        return view;
    }

    private void refresh() {
        setUpGoalList(MainActivity.dbHelper);
        MyAdapter myAdapter = new MyAdapter(getContext(),s1,s2,s3,s4);
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
        Iterator<Long> addedGoalsIterator = addedGoals.iterator();
        for (Goal goal : goalList) {
            if (!addedGoals.contains(goal.getGoalID())) {
                setStrings(goal,num);
                addedGoals.add(goal.getGoalID());
            }
            else
                setStrings(MainActivity.goalById.get(addedGoalsIterator.next()),num);
            num++;
        }
    }

    private void setStrings(Goal goal, int num) {
        s1[num] = goal.name;
        s2[num] = "Importance: " + goal.getGoalImportance();
        s3[num] = "Difficulty: " + goal.getGoalDifficulty();
        s4[num] = goal.getDescription();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}