package com.example.popstudios;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.popstudios.databinding.BubbleBinding;

import java.util.ArrayList;
import java.util.List;

import static com.example.popstudios.MainActivity.goalById;
import static com.example.popstudios.MainActivity.dbHelper;

public class BubbleFragment extends Fragment {
    private List<Long> addedGoals;
    private ViewGroup layout;
    private List<View> bubbles;

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        // Gets ID and View from button that user LongClicks on and opens a Dialog window allowing
        // user to choose whether to delete or not
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

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        addedGoals = new ArrayList<>();
        bubbles = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_bubble,container,false);
        layout = view.findViewById(R.id.bubble_layout);
        addBubbles();
        scaleBubbles();
        System.out.println("DONE WITH LAYOUT"); // Layout test message

        return view;
    }

    private void addBubbles() {
        List<Goal> goals = dbHelper.getGoalsFromDb();
        for (Goal goal : goals) {
            if (!addedGoals.contains(goal.getGoalID()) && goal.getGoalStatus()==0) {
                goalById.put(goal.getGoalID(), goal);
                addBubble(goal);
                addedGoals.add(goal.getGoalID());
            }
        }
    }

    private void addBubble(Goal goal) {
        View bubble = BubbleBinding.inflate(getLayoutInflater()).getRoot();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = goal.calculateRadius() * 2;
        params.height = goal.calculateRadius() * 2;
        bubble.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) bubble
                .setBackgroundTintList(ColorStateList.valueOf(goal.calculateColor()));
        bubble.setId((int) goal.getGoalID());
        bubble.setContentDescription("This is a goal. Name: " + goal.getName());
        bubble.setOnLongClickListener(listener);
        layoutBubble(bubble);
        layout.addView(bubble);
        bubbles.add(bubble);
    }

    private void layoutBubble(final View bubble) {
        layout.post(new Runnable() {
            @Override
            public void run() {
                bubble.setX((float) Math.random() * (layout.getWidth() - bubble.getWidth()));
                bubble.setY((float) Math.random() * (layout.getHeight() - bubble.getHeight()));
            }
        });
        validatePosition(bubble);
    }

    private void validatePosition(final View bubble) {
        for (final View otherBubble : bubbles) {
            layout.post(new Runnable() {
                @Override
                public void run() {
                    if (isViewOverlapping(bubble, otherBubble)) {
                        if (bubble.getX() + (float) bubble.getWidth()/2 < otherBubble.getX() +
                                (float) otherBubble.getWidth()/2)
                            bubble.setX(otherBubble.getX() - bubble.getWidth() * (float) 1.1);

                        else if (bubble.getY() + (float) bubble.getHeight()/2 < otherBubble.getY() +
                                (float) otherBubble.getHeight()/2)
                            bubble.setY(otherBubble.getY() - bubble.getHeight() * (float) 1.1);

                        else if (bubble.getX() + (float) bubble.getWidth()/2 > otherBubble.getX() +
                                (float) otherBubble.getWidth()/2)
                            bubble.setX(otherBubble.getRight() + bubble.getWidth() * (float) 1.1);

                        else if (bubble.getY() + (float) bubble.getHeight()/2 > otherBubble.getY() +
                                (float) otherBubble.getHeight()/2)
                            bubble.setY(otherBubble.getBottom() + bubble.getHeight() * (float) 1.1);

                        else layoutBubble(bubble);
                    }
                }
            });
        }

        for (final View otherBubble : bubbles) {
            layout.post(new Runnable() {
                @Override
                public void run() {
                    if (isViewOverlapping(bubble, otherBubble)) layoutBubble(bubble);
                }
            });
        }
    }

    private boolean isViewOverlapping(View firstBubble, View secondBubble) {
        // Be sure to always place calls to this method inside a Runnable statement (layout.post())
        float firstRadius = (float) firstBubble.getWidth()/2,
                secondRadius = (float) secondBubble.getWidth()/2;
        return Math.hypot(firstBubble.getX() + firstRadius - (secondBubble.getX() + secondRadius),
                firstBubble.getY() + firstRadius - (secondBubble.getY() + secondRadius)) <
                firstRadius + secondRadius;
    }

    private void scaleBubbles() {
        for (final View bubble : bubbles) {
            layout.post(new Runnable() {
                @Override
                public void run() {
                    float scale = getScale();
                    ViewGroup.LayoutParams params = bubble.getLayoutParams();
                    params.width *= scale;
                    params.height *= scale;
                    bubble.setLayoutParams(params);

                    bubble.setX(bubble.getX() + ((float) layout.getWidth()/2 - bubble.getX()) *
                            (1 - scale));
                    bubble.setY(bubble.getY() + ((float) layout.getHeight()/2 - bubble.getY()) *
                            (1 - scale));
                }
            });
        }
    }

    private float getScale() {
        return (float) 0.5;
    }

    @Override
    public void onResume() {
        super.onResume();
        addBubbles();
    }
}
