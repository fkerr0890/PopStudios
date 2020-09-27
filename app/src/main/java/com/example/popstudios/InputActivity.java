package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class InputActivity extends AppCompatActivity {
    SeekBar importanceBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        importanceBar = (SeekBar)findViewById(R.id.importanceBar);
        if (importanceBar != null) {
            importanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar importanceBar, int progress, boolean fromUser) {
                    // Write code to perform some action when progress is changed.
                }

                @Override
                public void onStartTrackingTouch(SeekBar importanceBar) {
                    // Write code to perform some action when touch is started.
                }

                @Override
                public void onStopTrackingTouch(SeekBar importanceBar) {
                    // Write code to perform some action when touch is stopped.
                    Toast.makeText(InputActivity.this, "Current Rating: " + importanceBar.getProgress(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}