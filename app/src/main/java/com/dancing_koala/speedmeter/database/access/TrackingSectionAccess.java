package com.dancing_koala.speedmeter.database.access;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dancing_koala.speedmeter.database.SpeedMeterDbHelper;
import com.dancing_koala.speedmeter.models.TrackingSection;

/**
 * Database access for the tracking_section table
 */
public class TrackingSectionAccess extends DatabaseAccess {
    /**
     * @see DatabaseAccess#DatabaseAccess(Context)
     */
    public TrackingSectionAccess(Context context) {
        super(context);
    }

    public TrackingSection getLastTrackingSection() {
        String query = "SELECT "
                + SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_ID + ", "
                + SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_START_TIME + ", "
                + SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_END_TIME + ", "
                + SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_DISTANCE + ", "
                + SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_AVERAGE_SPEED
                + " FROM " + SpeedMeterDbHelper.TrackingSectionEntry.TABLE_NAME
                + " ORDER BY  " + SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_START_TIME + " DESC "
                + " LIMIT 1 ;";

        Log.d("devel", "TrackingSectionAccess.getLastTrackingSection ::  " + query);

        TrackingSection section = null;
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            section = cursorToTrackingSection(c);
        }

        c.close();

        return section;
    }


    private TrackingSection cursorToTrackingSection(Cursor cursor) {

        TrackingSection trackingSection = new TrackingSection(
                cursor.getString(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_ID)),
                cursor.getLong(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_START_TIME)),
                cursor.getLong(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_END_TIME))
        );

        trackingSection.setDistance(cursor.getFloat(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_DISTANCE)));
        trackingSection.setAverageSpeed(cursor.getFloat(cursor.getColumnIndex(SpeedMeterDbHelper.TrackingSectionEntry.COLUMN_NAME_AVERAGE_SPEED)));

        return trackingSection;
    }
}
