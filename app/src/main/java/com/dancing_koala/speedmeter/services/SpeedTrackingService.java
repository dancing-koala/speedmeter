package com.dancing_koala.speedmeter.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class SpeedTrackingService extends Service implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     *
     */
    public static final String EXTRA_SPEED = "com.dancing_koala.speedmeter.extra_speed";
    /**
     * Action intent sent on to notify speed update
     */
    public static final String INTENT_ACTION_SPEED_UPDATE = "com.dancing_koala.speedmeter.speed_update";
    /**
     * Action intent sent on to notify that the device stopped moving
     */
    public static final String INTENT_ACTION_STOP_MOVING = "com.dancing_koala.speedmeter.stop_moving";

    /**
     * Max accuracy delta in meters
     */
    private static final int ACCURACY_DELTA_MAX = 200;
    /**
     * Fastest time interval for location updates (2 seconds)
     */
    private static final int TIME_INTERVAL_FASTEST = 1000 * 1;
    /**
     * Base time interval for location updates (5 seconds)
     */
    private static final int TIME_INTERVAL_BASE = 1000 * 3;
    /**
     * Max time delta between location updates (2 minutes).
     * Larger time delta means the update is too old.
     */
    private static final int TIME_DELTA_MAX = 1000 * 60 * 2;

    /**
     * Google API client used for requesting location updates
     */
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;

    /**
     * Constructor
     */
    public SpeedTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("devel", "SpeedTrackingService started.");

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        Log.i("devel", "SpeedTrackingService stopped.");

        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();

        Intent stopMovingIntent = new Intent(INTENT_ACTION_STOP_MOVING);
        sendBroadcast(stopMovingIntent);

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
        if (isBetterLocation(location, lastLocation)) {
            if (lastLocation != null) {
                float speed = calculateSpeed(lastLocation, location) * 3600 / 1000;
                Intent speedUpdateIntent = new Intent(INTENT_ACTION_SPEED_UPDATE);
                speedUpdateIntent.putExtra(EXTRA_SPEED, speed);
                sendBroadcast(speedUpdateIntent);
            }

            lastLocation = location;
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(TIME_INTERVAL_BASE);
        locationRequest.setFastestInterval(TIME_INTERVAL_FASTEST);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) return true;

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isNewerEnough = timeDelta > TIME_DELTA_MAX;
        boolean isTooOld = timeDelta < -TIME_DELTA_MAX;
        boolean isNewer = timeDelta > 0;

        if (isNewerEnough) {
            return true;
        } else if (isTooOld) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());

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
        long timeDelta = newLocation.getTime() - oldLocation.getTime();
        float distanceGap = newLocation.distanceTo(oldLocation);
        return Math.round(distanceGap / (timeDelta / 1000));
    }
}
