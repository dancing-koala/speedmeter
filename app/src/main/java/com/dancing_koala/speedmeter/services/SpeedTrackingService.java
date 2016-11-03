package com.dancing_koala.speedmeter.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dancing_koala.speedmeter.helpers.Tracker;
import com.dancing_koala.speedmeter.models.TrackingSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class SpeedTrackingService extends Service implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Key to get the speed from extras
     */
    public static final String EXTRA_PROVIDER = "com.dancing_koala.speedmeter.SpeedTrackingService.extra_provider";
    /**
     * Key to get the speed from extras
     */
    public static final String EXTRA_SESSION_ID = "com.dancing_koala.speedmeter.SpeedTrackingService.extra_session_id";
    /**
     * Key to get the speed from extras
     */
    public static final String EXTRA_SPEED = "com.dancing_koala.speedmeter.SpeedTrackingService.extra_speed";
    /**
     * Action intent sent on to notify speed update
     */
    public static final String INTENT_ACTION_SPEED_UPDATE = "com.dancing_koala.speedmeter.SpeedTrackingService.speed_update";
    /**
     * Action intent sent on to notify that the device stopped moving
     */
    public static final String INTENT_ACTION_STOP_MOVING = "com.dancing_koala.speedmeter.SpeedTrackingService.stop_moving";

    /**
     * Max accuracy delta in meters
     */
    private static final int ACCURACY_DELTA_MAX = 200;
    /**
     * Maximum stop duration before ending the tracking
     */
    private static final int STOP_MOVING_DELAY = 1000 * 2;// 2 seconds
    /**
     * Fastest time interval for location updates
     */
    private static final int TIME_INTERVAL_FASTEST = 1000;// 1 second
    /**
     * Base time interval for location updates
     */
    private static final int TIME_INTERVAL_BASE = 1000 * 3;// 3 seconds
    /**
     * Maximum time delta between location updates.
     * Larger time delta means the update is too old.
     */
    private static final int TIME_DELTA_MAX = 1000 * 60 * 2;// 2 minutes
    /**
     * Minimum time delta between location updates.
     * Smaller time delta means the update is too recent.
     */
    private static final int TIME_DELTA_MIN = 600;// 600 millisecondes

    /**
     * Determines whether the trip started or not
     */
    private boolean tripStarted;
    /**
     * Determines whether the trip started or not
     */
    private boolean serviceAboutToEnd;
    /**
     * Google API client used for requesting location updates
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Handler used for launching delayed actions
     */
    private Handler handler;
    /**
     * Last location saved
     */
    private Location lastLocation;
    /**
     * Runnable to stop the service itself
     */
    private StopSelfRunnable stopSelfRunnable;

    /**
     * Constructor
     */
    public SpeedTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("devel", "SpeedTrackingService started.");

        tripStarted = false;
        handler = new Handler();
        stopSelfRunnable = new StopSelfRunnable();

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        Log.i("devel", "SpeedTrackingService stopped.");

        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();

        String sessionId = Tracker.getCurrentSessionId();

        // Ending and saving the current tracking session
        if (Tracker.isInitialized())
            Tracker.finalizeSession();

        // Notifying all receivers about the service ending
        Intent stopIntent = new Intent(INTENT_ACTION_STOP_MOVING);
        stopIntent.putExtra(EXTRA_SESSION_ID, sessionId);
        sendBroadcast(stopIntent);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Notification notif = new Notification.Builder(getBaseContext())
                .setContentTitle("Erreur")
                .setContentText("Échec de connexion à l'API Google.")
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, notif);
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("devel", "SpeedTrackingService.onLocationChanged ::  ");
        if (isBetterLocation(location, lastLocation)) {
            if (lastLocation != null) {
//                float speed = calculateSpeed(lastLocation, location);
                float speed = location.getSpeed();

                Tracker.addDistance(location.distanceTo(lastLocation));
                Tracker.addSpeed(speed);

                // The trip starts as soon as the speed is greater than 0
                if (speed > 0 && !tripStarted) {
                    tripStarted = true;
                }

                if (tripStarted) {
                    // If the speed goes below 1m / s, the movement is considered as stopping
                    if (speed < 1f && !serviceAboutToEnd) {
                        handler.postDelayed(stopSelfRunnable, STOP_MOVING_DELAY);
                        serviceAboutToEnd = true;
                    } else if (speed >= 1f && serviceAboutToEnd) {
                        handler.removeCallbacks(stopSelfRunnable);
                        serviceAboutToEnd = false;
                    }
                }

                // Notify receivers about speed updates
                Intent speedUpdateIntent = new Intent(INTENT_ACTION_SPEED_UPDATE);
                speedUpdateIntent.putExtra(EXTRA_SPEED, speed);
                speedUpdateIntent.putExtra(EXTRA_PROVIDER, location.getProvider() + " " + Math.round(location.getSpeed()));
                sendBroadcast(speedUpdateIntent);
            }

            lastLocation = location;
        }
    }

    /**
     * Initializes the GoogleApiClient
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Starts the location updates
     */
    private void startLocationUpdates() {
        // Building the location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(TIME_INTERVAL_BASE);
        locationRequest.setFastestInterval(TIME_INTERVAL_FASTEST);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Requesting location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        // Starting a new tracking session
        Tracker.initializeSession(this);
    }

    /**
     * Stop the location updates
     */
    private void stopLocationUpdates() {
        // Stopping location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Determines whether a location is better than an older one or not
     *
     * @param location            the newer location
     * @param currentBestLocation the older location
     * @return True if the newer location is the better one.
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) return true;

        long timeDelta = location.getTime() - currentBestLocation.getTime();

        // Determining the time delta based conditions
        boolean isTooOldOrTooRecent = timeDelta < -TIME_DELTA_MAX || timeDelta < TIME_DELTA_MIN;
        boolean isNewerEnough = timeDelta > TIME_DELTA_MAX;
        boolean isNewer = timeDelta > 0;

        if (isNewerEnough) {
            return true;
        } else if (isTooOldOrTooRecent) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());

        // Determining the accuracy delta based conditions
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > ACCURACY_DELTA_MAX;

        boolean sameProvider = areFromSameProvider(location.getProvider(), currentBestLocation.getProvider());

        if (isMoreAccurate || (isNewer && !isLessAccurate)) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && sameProvider) {
            return true;
        }

        return false;
    }

    /**
     * Determines whether providers given are the same or not
     *
     * @param provider1 first provider to use
     * @param provider2 second provider to use
     * @return True if both providers are the same
     */
    private boolean areFromSameProvider(String provider1, String provider2) {
        return (provider1 == null) ? provider2 == null : provider1.equals(provider2);
    }

    /**
     * Calculates speed in meters per second between 2 locations
     *
     * @param oldLocation Older location
     * @param newLocation Newer location
     * @return The speed in meters per second
     */
    private float calculateSpeed(Location oldLocation, Location newLocation) {
        // Time delta in milliseconds
        long timeDelta = newLocation.getTime() - oldLocation.getTime();
        // Distance between the locations in meters
        float distanceGap = newLocation.distanceTo(oldLocation);
        return Math.round(distanceGap / (timeDelta / 1000));
    }

    /**
     * Runnable that stops the service itself
     */
    private class StopSelfRunnable implements Runnable {
        @Override
        public void run() {
            stopSelf();
        }
    }
}
