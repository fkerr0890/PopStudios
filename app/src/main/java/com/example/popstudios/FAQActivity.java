// FAQActivity.java opens a new page when the user clicks on the ? icon in the top left corner

package com.example.popstudios;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * An activity displaying FAQ information
 */
public class FAQActivity extends AppCompatActivity {

    //Sets up FAQ page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_activity);
    }
}