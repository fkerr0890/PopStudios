package com.example.popstudios;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.example.popstudios.databinding.BubbleBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.popstudios.MainActivity.goalById;
import static com.example.popstudios.MainActivity.dbHelper;

public class BubbleFragment extends Fragment {
    private FloatingActionButton firstBubble;
    private List<Long> addedGoals;
    private CoordinatorLayout layout;

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        // Gets ID and View from button that user LongClicks on and opens a Dialog window allowing user to choose whether to delete or not
        @Override
        public boolean onLongClick(View v) {
            int buttonId = v.getId();

            // set info of button to be deleted (view and ID)
            ((MainActivity)getActivity()).setDeleteButtonId(buttonId);
            ((MainActivity)getActivity()).setDeleteView(v);
            // System.out.println(buttonId);
            openDialog();
            return true;
        }

        // creates a new instance of the dialog class??
        public void openDialog() {
            DeleteButtonDialog dialog = new DeleteButtonDialog();
            dialog.show(getChildFragmentManager(), "example dialog");
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firstBubble = null;
        addedGoals = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_bubble,container,false);
        layout = view.findViewById(R.id.bubble_layout);
        addBubbles(layout);
        return view;
    }

    private void addBubbles(CoordinatorLayout layout) {
        List<Goal> goals = dbHelper.getGoalsFromDb();
        for (Goal goal : goals) {
            if (!addedGoals.contains(goal.getGoalID())) {
                goalById.put(goal.getGoalID(), goal);
                addBubble(goal, layout);
                addedGoals.add(goal.getGoalID());
            }
        }
    }

    private void addBubble(Goal goal, CoordinatorLayout layout) {
        Random random = new Random();
        FloatingActionButton bubble = BubbleBinding.inflate(getLayoutInflater()).getRoot();
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.width = goal.calculateRadius() * 2;
        params.height = goal.calculateRadius() * 2;
        bubble.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) bubble
                .setBackgroundTintList(ColorStateList.valueOf(goal.calculateColor()));
        bubble.setId((int) goal.getGoalID());
        bubble.setContentDescription("This is a goal. Name: " + goal.getName());
        bubble.setOnLongClickListener(listener);
        if (firstBubble == null) firstBubble = bubble;
        else {
            double angle = random.nextDouble() * 2 * Math.PI;
            bubble.setTranslationX(((float) bubble.getLayoutParams().width/2 +
                    (float) firstBubble.getLayoutParams().width/2) * (float) Math.cos(angle));
            bubble.setTranslationY(((float) bubble.getLayoutParams().width/2 +
                    (float) firstBubble.getLayoutParams().width/2) * (float) Math.sin(angle));
            System.out.println(firstBubble.getLayoutParams().width);
        }
        layout.addView(bubble);
    }

    @Override
    public void onResume(){
        super.onResume();
        addBubbles(layout);
    }

}
