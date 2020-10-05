package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    private static final String TAG = "MyActivity";

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            // this is where I try to get the button ID, I think this will be useful if we
            // want to delete all info attached to the button from the data table?
            int buttonId = v.getId();
            System.out.println("DID THIS WORK? HERE IS THE ID" + buttonId);

            // this makes it disappear but doesn't delete it
            // v.setVisibility(View.GONE);

            // gets the ViewGroup (essentially the layout that the button is from) and
            // removes it (still comes back, but that is related to the data table, I believe)
            ViewGroup parentView = (ViewGroup) v.getParent();
            parentView.removeView(v);
            return true;
        }
    };

    private int numBubbles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        addBubble(goals);
    }

    private void addBubble(List<Goal> goalList) {
        for (Goal goal: goalList){
            RelativeLayout relativeLayout = findViewById(R.id.bubble_layout);
            View bubble = LayoutInflater.from(this).inflate(R.layout.bubble,null);
            bubble.setId(numBubbles);
            bubble.setBackgroundColor(goal.calculateColor());
            bubble.setX(new Random().nextInt(400));   //randomize location of bubble
            bubble.setY(new Random().nextInt(400));
            bubble.setOnLongClickListener(listener);
            relativeLayout.addView(bubble);
        }

/*
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        // connect start and end point of views, in this case top of child to top of parent.

        set.connect(bubble.getId(), ConstraintSet.TOP, bubble.getId(), ConstraintSet.TOP, 60);
        set.constrainWidth(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);*/

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