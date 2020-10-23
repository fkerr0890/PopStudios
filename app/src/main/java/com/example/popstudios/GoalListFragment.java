package com.example.popstudios;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A fragment containing the list of goals
 */
public class GoalListFragment extends Fragment {

    private String[] s1, s2, s3, s4;
    private RecyclerView recyclerView;
    static List<Long> addedGoals;
    static List<Long> completedGoals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        addedGoals = new ArrayList<>();
        completedGoals = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh();
        return view;
    }

    /**
     * Deletes all goals from the list
     */
    public void resetList() {
        addedGoals.clear();
        completedGoals.clear();
        s1 = null;
        s2 = null;
        s3 = null;
        s4 = null;
        refresh();
    }

    /**
     * Refresh the list in case a goal was added
     */
    private void refresh() {
        setUpGoalList(((MainActivity) Objects.requireNonNull(getActivity())).getDbHelper());
        MyAdapter myAdapter = new MyAdapter(getContext(),s1,s2,s3,s4);
        recyclerView.setAdapter(myAdapter);
    }

    /**
     * Adds goal information to string arrays that are used by the RecyclerView. Completed goals are added at the end of the list
     * @param dbHelper - current FeedReaderDbHelper
     */
    public void setUpGoalList(FeedReaderDbHelper dbHelper) {
        List<Goal> goalList = dbHelper.getGoalsFromDb();
        int num = 0;
        int listSize = goalList.size();
        s1 = new String[listSize];
        s2 = new String[listSize];
        s3 = new String[listSize];
        s4 = new String[listSize];
        for (Long id : completedGoals)
            addedGoals.remove(id);
        Iterator<Long> addedGoalsIterator = addedGoals.iterator();

        for (Goal goal : goalList) {
            if (!addedGoals.contains(goal.getGoalID())) {
                if (goal.getGoalStatus() == 0) {
                    setStrings(goal, num, false);
                    addedGoals.add(goal.getGoalID());
                    num++;
                }
                else if (!completedGoals.contains(goal.getGoalID())) {
                    completedGoals.add(goal.getGoalID());
                }
            }
            else {
                long id = addedGoalsIterator.next();
                Goal nextGoal = MainActivity.goalById.get(id);
                if (nextGoal == null) {
                    MainActivity.goalById.put(id, goal);
                    setStrings(goal, num, false);
                }
                else
                    setStrings(nextGoal,num,false);
                num++;
            }
        }
        for (Long goalId : completedGoals) {
            setStrings(Objects.requireNonNull(MainActivity.goalById.get(goalId)), num, true);
            num++;
        }
    }

    /**
     * Sets RecyclerView string arrays with goal info
     * @param goal The new goal
     * @param num The array index
     * @param completed True if the goal has been completed
     */
    private void setStrings(Goal goal, int num, boolean completed) {
        s1[num] = goal.name;
        if (completed)
            s1[num] = s1[num] + " (Completed)";
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