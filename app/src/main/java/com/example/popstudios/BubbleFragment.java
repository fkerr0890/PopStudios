package com.example.popstudios;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.popstudios.databinding.BubbleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.popstudios.MainActivity.goalById;

/**
 * A fragment containing every bubble
 */
public class BubbleFragment extends Fragment {
    public static List<Long> addedGoals;
    private float width, height, currentScale;
    private ViewGroup layout;
    private View fragment;
    private List<View> bubbles;
    boolean firstTime;
    private float MULTIPLIER;
    private MainActivity mainActivity;

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        // Gets ID and View from button that user LongClicks on and opens a Dialog window allowing
        // user to choose whether to delete or not
        @Override
        public boolean onLongClick(View v) {
            int buttonId = v.getId();

            // set info of button to be deleted (view and ID)
            mainActivity.setDeleteButtonId(buttonId);
            mainActivity.setDeleteView(v);
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
        mainActivity = (MainActivity)getActivity();
        firstTime = true;
        MULTIPLIER = 0.65f;
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        currentScale = preferences.getFloat("Scale",1f);
        addedGoals = new ArrayList<>();
        bubbles = new ArrayList<>();
        fragment = inflater.inflate(R.layout.fragment_bubble,container,false);
        layout = fragment.findViewById(R.id.bubble_layout);
        setupLayoutListener(fragment, null);
        addBubbles();
        return fragment;
    }

    /**
     * Sets up the global layout listener for this fragment. Layout bubble is always called from here
     * @param view This fragment
     * @param addedBubble The bubble that was just added, or null if a bubble wasn't added
     */
    private void setupLayoutListener(final View view, final View addedBubble) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        width = view.getWidth();
                        height = view.getHeight();
                        if (addedBubble != null)
                            layoutBubble(addedBubble);
                        else {
                            for (View bubble : bubbles) {
                                layoutBubble(bubble);
                            }
                            firstTime = false;
                        }
                    }
                }
        );
    }

    /**
     * Clears lists and all bubbles from this fragment
     */
    public void resetBubbles() {
        bubbles.clear();
        addedGoals.clear();
        layout.removeAllViews();
    }

    /**
     * Gets goals from database, adds them to the goalById map, and calls add bubble for each goal
     */
    private void addBubbles() {
        List<Goal> goals = mainActivity.getDbHelper().getGoalsFromDb();
        for (Goal goal : goals) {
            if (!addedGoals.contains(goal.getGoalID())) {
                goalById.put(goal.getGoalID(), goal);
                if (goal.getGoalStatus()==0) {
                    addBubble(goal);
                    addedGoals.add(goal.getGoalID());
                }
            }
        }
    }

    /**
     * Sets a bubble's width, height, color, and adds it to the layout
     * @param goal The goal that will be represented as a bubble
     * @return The new bubble, as a view
     */
    private View addBubble(Goal goal) {
        View bubble = BubbleBinding.inflate(getLayoutInflater()).getRoot();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = (int) (goal.calculateRadius() * 2 * currentScale);
        params.height = (int) (goal.calculateRadius() * 2 * currentScale);
        bubble.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) bubble
                .setBackgroundTintList(ColorStateList.valueOf(goal.calculateColor()));
        bubble.setId((int) goal.getGoalID());
