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
    public static Map<Long,Goal> goalById;
    int deleteButtonId;
    View deleteView;

    Random random;

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
        assert goal != null;
        goal.setGoalStatus(1);
        // shrink the button
        animate(deleteButtonView,0f, true);

        SQLiteDatabase db = dbHelper.getWritableDatabase();


//        // New value for completed Status
//        int goalStatus = 1;
//        ContentValues values = new ContentValues();
//        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS, goalStatus);
//
//        // Which row to update
//        String selection = FeedEntry.COLUMN_NAME_STATUS + " LIKE ?" ;
//        int[] selectionArgs = { 0 };
//
//        int count = db.update(
//                FeedReaderDbHelper.FeedEntry.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs);
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

        ViewPager viewPager = findViewById(R.id.pager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.tab1_text);
        tabLayout.getTabAt(1).setText(R.string.tab2_text);

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

    // takes in a bubble, scale size and shrinks/expands bubble to that scale. Also takes in a boolean
    // for deleting, if true, calls method to remove the bubble from the screen after animating
    private void animate(final View bubble, float finalScale, final boolean deleting) {
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
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;
    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new BubbleFragment());
        adapter.addFragment(new GoalListFragment());
        viewPager.setAdapter(adapter);
    }


}