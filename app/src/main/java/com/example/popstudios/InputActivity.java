package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class InputActivity extends AppCompatActivity {
    SeekBar importanceBar, difficultyBar;
    EditText editGoal;
    Button inputAddBttn;

    String goalName;
    int goalImportanceNum,goalDifficultyNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        editGoal = (EditText)findViewById(R.id.editGoal);
        inputAddBttn = (Button)findViewById(R.id.inputAddBtn);
        importanceBar = (SeekBar)findViewById(R.id.importanceBar);
        difficultyBar = (SeekBar)findViewById(R.id.difficultyBar);

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
                    Toast.makeText(InputActivity.this, "Importance Rating: " + importanceBar.getProgress(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(InputActivity.this, "Difficulty Rating: " + difficultyBar.getProgress(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        inputAddBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalName = editGoal.getText().toString();
                goalImportanceNum = importanceBar.getProgress();
                goalDifficultyNum = difficultyBar.getProgress();

                if (goalName.isEmpty()){
                    Toast.makeText(InputActivity.this, "Please name your goal",Toast.LENGTH_SHORT).show();
                }
                FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(v.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME, goalName);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE, goalImportanceNum);
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY, goalDifficultyNum);


// Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
            }

        });

    }

    public void startMainActivity(View view) {
        Intent mainActivityIntent = new Intent(this,MainActivity.class);
        startActivity(mainActivityIntent);
    }
}