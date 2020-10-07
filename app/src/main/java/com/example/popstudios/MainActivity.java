package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.example.popstudios.databinding.ActivityMainBinding;
import com.example.popstudios.databinding.BubbleBinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    private static final String TAG = "MyActivity";
    ActivityMainBinding main;
    private int numBubbles;
    FeedReaderDbHelper dbHelper;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    private List<Integer> listOfExpandedBubbles;

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int buttonId = v.getId();
            System.out.println(buttonId);
            this.deleteGoal(buttonId);

            // this makes it disappear but doesn't delete it
            // v.setVisibility(View.GONE);

            // gets the ViewGroup (essentially the layout that the button is from) by ca
            ViewGroup parentView = (ViewGroup) v.getParent();
            parentView.removeView(v);

            return true;
        }

        private void deleteGoal(int buttonID) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selection = FeedReaderContract.FeedEntry._ID + " = " + (long)buttonID;
            db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, null);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        numBubbles = 0;
        listOfExpandedBubbles = new ArrayList<>();
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        dbHelper = new FeedReaderDbHelper(this);
        List<Goal> goals = dbHelper.getGoalsFromDb();
        addBubbles(goals);
        //addSingleBubble();
    }

    @SuppressLint("InflateParams")
    private void addBubbles(List<Goal> goalList) {
        for (Goal goal : goalList) {
            CoordinatorLayout coordinatorLayout = findViewById(R.id.bubble_layout);
            View bubble = LayoutInflater.from(this).inflate(R.layout.bubble, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                bubble.setBackgroundTintList(ColorStateList.valueOf(goal.calculateColor()));
            bubble.setId((int) goal.getGoalID());
            bubble.setX(new Random().nextInt(1000));   //randomize location of bubble
            bubble.setY(new Random().nextInt(1000));
            bubble.setOnLongClickListener(listener);
            coordinatorLayout.addView(bubble);
        }
    }


    private void addSingleBubble() {
        View bubble = BubbleBinding.inflate(getLayoutInflater()).getRoot();
        bubble.setLayoutParams(new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT));
        ((CoordinatorLayout.LayoutParams) bubble.getLayoutParams()).gravity = Gravity.CENTER;
        main.bubbleLayout.addView(bubble);
    }

    public void startInputActivity(View view) {
        Intent inputActivityIntent = new Intent(this,InputActivity.class);
        startActivity(inputActivityIntent);
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
            scale = 4f;
            listOfExpandedBubbles.add(view.getId());
        }
        animate(view,scale);
    }


    private void animate(View bubble, float finalScale) {
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
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;
    }
}