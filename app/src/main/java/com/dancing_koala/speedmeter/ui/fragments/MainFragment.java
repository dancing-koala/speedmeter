package com.dancing_koala.speedmeter.ui.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.database.access.TrackingSessionAccess;
import com.dancing_koala.speedmeter.helpers.PermissionHelper;
import com.dancing_koala.speedmeter.helpers.Tracker;
import com.dancing_koala.speedmeter.models.TrackingSession;
import com.dancing_koala.speedmeter.services.SpeedTrackingService;
import com.dancing_koala.speedmeter.ui.views.SpeedMeterView;

import java.util.Locale;

public class MainFragment extends Fragment {

    /**
     * ID used when requesting location permission
     */
    public static final int LOCATION_PERMISSION_REQUEST_ID = 0x56;

    /**
     * Visual car-style speed indicator
     */
    private SpeedMeterView speedMeterView;
    /**
     * Receiver dedicated to speed tracking (speed update and stop moving)
     */
    private SpeedTrackingBroadcastReceiver receiver;
    private TextView speedTextView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (receiver != null) {
            getActivity().registerReceiver(receiver, new IntentFilter(SpeedTrackingService.INTENT_ACTION_SPEED_UPDATE));
            getActivity().registerReceiver(receiver, new IntentFilter(SpeedTrackingService.INTENT_ACTION_STOP_MOVING));
        }
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }

        super.onDestroy();
    }

    /**
     * Fragment initialization
     */
    private void init() {

        rootView.findViewById(R.id.btn_start_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionHelper.hasLocationPermission(getActivity())) {
                    Intent serviceIntent = new Intent(getActivity().getApplicationContext(), SpeedTrackingService.class);
                    getActivity().getApplicationContext().startService(serviceIntent);
                } else {
                    showLocationPermissionExplanation();
                }
            }
        });

        rootView.findViewById(R.id.btn_stop_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity().getApplicationContext(), SpeedTrackingService.class);
                getActivity().getApplicationContext().stopService(serviceIntent);
            }
        });

        speedTextView = (TextView) rootView.findViewById(R.id.txtv_speed);
        speedMeterView = (SpeedMeterView) rootView.findViewById(R.id.smv_speedmeterview);
        receiver = new SpeedTrackingBroadcastReceiver();
    }

    /**
     * Shows an alert dialog explaining to the user why the location permission is
     * needed by the app
     */
    private void showLocationPermissionExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setTitle(R.string.location_explanation_title)
                .setMessage(R.string.location_explanation_msg)
                .setNeutralButton(R.string.understood, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionHelper.requestLocationPermission(getActivity(), LOCATION_PERMISSION_REQUEST_ID);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_ID
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Intent serviceIntent = new Intent(getActivity().getApplicationContext(), SpeedTrackingService.class);
            getActivity().getApplicationContext().startService(serviceIntent);

        }
    }

    /**
     * BroadcastReceiver dedicated to speed tracking
     */
    private class SpeedTrackingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("devel", "SpeedTrackingBroadcastReceiver.onReceive ::  " + intent.getAction());
            String action = intent.getAction();

            switch (action) {
                case SpeedTrackingService.INTENT_ACTION_SPEED_UPDATE:
                    float speed = intent.getFloatExtra(SpeedTrackingService.EXTRA_SPEED, 0f) * 3600 / 1000;
                    speedTextView.setText(String.format(Locale.FRANCE, "%.01f", speed));
                    speedMeterView.setSpeed(speed);
                    break;

                case SpeedTrackingService.INTENT_ACTION_STOP_MOVING:
                    speedTextView.setText("Stop");
                    break;
            }
        }
    }
}
