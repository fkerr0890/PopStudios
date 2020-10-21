package com.example.popstudios;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.LinkedList;
import java.util.List;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    //Creates SQLite table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE + " SMALLINT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY + " SMALLINT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS + " SMALLINT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //Iterates through SQLite table, using row information to create goals, returning a list of goals.
    public List<Goal> getGoalsFromDb() {
        // Reads database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
                FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS
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
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String goalName = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_GOAL_NAME));
            int goalImportance = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_IMPORTANCE));
            int goalDifficulty = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DIFFICULTY));
            String goalDescription = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION));
            int goalStatus = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS));
            if (goalStatus == 0) {
                Goal newGoal = new Goal(itemId, goalName, goalImportance, goalDifficulty, goalDescription, goalStatus);
                goals.add(newGoal);
            }
        }
        cursor.close();
        return goals;
    }
}
