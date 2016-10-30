package com.dancing_koala.speedmeter.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class SpeedMeterDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "speed_meter.db";

    private static final String COMMA_SEPARATOR = ", ";
    private static final String TYPE_INTEGER = " INTEGER ";
    private static final String TYPE_TEXT = " TEXT ";
    private static final String TYPE_REAL = " REAL ";

    private static SpeedMeterDbHelper mInstance = null;


    private SpeedMeterDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SpeedMeterDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SpeedMeterDbHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateEntriesSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static String getCreateEntriesSQL() {
        String sql = "CREATE TABLE " + TrackingSessionEntry.TABLE_NAME +
                " (" +
                TrackingSessionEntry.COLUMN_NAME_ID + TYPE_TEXT + " PRIMARY KEY" + COMMA_SEPARATOR +
                TrackingSessionEntry.COLUMN_NAME_START_TIME + TYPE_INTEGER + COMMA_SEPARATOR +
                TrackingSessionEntry.COLUMN_NAME_END_TIME + TYPE_INTEGER + COMMA_SEPARATOR +
                TrackingSessionEntry.COLUMN_NAME_DISTANCE + TYPE_REAL + COMMA_SEPARATOR +
                TrackingSessionEntry.COLUMN_NAME_AVERAGE_SPEED + TYPE_REAL +
                " );";


        Log.d("devel", "SpeedMeterDbHelper.getCreateEntriesSQL ::  " + sql);

        return sql;
    }

    public static String getDeleteEntriesSQL() {
        String sql = "DROP TABLE IF EXISTS " + TrackingSessionEntry.TABLE_NAME + ";";

        Log.d("devel", "SpeedMeterDbHelper.getDeleteEntriesSQL ::  " + sql);

        return sql;
    }

    /**
     * Tracking session table schema
     */
    public static class TrackingSessionEntry {
        public static final String TABLE_NAME = "tracking_session";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_AVERAGE_SPEED = "average_speed";
    }
}
