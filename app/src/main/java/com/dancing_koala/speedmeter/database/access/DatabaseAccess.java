package com.dancing_koala.speedmeter.database.access;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dancing_koala.speedmeter.database.SpeedMeterDbHelper;

/**
 * Parent class for database access classes
 */
public abstract class DatabaseAccess {

    /**
     * Database use to perform queries
     */
    protected SQLiteDatabase db;
    /**
     * Helper used to open the database
     */
    protected SpeedMeterDbHelper dbHelper;

    /**
     * Constructor
     *
     * @param context Context of the instanciation.
     */
    public DatabaseAccess(Context context) {
        dbHelper = SpeedMeterDbHelper.getInstance(context);
    }

    /**
     * Opens the database in read-only mode
     */
    public void openToRead() {
        db = dbHelper.getReadableDatabase();
    }

    /**
     * Opens the database in read and write mode
     */
    public void openToWrite() {
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Close the database if opened.
     */
    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }
}
