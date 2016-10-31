package com.dancing_koala.speedmeter.database.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dancing_koala.speedmeter.database.SpeedMeterDbHelper;
import com.dancing_koala.speedmeter.models.TrackingSession;

/**
 * Database access for the tracking_session table
 */
public class TrackingSessionAccess extends DatabaseAccess {
    /**
     * @see DatabaseAccess#DatabaseAccess(Context)
     */
    public TrackingSessionAccess(Context context) {
        super(context);
    }

    /**
     * Gets the last tracking session recorded
     *
     * @return The last tracking session model
     */
    public TrackingSession getLastTrackingSession() {
        String query = "SELECT "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_ID + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_START_TIME + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_END_TIME + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_DISTANCE + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_AVERAGE_SPEED
                + " FROM " + SpeedMeterDbHelper.TrackingSessionEntry.TABLE_NAME
                + " ORDER BY  " + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_START_TIME + " DESC "
                + " LIMIT 1 ;";

        TrackingSession session = null;
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            session = cursorToTrackingSession(c);
        }

        c.close();

        return session;
    }

    /**
     * Gets the last tracking session recorded
     *
     * @return The last tracking session model
     */
    public TrackingSession getTrackingSessionById(String id) {

        String query = "SELECT "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_ID + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_START_TIME + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_END_TIME + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_DISTANCE + ", "
                + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_AVERAGE_SPEED
                + " FROM " + SpeedMeterDbHelper.TrackingSessionEntry.TABLE_NAME
                + " WHERE " + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_ID + " = " + id + " "
                + " ORDER BY  " + SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_START_TIME + " DESC "
                + " LIMIT 1 ;";

        TrackingSession session = null;
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            session = cursorToTrackingSession(c);
        }

        c.close();

        return session;
    }

    /**
     * Saves a tracking session model into the database
     *
     * @param mSession Tracking session model to save
     */
    public void saveTrackingSession(TrackingSession mSession) {

        ContentValues values = new ContentValues();

        values.put(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_ID, mSession.getId());
        values.put(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_START_TIME, mSession.getStartTime());
        values.put(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_END_TIME, mSession.getEndTime());
        values.put(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_DISTANCE, mSession.getDistance());
        values.put(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_AVERAGE_SPEED, mSession.getAverageSpeed());

        db.replace(SpeedMeterDbHelper.TrackingSessionEntry.TABLE_NAME, null, values);
    }


    /**
     * Converts a cursor to tracking session model
     *
     * @param cursor Cursor to convert
     * @return The tracking session model
     */
    private TrackingSession cursorToTrackingSession(Cursor cursor) {

        TrackingSession trackingSession = new TrackingSession(
                cursor.getString(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_ID)),
                cursor.getLong(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_START_TIME)),
                cursor.getLong(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_END_TIME))
        );

        trackingSession.setDistance(cursor.getFloat(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_DISTANCE)));
        trackingSession.setAverageSpeed(cursor.getFloat(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSessionEntry.COLUMN_NAME_AVERAGE_SPEED)));

        return trackingSession;
    }


}
