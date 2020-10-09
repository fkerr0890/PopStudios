package com.example.popstudios;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "goal";
        public static final String COLUMN_NAME_GOAL_NAME = "goal_name";
        public static final String COLUMN_NAME_IMPORTANCE = "goal_importance";
        public static final String COLUMN_NAME_DIFFICULTY = "goal_difficulty";
        public static final String COLUMN_NAME_DESCRIPTION = "goal_description";
    }
}
