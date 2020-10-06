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

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            // this is where I try to get the button ID, I think this will be useful if we
            // want to delete all info attached to the button from the data table?
            int buttonId = v.getId();
            System.out.println("DID THIS WORK? HERE IS THE ID" + buttonId);

            // this makes it disappear but doesn't delete it
            // v.setVisibility(View.GONE);

            // gets the ViewGroup (essentially the layout that the button is from) by ca
            ViewGroup parentView = (ViewGroup) v.getParent();
            parentView.removeView(v);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        numBubbles = 0;

//        Reads database
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY
        };


        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Goal> goals = new LinkedList<>();
        while(cursor.moveToNext()) {
//            long itemId = cursor.getLong(
//                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String goalName = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME));
            int goalImportance = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE));
            int goalDifficulty = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY));
            Goal newGoal = new Goal(goalName, goalImportance, goalDifficulty);
            goals.add(newGoal);
        }
        cursor.close();
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
            bubble.setId(numBubbles);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                bubble.setBackgroundTintList(ColorStateList.valueOf(goal.calculateColor()));
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
        bubble.setId(numBubbles);
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