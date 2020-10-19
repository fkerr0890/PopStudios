package com.example.popstudios;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InputActivity extends AppCompatActivity {
    SeekBar importanceBar, difficultyBar;
    EditText editGoal, editDescription;
    Button inputAddBttn;
    TextView inputName;

    String goalName, goalDescriptionStr;
    int goalImportanceNum,goalDifficultyNum;

    FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        inputName = findViewById(R.id.inputActivityTitle);
        inputName.setText(R.string.create_goal);
        inputName = findViewById(R.id.inputActivityTitle);
        editGoal = findViewById(R.id.editGoal);
        inputAddBttn = findViewById(R.id.inputAddBtn);
        importanceBar = findViewById(R.id.importanceBar);
        difficultyBar = findViewById(R.id.difficultyBar);
        editDescription = findViewById(R.id.editDescription);

        dbHelper = new FeedReaderDbHelper(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("GOAL_ID")){
            final long goalID = intent.getLongExtra("GOAL_ID",0);
            String goalName = intent.getStringExtra("GOAL_NAME");
            int goalImportance = intent.getIntExtra("GOAL_IMPORTANCE",0);
            int goalDifficulty = intent.getIntExtra("GOAL_DIFFICULTY",0);
            String description = intent.getStringExtra("GOAL_DESCRIPTION");
            inputName.setText(R.string.edit_goal);
            editGoal.setText(goalName);
            importanceBar.setProgress(goalImportance);
            difficultyBar.setProgress(goalDifficulty);
            editDescription.setText(description);
            inputAddBttn.setText(R.string.save_button_text);

            inputAddBttn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    updateGoal(goalID);
                    Intent mainActivityIntent = new Intent(InputActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                }
            });
        }

        if (importanceBar != null) {
            importanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar importanceBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar importanceBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar importanceBar) {
                }
            });
        }
        if (difficultyBar != null) {
            difficultyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar difficultyBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar difficultyBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar difficultyBar) {
                }
            });
        }

    }

    public void startMainActivity(View view) {
        if (writeSql()) {
/*            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);*/
            finish();
        }
    }


    private boolean writeSql() {
        goalName = editGoal.getText().toString();
        goalImportanceNum = importanceBar.getProgress();
        goalDifficultyNum = difficultyBar.getProgress();
        goalDescriptionStr = editDescription.getText().toString();

        if (goalName.isEmpty()){
            Toast.makeText(InputActivity.this, "Please name your goal",Toast.LENGTH_SHORT).show();
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME, goalName);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE, goalImportanceNum);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY, goalDifficultyNum);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, goalDescriptionStr);
        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        return true;
    }

    public void updateGoal(Long goalID){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        goalName = editGoal.getText().toString();
        goalImportanceNum = importanceBar.getProgress();
        goalDifficultyNum = difficultyBar.getProgress();
        goalDescriptionStr = editDescription.getText().toString();

        ContentValues newValues = new ContentValues();
        newValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME, goalName);
        newValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE, goalImportanceNum);
        newValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY, goalDifficultyNum);
        newValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, goalDescriptionStr);
        db.update(FeedReaderContract.FeedEntry.TABLE_NAME, newValues,
                FeedReaderContract.FeedEntry._ID + " = " + goalID,null);
    }

}