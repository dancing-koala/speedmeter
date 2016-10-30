package com.dancing_koala.speedmeter.helpers;


import android.content.Context;

import com.dancing_koala.speedmeter.database.access.TrackingSessionAccess;
import com.dancing_koala.speedmeter.models.TrackingSession;

import java.util.ArrayList;

/**
 * Class dedicated to the speed tracking
 */
public class Tracker {

    private static ArrayList<Float> mDistanceRecords = new ArrayList<>();
    private static ArrayList<Float> mSpeedRecords = new ArrayList<>();
    private static TrackingSession mSession = null;
    private static TrackingSessionAccess mSessionAccess = null;

    public static void initializeSession(Context context) {
        if (mSessionAccess == null) {
            mSessionAccess = new TrackingSessionAccess(context);
        }

        if (mSession != null) {
            finalizeSession();
        }

        mSession = new TrackingSession(System.currentTimeMillis());
    }

    public static boolean isInitialized() {
        return mSession != null;
    }

    public static void addSpeed(float speed) {
        mSpeedRecords.add(speed);
    }

    private static float getAverageSpeed() {
        float totalSpeed = 0f;

        if (mSpeedRecords.size() == 0) return totalSpeed;

        for (float speed : mSpeedRecords)
            totalSpeed += speed;

        return totalSpeed / mSpeedRecords.size();
    }

    public static void addDistance(float distance) {
        mDistanceRecords.add(distance);
    }

    private static float getTotalDistance() {
        float totalDistance = 0f;

        for (float distance : mDistanceRecords)
            totalDistance += distance;

        return totalDistance;
    }

    public static void finalizeSession() {
        final long endTime = System.currentTimeMillis();
        mSession.setEndTime(endTime);
        mSession.setAverageSpeed(getAverageSpeed());
        mSession.setDistance(getTotalDistance());

        mSessionAccess.openToWrite();
        mSessionAccess.saveTrackingSession(mSession);
        mSessionAccess.close();

        mSession = null;
        mSpeedRecords.clear();
        mDistanceRecords.clear();
    }

}
