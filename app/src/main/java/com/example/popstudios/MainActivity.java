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
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.popstudios.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, DeleteButtonDialog.DeleteButtonDialogListener {
    //    private static final String TAG = "MyActivity";
    ActivityMainBinding main;
    public static FeedReaderDbHelper dbHelper;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    private List<Integer> listOfExpandedBubbles;
    public static float screenWidth;
    private static float screenHeight;
    public static Map<Long,Goal> goalById;
    int deleteButtonId;
    View deleteView;
    private BubbleFragment bubbleFragment;
    private static int moveCount;

    // Creates a new listener for when user long clicks
    View.OnLongClickListener listener = new View.OnLongClickListener() {
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
        // visually delete button by removing it from ViewGroup
        // ViewGroup parentView = (ViewGroup) deleteButtonView.getParent();
        // parentView.removeView(deleteButtonView);
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
        animate(deleteButtonView,0f, true);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for completed Status
        int goalStatus = 1;

        System.out.println(goal.getGoalStatus());
        ContentValues newValues = new ContentValues();
        newValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS, goalStatus);
        db.update(FeedReaderContract.FeedEntry.TABLE_NAME, newValues,
                FeedReaderContract.FeedEntry._ID + " = " + goal.getGoalID(),null);
        goal.setGoalStatus(1);
        GoalListFragment.completedGoals.add((long) deleteButtonId);
        System.out.println(goal.getGoalStatus());
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
        screenHeight = metrics.heightPixels;
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        ViewPager viewPager = findViewById(R.id.pager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        tab1.setText(R.string.tab1_text);
        tab1.setIcon(R.mipmap.baseline_bubble_chart_white_36);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        tab2.setText(R.string.tab2_text);
        tab2.setIcon(R.mipmap.baseline_list_white_36);

        dbHelper = new FeedReaderDbHelper(this);
        goalById = new HashMap<>();



    }

    private Balloon createBalloon() {
        return new Balloon.Builder(this)
                .setLayout(R.layout.balloon_layout)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
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

    public void startInputActivity(View view) {
        Intent inputActivityIntent = new Intent(this,InputActivity.class);
        startActivity(inputActivityIntent);
    }

    public void startHelp(View view){
        Intent helpActivityIntent = new Intent(this,FAQActivity.class);
        startActivity(helpActivityIntent);
    }

    public void startEditInputActivity(View view){
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

    public void showInfo(View view) {
        Goal goal = goalById.get((long)view.getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(goal.getDescription()).setTitle(goal.name);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void animateBubble(View view) {
        if (currentAnimator != null)
            currentAnimator.cancel();
        moveCount = 0;
        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
/*        view.setPivotX(0f);
        view.setPivotY(0f);*/
        float scale;
        if (listOfExpandedBubbles.contains(view.getId())) {
            scale = 1f;
            listOfExpandedBubbles.remove((Integer) view.getId());
        }
        else {
            scale = (float)(2*Math.pow(30,(view.getWidth()*0.001-0.2)*-1)+1);
            listOfExpandedBubbles.add(view.getId());
            Balloon bubbleInfo = createBalloon();
            ImageButton editButton = bubbleInfo.getContentView().findViewById(R.id.editButton);
            if (editButton != null)
                editButton.setId(view.getId());
            ImageButton infoButton = bubbleInfo.getContentView().findViewById(R.id.infoButton);
            if (infoButton != null)
                infoButton.setId(view.getId());
            TextView textView = bubbleInfo.getContentView().findViewById(R.id.textView);
            textView.setText(Objects.requireNonNull(goalById.get((long) view.getId())).getName());
            bubbleInfo.showAlignTop(view);
        }
        animate(view,scale, false);
    }

    private List<View> findIntersectingBubbles(View bubble1) {
        Rect bubble1Rect = new Rect();
        bubble1.getHitRect(bubble1Rect);
        List<View> result = new ArrayList<>();
        for (View bubble2 : bubbleFragment.getBubbles()) {
            Rect bubble2Rect = new Rect();
            bubble2.getHitRect(bubble2Rect);
            if (bubble1 != bubble2 && circlesIntersect(bubble1Rect.centerX(),bubble1Rect.centerY(),bubble2Rect.centerX(),bubble2Rect.centerY(),bubble1Rect.width()/2f,bubble2Rect.width()/2f, 3/(moveCount*0.20+0.5)))
                result.add(bubble2);
        }
        return result;
    }

    public static boolean circlesIntersect(float x1, float y1, float x2, float y2, float r1, float r2, double amountOverlap) {
        float distSq = (float)(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2));
        float radSumSq = (float)Math.pow(r1 + r2,2);
        return distSq <= radSumSq && radSumSq - distSq >= radSumSq/(amountOverlap);
    }

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
            animate(bubble2, finalX, finalY);
        }
    }

    // takes in a bubble, scale size and shrinks/expands bubble to that scale. Also takes in a boolean
    // for deleting, if true, calls method to remove the bubble from the screen after animating
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
                }
                else if (finalScale != 1f) {
                    for (View intersectingBubble : findIntersectingBubbles(bubble)) {
                        int numBubbles = bubbleFragment.getBubbles().size();
                        if (moveCount < numBubbles*numBubbles)
                            dodgeEachOther(bubble,intersectingBubble);
                        moveCount++;
                    }
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
                    if (moveCount < numBubbles*numBubbles)
                        dodgeEachOther(bubble,intersectingBubble);
                    moveCount++;
                }
            }

        });
        set.start();
    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        bubbleFragment = new BubbleFragment();
        adapter.addFragment(bubbleFragment);
        adapter.addFragment(new GoalListFragment());
        viewPager.setAdapter(adapter);
    }


}