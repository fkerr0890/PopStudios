package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class InputActivity extends AppCompatActivity {
    SeekBar importanceBar;
    SeekBar difficultyBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

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

    }

    public void startMainActivity(View view) {
        Intent mainActivityIntent = new Intent(this,MainActivity.class);
        startActivity(mainActivityIntent);
    }
}