//        bubble.setElevation(1f/goal.calculateRadius()*10f);
        bubble.setContentDescription("Goal name: " + goal.getName());
        bubble.setOnLongClickListener(listener);
        layout.addView(bubble);
        bubbles.add(bubble);
        return bubble;
    }

    /**
     * Sets the bubble's x and y to a randomly until the bubble's position is valid. If a valid position is unavailable,
     * the size of all bubbles are scaled down
     * @param bubble The bubble to be laid out
     */
    private void layoutBubble(View bubble) {
        int x = 0;
        float numScales = 0f;
        float scale = (float)Math.pow(MULTIPLIER,numScales);
        do {
            Rect rect = new Rect();
            bubble.getHitRect(rect);
            bubble.setX((float) Math.random() * (getWidth() - rect.width()*scale));
            bubble.setY((float) Math.random() * (getHeight() - rect.height()*scale));
            x++;
            int maxIterations;
            if (firstTime)
                maxIterations = 8500;
            else
                maxIterations = 3000;
            if (x > maxIterations) {
                /*                resetBubbles();*/
                scaleBubbles(false);
                numScales++;
                scale = (float)Math.pow(MULTIPLIER,numScales);
                x = 0;
            }
        } while (!validatePosition(bubble, scale));
    }

    /**
     * Checks to see if bubble overlaps with any other bubble. If it does, it is moved to where it doesn't overlap with the other bubble.
     * If it still overlaps with another bubble, returns false.
     * @param bubble The bubble to validate
     * @param scale The current scale factor for this layout phase
     * @return True if bubble's position is valid, false if otherwise
     */
    private boolean validatePosition(final View bubble, float scale) {
        Rect bubble1Rect = new Rect();
        bubble.getHitRect(bubble1Rect);
        for (View otherBubble : bubbles) {
            Rect bubble2Rect = new Rect();
            otherBubble.getHitRect(bubble2Rect);
            if (bubble != otherBubble && MainActivity.circlesIntersect(bubble1Rect.centerX(),
                    bubble1Rect.centerY(), bubble2Rect.centerX(), bubble2Rect.centerY(),
                    bubble1Rect.width()*scale/2f,bubble2Rect.width()*scale/2f,6)) {
                if (bubble.getX() + (float) bubble.getWidth()*scale/2 < otherBubble.getX() +
                        (float) otherBubble.getWidth()*scale/2)
                    bubble.setX(otherBubble.getX() - bubble.getWidth()*scale * (float) 1.1);

                else if (bubble.getY() + (float) bubble.getHeight()*scale/2 < otherBubble.getY() +
                        (float) otherBubble.getHeight()*scale/2)
                    bubble.setY(otherBubble.getY() - bubble.getHeight() * scale * (float) 1.1);

                else if (bubble.getX() + (float) bubble.getWidth()*scale/2 > otherBubble.getX() +
                        (float) otherBubble.getWidth()*scale/2)
                    bubble.setX(otherBubble.getRight() + bubble.getWidth()*scale * (float) 1.1);

                else if (bubble.getY() + (float) bubble.getHeight()*scale/2 > otherBubble.getY() +
                        (float) otherBubble.getHeight()*scale/2)
                    bubble.setY(otherBubble.getBottom() + bubble.getHeight()*scale * (float) 1.1);
            }
        }

        for (View otherBubble : bubbles) {
            Rect bubble2Rect = new Rect();
            otherBubble.getHitRect(bubble2Rect);
            if (bubble != otherBubble && MainActivity.circlesIntersect(bubble1Rect.centerX(),
                    bubble1Rect.centerY(), bubble2Rect.centerX(), bubble2Rect.centerY(),
                    bubble1Rect.width() * scale /2f,bubble2Rect.width()*scale/2f,6))
                return false;
        }
        return true;
    }

    /**
     * Scales the size of every bubble up or down by a multiplier
     * @param up If true, the bubbles will scale up (increase in size)
     */
    private void scaleBubbles(boolean up) {
        float scale = MULTIPLIER;
        float prevScale = scale;
        if (up)
            scale = 1f / scale;
        for (View bubble : bubbles) {
            float newX = 0;
            float newY = 0;
            ViewGroup.LayoutParams params = bubble.getLayoutParams();
            if (up) {
                newX = bubble.getX() - bubble.getWidth() * (1 - prevScale);
                newY = bubble.getY() - bubble.getHeight() * (1 - prevScale);
            }
            params.width *= scale;
            params.height *= scale;
            bubble.setLayoutParams(params);
            if (!up) {
                newX = bubble.getX() + params.width * (1 - scale);
                newY = bubble.getY() + params.height * (1 - scale);
            }
            newX = Math.min(Math.max(0, newX), getWidth() - bubble.getWidth());
            newY = Math.min(Math.max(0, newY), getHeight() - bubble.getHeight());
            bubble.setX(newX);
            bubble.setY(newY);
        }
        currentScale *= scale;
        currentScale = Math.min(1,currentScale);
        saveScale();
    }

    /**
     * Calculates the percentage of bubbleArea to fragment area
     * @param deletedBubble The recently deleted bubble
     * @return the bubble load
     */
    private float calculateBubbleLoad(View deletedBubble) {
        float fragmentArea = getWidth() * getHeight();
        float bubbleArea = 0;
        for (View bubble : bubbles) {
            bubbleArea += Math.PI * Math.pow(bubble.getWidth()/2f,2);
        }
        if (deletedBubble != null)
            bubbleArea -= deletedBubble.getWidth();
        return bubbleArea/fragmentArea;
    }

    /**
     * Checks to see if the bubbles can increase in size after a bubble is deleted
     * @param deletedBubbleId - the id of the recently deleted bubble
     */
    public void checkForScaleUp(int deletedBubbleId) {
        View deletedBubble = findBubble(deletedBubbleId);
        if (calculateBubbleLoad(deletedBubble) < 0.2f && currentScale < 1f) {
            bubbles.remove(deletedBubble);
            scaleBubbles(true);
        }
        else
            bubbles.remove(deletedBubble);
    }

    /**
     * Searches the bubble list for a bubble with id
     * @param id the id of the bubble
     * @return the bubble
     */
    private View findBubble(int id) {
        for (View bubble : bubbles) {
            if (bubble.getId() == id) {
                return bubble;
            }
        }
        return null;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public List<View> getBubbles() {
        return bubbles;
    }

    public float getCurrentScale() {
        return currentScale;
    }

    public void setCurrentScale(float currentScale) {
        this.currentScale = currentScale;
    }

    /**
     * Saves the current scale factor to the device so it can be retrieved after the app restarts
     */
    private void saveScale() {
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("Scale", currentScale);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Goal> addedGoalList = new ArrayList<>();
        if (mainActivity != null) {
            for (Goal goal : mainActivity.getDbHelper().getGoalsFromDb()) {
                if (!addedGoals.contains(goal.getGoalID()))
                    addedGoalList.add(goal);
            }
            for (Goal goal : addedGoalList) {
                if (goal.getGoalStatus() == 0) {
                    goalById.put(goal.getGoalID(), goal);
                    setupLayoutListener(fragment, addBubble(goal));
                    addedGoals.add(goal.getGoalID());
                }
            }
        }
    }
}
