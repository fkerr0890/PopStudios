// Main Activity is where all the user's goals appear as bubbles. It is the main page of our app
// and holds methods for adding bubbles to a list, deleting/completing a goal, placing bubbles,
// bubble animations, and can open input, edit, FAQ pages, and the list view of bubbles. We would
// have liked to refactor this if we had more time

package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.popstudios.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The main activity of this app that contains the bubble and goalList fragments and handles bubble animation
 */
public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, DeleteButtonDialog.DeleteButtonDialogListener {
    ActivityMainBinding main;
    private FeedReaderDbHelper dbHelper;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    private List<Integer> listOfExpandedBubbles;
    public static float screenWidth;
    public static Map<Long, Goal> goalById;
    int deleteButtonId;
    View deleteView;
    private BubbleFragment bubbleFragment;
    private GoalListFragment goalListFragment;
    private static int moveCount;
    boolean itHitTheWall;
    private Balloon currentBalloon;
    public static Goal editedGoal;
    public static long editedGoalId = -1;

    // Creates a new listener for when user long clicks

    @Override
    protected void onResume() {
        super.onResume();
        if (editedGoalId != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                goalById.replace(editedGoalId,editedGoal);
            }
            for (Integer id : listOfExpandedBubbles) {
                listOfExpandedBubbles.remove(id);
                animate(findViewById(id), 1f, false);
            }
            for (View bubble : bubbleFragment.getBubbles()) {
                if (bubble.getId() == editedGoalId) {
                    ViewGroup.LayoutParams params = bubble.getLayoutParams();
                    params.width = (int) (editedGoal.calculateRadius() * 2 * bubbleFragment.getCurrentScale());
                    params.height = (int) (editedGoal.calculateRadius() * 2 * bubbleFragment.getCurrentScale());
                    bubble.setLayoutParams(params);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) bubble
                            .setBackgroundTintList(ColorStateList.valueOf(editedGoal.calculateColor()));
                }

            }
        }
    }


    // setters and getters for deleting button View and ID
    public void setDeleteButtonId(int newDeleteButtonId) {
        this.deleteButtonId = newDeleteButtonId;
    }
    public int getDeleteButtonId() {
        return deleteButtonId;
    }

    public void setDeleteView(View view) {
        this.deleteView = view;
    }
    public View getDeleteView() {
        return deleteView;
    }

    public FeedReaderDbHelper getDbHelper() {
        return dbHelper;
    }

    // Called when the user clicks "delete"
    @Override
    public void onYesClicked() {
        // get info from longClick
        int deleteButtonId = getDeleteButtonId();
        View deleteButtonView = getDeleteView();

        // shrink the button
        animate(deleteButtonView,0f, true);

        // us ID to delete from Database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FeedReaderContract.FeedEntry._ID + " = " + (long)deleteButtonId;
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, null);
        GoalListFragment.addedGoals.remove((long)deleteButtonId);
        BubbleFragment.addedGoals.remove((long)deleteButtonId);
        bubbleFragment.checkForScaleUp(deleteButtonId);
        // visually delete button by removing it from ViewGroup
    }

    // Called when the user clicks "completed" in dialog
    @Override
    public void onNeutralClicked() {
        // get info from longClick
        int deleteButtonId = getDeleteButtonId();
        View deleteButtonView = getDeleteView();
        Goal goal = goalById.get((long)deleteButtonId);

        // I followed the recommendation of java, but I think that seems risky? Maybe if
//        assert goal != null;
//        goal.setGoalStatus(1);
        // shrink the button
        if (goal != null) {
            animate(deleteButtonView, 0f, true);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // New value for completed Status
            int goalStatus = 1;

            ContentValues newValues = new ContentValues();
            newValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS, goalStatus);
            db.update(FeedReaderContract.FeedEntry.TABLE_NAME, newValues,
                    FeedReaderContract.FeedEntry._ID + " = " + goal.getGoalID(), null);
            goal.setGoalStatus(1);
            GoalListFragment.completedGoals.add((long) deleteButtonId);
            bubbleFragment.checkForScaleUp(deleteButtonId);
            System.out.println(goal.getGoalStatus());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main.getRoot());
        listOfExpandedBubbles = new ArrayList<>();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        itHitTheWall = false;

        setupListener();
        ViewPager viewPager = findViewById(R.id.pager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        if (tab1 != null && tab2 != null) {
            tab1.setText(R.string.tab1_text);
            tab1.setIcon(R.mipmap.baseline_bubble_chart_white_36);

            tab2.setText(R.string.tab2_text);
            tab2.setIcon(R.mipmap.baseline_list_white_36);
        }
        dbHelper = new FeedReaderDbHelper(this);
        goalById = new HashMap<>();
    }

    /**
     * Sets up the long click listener that, when triggered, shows the complete/delete goal dialog
     */
    private void setupListener() {
        new View.OnLongClickListener() {
            // Gets ID and View from button that user LongClicks on and opens a Dialog window allowing user
            // to choose whether to delete or not
            @Override
            public boolean onLongClick(View v) {
                int buttonId = v.getId();

                // set info of button to be deleted (view and ID)
                setDeleteButtonId(buttonId);
                setDeleteView(v);
                openDialog();
                return true;
            }

            // creates a new instance of the dialog class
            public void openDialog() {
                DeleteButtonDialog dialog = new DeleteButtonDialog();
                dialog.show(getSupportFragmentManager(), "example dialog");
            }
        };
    }

    /**
     * Creates a new balloon
     * @return a new balloon
     */
    private Balloon createBalloon() {
        return new Balloon.Builder(this)
                .setLayout(R.layout.balloon_layout)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.35f)
                .setHeight(100)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(1f)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.balloon))
                .setBalloonAnimation(BalloonAnimation.CIRCULAR)
                .build();
    }

    /**
     * Starts a new InputActivity via intent
     * @param view The add button
     */
    public void startInputActivity(View view) {
        Intent inputActivityIntent = new Intent(this, InputActivity.class);
        startActivity(inputActivityIntent);
    }

    /**
     * Starts a new FAQActivity via intent
     * @param view The help button
     */
    public void startHelp(View view){
        Intent helpActivityIntent = new Intent(this, FAQActivity.class);
        startActivity(helpActivityIntent);
    }

    /**
     * Starts a new InputActivity for editing a gaol via intent with extras containing the goal's existing information
     * @param view The edit button
     */
    public void startEditInputActivity(View view){
        if (currentBalloon != null)
            currentBalloon.dismiss();

        Intent inputEditActivityIntent = new Intent(this,InputActivity.class);

        Goal goal = goalById.get((long)view.getId());
        assert goal != null;
        inputEditActivityIntent.putExtra("GOAL_ID", goal.getGoalID());

        String goalName = goal.getName();
        inputEditActivityIntent.putExtra("GOAL_NAME", goalName);

        int goalImportance = goal.getGoalImportance();
        inputEditActivityIntent.putExtra("GOAL_IMPORTANCE", goalImportance);

        int goalDifficulty = goal.getGoalDifficulty();
        inputEditActivityIntent.putExtra("GOAL_DIFFICULTY", goalDifficulty);

        String goalDescription = goal.getDescription();
        inputEditActivityIntent.putExtra("GOAL_DESCRIPTION", goalDescription);

        startActivity(inputEditActivityIntent);
    }

    /**
     * Shows a goal's info
     * @param view The info button
     */
    public void showInfo(View view) {
        Goal goal = goalById.get((long)view.getId());
        if (goal != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(goal.getDescription()).setTitle(goal.name);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    /**
     * After collapsing any expanded bubbles, this calls animate() with a scale of 1f if the bubble is already expanded.
     * If it isn't expanded, an appropriate final scale for the bubble is calculated based on the bubble's size and animate() is called
     * with that scale.
     * @param view The bubble the user clicked
     */
    public void animateBubble(View view) {
        if (currentAnimator != null)
            currentAnimator.cancel();
        moveCount = 0;

        for (Integer id : listOfExpandedBubbles) {
            View expandedBubble = findViewById(id);
            if (expandedBubble != view) {
                listOfExpandedBubbles.remove(id);
                currentBalloon.dismiss();
                animate(findViewById(id), 1f, false);
            }
        }

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
/*        view.setPivotX(0f);
        view.setPivotY(0f);*/
        float scale;
        if (listOfExpandedBubbles.contains(view.getId())) {
            currentBalloon.dismiss();
            scale = 1f;
            listOfExpandedBubbles.remove((Integer) view.getId());
        }
        else {
            scale = (float)(2*Math.pow(30,(view.getWidth()*0.001-0.2)*-1)+1);
            listOfExpandedBubbles.add(view.getId());
        }
        animate(view,scale, false);
    }

    /**
     * Attaches a balloon to the expanded bubble. The balloon displays the goal name, edit and info buttons
     * @param view The bubble on which to attach the balloon
     */
    private void addBalloon(View view) {
        Balloon bubbleInfo = createBalloon();
        ImageButton editButton = bubbleInfo.getContentView().findViewById(R.id.editButton);
        if (editButton != null)
            editButton.setId(view.getId());
        ImageButton infoButton = bubbleInfo.getContentView().findViewById(R.id.infoButton);
        if (infoButton != null)
            infoButton.setId(view.getId());
        TextView textView = bubbleInfo.getContentView().findViewById(R.id.textView);
        textView.setText(Objects.requireNonNull(goalById.get((long) view.getId())).getName());
        Rect rect = new Rect();
        view.getHitRect(rect);
        bubbleInfo.showAlignTop(view, 160,30);
        currentBalloon = bubbleInfo;
    }

    /**
     * Returns a list of bubbles that overlap with bubble1
     * @param bubble1 The bubble in question
     * @return A list of intersecting bubbles
     */
    private List<View> findIntersectingBubbles(View bubble1) {
        Rect bubble1Rect = new Rect();
        bubble1.getHitRect(bubble1Rect);
        List<View> result = new ArrayList<>();
        for (View bubble2 : bubbleFragment.getBubbles()) {
            Rect bubble2Rect = new Rect();
            bubble2.getHitRect(bubble2Rect);
            if (bubble1 != bubble2 && circlesIntersect(bubble1Rect.centerX(),bubble1Rect.centerY(),bubble2Rect.centerX(),bubble2Rect.centerY(),bubble1Rect.width()/2f,bubble2Rect.width()/2f, 3/(moveCount*0.20+0.2)))
                result.add(bubble2);
        }
        return result;
    }

    /**
     * Determines if one circle overlaps another
     * @param x1 The center x of circle 1
     * @param y1 The center y of circle 1
     * @param x2 The center x of circle 2
     * @param y2 The center y of circle 2
     * @param r1 The radius of circle 1
     * @param r2 The radius of circle 2
     * @param amountOverlap 1/amountOverlap = the fraction the circles' areas that are permitted to overlap
     * @return True if the circles overlap
     */
    public static boolean circlesIntersect(float x1, float y1, float x2, float y2, float r1, float r2, double amountOverlap) {
        float distSq = (float)(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2));
        float radSumSq = (float)Math.pow(r1 + r2,2);
        if ((int)distSq == 0)
            return true;
        return distSq <= radSumSq && radSumSq - distSq >= radSumSq/(amountOverlap);
    }

    /**
     * Animates bubble2 to one of the corners of bubble1
     * @param bubble1 first bubble
     * @param bubble2 second bubble
     */
    private void dodgeEachOther(View bubble1, View bubble2) {
        Rect bubble1Rect = new Rect();
        bubble1.getHitRect(bubble1Rect);
        Rect bubble2Rect = new Rect();
        bubble2.getHitRect(bubble2Rect);
        if (bubble2 != bubble1 && Rect.intersects(bubble1Rect,bubble2Rect)) {
            float finalX;
            float finalY;
            float hypot = (float)Math.sqrt(Math.pow(bubble1Rect.width()/2d,2) + Math.pow(bubble1Rect.height()/2d,2)) - bubble1Rect.width()/2f;
            float xOffset = (float) (hypot * Math.cos(Math.toRadians(45)));
            float yOffset = (float) (hypot * Math.sin(Math.toRadians(45)));
            if (Math.abs(bubble1Rect.left-bubble2Rect.right) < Math.abs(bubble2Rect.left-bubble1Rect.right)) {
                finalX = bubble1Rect.left-bubble2.getWidth() + xOffset;
            }
            else
                finalX = bubble1Rect.right - xOffset;
            if (Math.abs(bubble1Rect.top-bubble2Rect.bottom) < Math.abs(bubble2Rect.top-bubble1Rect.bottom)) {
                finalY = bubble1Rect.top-bubble2.getHeight() + yOffset;
            }
            else
                finalY = bubble1Rect.bottom - yOffset;

            finalX = Math.min(Math.max(0,finalX),bubbleFragment.getWidth()-bubble2.getWidth());
            finalY = Math.min(Math.max(0,finalY),bubbleFragment.getHeight()-bubble2.getHeight());
            if (finalX == 0 || finalX == bubbleFragment.getWidth()-bubble2.getWidth() || finalY == 0 || finalY == bubbleFragment.getHeight()-bubble2.getHeight())
                itHitTheWall = true;
            animate(bubble2, finalX, finalY);
        }
    }


    /**
     * Animates the scaleX and scaleY of bubble to finalScale. If bubble is being deleted, it is removed from the layout.
     * If the bubble expanded it adds a balloon to bubble, and if it intersects with other bubbles, the overlapping bubbles move out of the way
     * @param bubble The bubble to be scaled
     * @param finalScale The final scale of bubble
     * @param deleting True if bubble is being deleted
     */
    private void animate(final View bubble, final float finalScale, final boolean deleting) {
        // Construct and run the parallel animation of the four translation and
        // scale properties (SCALE_X and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(bubble, View.SCALE_X,finalScale))
                .with(ObjectAnimator.ofFloat(bubble,
                        View.SCALE_Y, finalScale));

        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            // When the animation is done and if deleting is true removes the bubble entirely from screen
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
                if (deleting) {
                    ViewGroup parentView = (ViewGroup) bubble.getParent();
                    parentView.removeView(bubble);
                    listOfExpandedBubbles.remove((Integer)bubble.getId());
                }
                else if (finalScale != 1f) {
                    for (View intersectingBubble : findIntersectingBubbles(bubble)) {
                        int numBubbles = bubbleFragment.getBubbles().size();
                        if (moveCount < numBubbles*numBubbles)
                            dodgeEachOther(bubble,intersectingBubble);
                        moveCount++;
                    }
                    if (listOfExpandedBubbles.contains(bubble.getId()))
                        addBalloon(bubble);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;
    }

    /**
     * Animates the x and y positions of bubble to finalX and finalY. If the bubble now overlaps other bubbles,
     * the other bubbles move out of the way, unless you're up against the wall
     * @param bubble The bubble to animate
     * @param finalX Final x position
     * @param finalY Final y position
     */
    private void animate(final View bubble, float finalX, float finalY) {
        final AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(bubble,View.X,finalX))
                .with(ObjectAnimator.ofFloat(bubble,View.Y,finalY));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (View intersectingBubble : findIntersectingBubbles(bubble)) {
                    int numBubbles = bubbleFragment.getBubbles().size();
                    if (moveCount < numBubbles*numBubbles) {
                        if (itHitTheWall)
                            itHitTheWall = false;
                        else
                            dodgeEachOther(bubble, intersectingBubble);
                    }
                    moveCount++;
                    bubble.bringToFront();
                }
/*                if (listOfExpandedBubbles.contains(bubble.getId()))
                    addBalloon(bubble);*/
            }

        });
        set.start();
    }

    /**
     * When the delete button is clicked, a dialog asks for confirmation. Upon confirmation, all goals in the bubble and goalList fragments
     * are cleared and the database is deleted.
     * @param view The delete button
     */
    public void deleteAllGoals(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This will delete all of your goals, including completed ones").setTitle("Delete all goals?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goalById.clear();
                dbHelper.getContext().deleteDatabase(dbHelper.getDatabaseName());
                dbHelper.close();
                bubbleFragment.resetBubbles();
                goalListFragment.resetList();
                bubbleFragment.setCurrentScale(1);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Sets up the ViewPager that contains the bubble and goalList fragments and handles switching/swiping between them
     * @param viewPager The current viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        bubbleFragment = new BubbleFragment();
        goalListFragment = new GoalListFragment();
        adapter.addFragment(bubbleFragment);
        adapter.addFragment(goalListFragment);
        viewPager.setAdapter(adapter);
    }
}