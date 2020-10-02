package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private int numBubbles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numBubbles = 0;
        addBubble();
    }

    private void addBubble() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        Bubble bubble = new Bubble(this, 100, Bubble.getRandomColor());
        bubble.setId(numBubbles);
        bubble.setX(200);
        bubble.setY(200);
        constraintLayout.addView(bubble);
/*
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        // connect start and end point of views, in this case top of child to top of parent.

        set.connect(bubble.getId(), ConstraintSet.TOP, bubble.getId(), ConstraintSet.TOP, 60);
        set.constrainWidth(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(bubble.getId(),ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);*/
        Log.v("What's up",bubble.getWidth()+","+bubble.getHeight());
        Log.v("What's up",bubble.getMeasuredWidth()+","+bubble.getMeasuredHeight());
    }

    public void startInputActivity(View view) {
        Intent inputActivityIntent = new Intent(this,InputActivity.class);
        startActivity(inputActivityIntent);
    }
}