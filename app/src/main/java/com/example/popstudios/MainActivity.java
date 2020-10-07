package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popstudios.databinding.ActivityMainBinding;
import com.example.popstudios.databinding.BubbleBinding;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    private static final String TAG = "MyActivity";
    ActivityMainBinding main;
    private int numBubbles;
    FeedReaderDbHelper dbHelper;

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

        dbHelper = new FeedReaderDbHelper(this);
        List<Goal> goals = dbHelper.getGoalsFromDb();
        addBubbles(goals);
//        addSingleBubble();
    }

/*
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        // connect start and end point of views, in this case top of child to top of parent.

        set.connect(bubble.getId(), ConstraintSet.TOP, bubble.getId(), ConstraintSet.TOP, 60);
        set.constrainWidth(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);*/

    @SuppressLint("InflateParams")
    private void addBubbles(List<Goal> goalList) {
        for (Goal goal : goalList) {
            CoordinatorLayout coordinatorLayout = findViewById(R.id.bubble_layout);
            View bubble = LayoutInflater.from(this).inflate(R.layout.bubble, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                bubble.setBackgroundTintList(ColorStateList.valueOf(goal.calculateColor()));
            bubble.setId((int) goal.getGoalID());
            bubble.setX(new Random().nextInt(300));   //randomize location of bubble
            bubble.setY(new Random().nextInt(300));
            bubble.setOnLongClickListener(listener);
            coordinatorLayout.addView(bubble);
        }
    }
/*
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        // connect start and end point of views, in this case top of child to top of parent.

        set.connect(bubble.getId(), ConstraintSet.TOP, bubble.getId(), ConstraintSet.TOP, 60);
        set.constrainWidth(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);*/

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
}