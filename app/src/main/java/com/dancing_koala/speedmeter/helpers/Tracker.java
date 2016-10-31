package com.dancing_koala.speedmeter.helpers;


import android.content.Context;

import com.dancing_koala.speedmeter.database.access.TrackingSessionAccess;
import com.dancing_koala.speedmeter.models.TrackingSession;

import java.util.ArrayList;

/**
 * Class dedicated to the speed tracking
 */
public class Tracker {

    /**
     * List of all recorded distances during session.
     */
    private static ArrayList<Float> mDistanceRecords = new ArrayList<>();
    /**
     * List of all recorded speeds during session.
     */
    private static ArrayList<Float> mSpeedRecords = new ArrayList<>();
    /**
     * Current tracking session
     */
    private static TrackingSession mSession = null;
    /**
     * Database access for tracking session
     */
    private static TrackingSessionAccess mSessionAccess = null;

    /**
     * Creates a new session
     *
     * @param context Context used to instantiate the database access
     */
    public static void initializeSession(Context context) {
        if (mSessionAccess == null) {
            // Instantiating the database access
            mSessionAccess = new TrackingSessionAccess(context);
        }

        if (mSession != null) {
            // If a session has been started but not ended, we finalize it.
            finalizeSession();
        }

        // Creating new session
        mSession = new TrackingSession(System.currentTimeMillis());
    }

    /**
     * Determines whether a session has already been started or not
     *
     * @return True if a session is in progress
     */
    public static boolean isInitialized() {
        return mSession != null;
    }

    /**
     * Adds a speed to the recorded speeds for the session
     *
     * @param speed Speed to add to records
     */
    public static void addSpeed(float speed) {
        mSpeedRecords.add(speed);
    }

    /**
     * Calculates the average speed of the session in progress
     *
     * @return The average speed for the current session
     */
    private static float getAverageSpeed() {
        float totalSpeed = 0f;

        if (mSpeedRecords.size() == 0) return totalSpeed;

        for (float speed : mSpeedRecords)
            totalSpeed += speed;

        return totalSpeed / mSpeedRecords.size();
    }

    /**
     * Adds a distance to the recorded distances for the session
     *
     * @param distance Distance to add to records
     */
    public static void addDistance(float distance) {
        mDistanceRecords.add(distance);
    }

    /**
     * Calculates the total distance traveled during the session
     *
     * @return The total distance traveled
     */
    private static float getTotalDistance() {
        float totalDistance = 0f;

        for (float distance : mDistanceRecords)
            totalDistance += distance;

        return totalDistance;
    }

    /**
     * Gets the id of the tracking session in progress
     *
     * @return The id of the current tracking session
     */
    public static String getCurrentSessionId() {
        return (mSession == null) ? null : mSession.getId();
    }

    /**
     * Ends the tracking session in progress and saves it to the database
     */
    public static void finalizeSession() {
        // Finalizing the session in progress
        final long endTime = System.currentTimeMillis();
        mSession.setEndTime(endTime);
        mSession.setAverageSpeed(getAverageSpeed());
        mSession.setDistance(getTotalDistance());

        // Writing the session to the database
        mSessionAccess.openToWrite();
        mSessionAccess.saveTrackingSession(mSession);
        mSessionAccess.close();

        // Reseting the session fields
        mSession = null;
        mSpeedRecords.clear();
        mDistanceRecords.clear();
    }

}
