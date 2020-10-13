package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.popstudios.databinding.ActivityMainBinding;
import com.example.popstudios.databinding.BubbleBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, DeleteButtonDialog.DeleteButtonDialogListener {
//    private static final String TAG = "MyActivity";
    ActivityMainBinding main;
    FeedReaderDbHelper dbHelper;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    private List<Integer> listOfExpandedBubbles;
    private Map<Long,Goal> goalById;
    int deleteButtonId;
    View deleteView;

    FloatingActionButton firstBubble;
    Random random;
    View.OnLongClickListener listener = new View.OnLongClickListener() {
        // Gets ID and View from button that user LongClicks on and opens a Dialog window allowing user to choose whether to delete or not
        @Override
        public boolean onLongClick(View v) {
            int buttonId = v.getId();

            // set info of button to be deleted (view and ID)
            setDeleteButtonId(buttonId);
            setDeleteView(v);
            // System.out.println(buttonId);
            openDialog();
            return true;
        }

        // creates a new instance of the dialog class??
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

    // Called when the user clicks "yes" to delete in Dialog
    @Override
    public void onYesClicked() {
        // get info from longClick
        int deleteButtonId = getDeleteButtonId();
        View deleteButtonView = getDeleteView();

        // us ID to delete from Database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FeedReaderContract.FeedEntry._ID + " = " + (long)deleteButtonId;
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, null);

        // visually delete button by removing it from ViewGroup
        ViewGroup parentView = (ViewGroup) deleteButtonView.getParent();
        parentView.removeView(deleteButtonView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main.getRoot());
        listOfExpandedBubbles = new ArrayList<>();
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.deleteDatabase("FeedReader");
        SQLiteDatabase.deleteDatabase(new File("FeedReader.db"));
        dbHelper = new FeedReaderDbHelper(this);
        goalById = new HashMap<>();
        firstBubble = null;
        random = new Random();
        List<Goal> goals = dbHelper.getGoalsFromDb();
        for (Goal goal : goals) {
            goalById.put(goal.getGoalID(),goal);
            addBubble(goal);
        }
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

    private void addBubble(Goal goal) {
        FloatingActionButton bubble = BubbleBinding.inflate(getLayoutInflater()).getRoot();
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.width = goal.calculateRadius() * 2;
        params.height = goal.calculateRadius() * 2;
        bubble.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) bubble
                .setBackgroundTintList(ColorStateList.valueOf(goal.calculateColor()));
        bubble.setId((int) goal.getGoalID());
        bubble.setOnLongClickListener(listener);
        if (firstBubble == null) firstBubble = bubble;
        else {
            double angle = random.nextDouble() * 2 * Math.PI;
            bubble.setTranslationX(((float) bubble.getLayoutParams().width/2 +
                    (float) firstBubble.getLayoutParams().width/2) * (float) Math.cos(angle));
            bubble.setTranslationY(((float) bubble.getLayoutParams().width/2 +
                    (float) firstBubble.getLayoutParams().width/2) * (float) Math.sin(angle));
            System.out.println(firstBubble.getLayoutParams().width);
        }
        main.bubbleLayout.addView(bubble);
    }

    public void startInputActivity(View view) {
        Intent inputActivityIntent = new Intent(this,InputActivity.class);
        startActivity(inputActivityIntent);
    }

    public void startEditInputActivity(View view){
        Intent inputEditActivityIntent = new Intent(this,InputActivity.class);

        Goal goal = goalById.get((long)view.getId());
        assert goal != null;
        inputEditActivityIntent.putExtra("GOAL_ID", goal.getGoalID());

        String goalName = goal.getName();
        inputEditActivityIntent.putExtra("GOAL_NAME", goalName);

        int goalImportance = goal.goalImportance;
        inputEditActivityIntent.putExtra("GOAL_IMPORTANCE", goalImportance);

        int goalDifficulty = goal.goalDifficulty;
        inputEditActivityIntent.putExtra("GOAL_DIFFICULTY", goalDifficulty);

        String goalDescription = goal.getDescription();
        inputEditActivityIntent.putExtra("GOAL_DESCRIPTION", goalDescription);

        startActivity(inputEditActivityIntent);
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
            ImageButton button = bubbleInfo.getContentView().findViewById(R.id.editButton);
            if (button != null)
                button.setId(view.getId());
            TextView textView = bubbleInfo.getContentView().findViewById(R.id.textView);
            textView.setText(Objects.requireNonNull(goalById.get((long) view.getId())).getName());
            bubbleInfo.showAlignTop(view);
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