package com.dancing_koala.speedmeter.ui.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.helpers.PermissionHelper;
import com.dancing_koala.speedmeter.services.SpeedTrackingService;
import com.dancing_koala.speedmeter.ui.activities.SummaryActivity;
import com.dancing_koala.speedmeter.ui.views.SpeedMeterView;

import java.util.Locale;
import java.util.Random;

public class MainFragment extends Fragment {

    /**
     * ID used when requesting location permission
     */
    public static final int LOCATION_PERMISSION_REQUEST_ID = 0x56;

    // Testing dedicated fields
    private boolean mRunSpeedTest;
    private Handler mTestHandler;
    private Runnable mTestSpeedRunnable;


    /**
     * Visual car-style speed indicator
     */
    private SpeedMeterView mSpeedMeterView;
    /**
     * Receiver dedicated to speed tracking (speed update and stop moving)
     */
    private SpeedTrackingBroadcastReceiver mReceiver;
    private TextView mSpeedTextView;
    /**
     * Root view of the fragment
     */
    private View mRootView;

    /**
     * Constructor
     */
    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReceiver != null) {
            getActivity().registerReceiver(mReceiver, new IntentFilter(SpeedTrackingService.INTENT_ACTION_SPEED_UPDATE));
            getActivity().registerReceiver(mReceiver, new IntentFilter(SpeedTrackingService.INTENT_ACTION_STOP_MOVING));
        }
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }

        super.onDestroy();
    }

    /**
     * Fragment initialization
     */
    private void init() {

        mRunSpeedTest = false;

        mTestHandler = new Handler();

        mTestSpeedRunnable = new Runnable() {

            Random rand = new Random();

            @Override
            public void run() {
                if (mRunSpeedTest) {
                    int speed = rand.nextInt(100);
                    mSpeedTextView.setText("" + speed);
                    mSpeedMeterView.setSpeed(speed);
                    mTestHandler.postDelayed(this, 1000);
                } else {
                    mSpeedTextView.setText("Stop");
                    mSpeedMeterView.setSpeed(0);
                }
            }
        };

        mRootView.findViewById(R.id.btn_start_speed_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRunSpeedTest) {
                    mRunSpeedTest = true;
                    mTestHandler.post(mTestSpeedRunnable);
                }
            }
        });

        mRootView.findViewById(R.id.btn_stop_speed_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunSpeedTest = false;
            }
        });

        mRootView.findViewById(R.id.btn_start_tracking).setOnClickListener(new View.OnClickListener() {
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

        mRootView.findViewById(R.id.btn_stop_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity().getApplicationContext(), SpeedTrackingService.class);
                getActivity().getApplicationContext().stopService(serviceIntent);
            }
        });

        mSpeedTextView = (TextView) mRootView.findViewById(R.id.txtv_speed);
        mSpeedMeterView = (SpeedMeterView) mRootView.findViewById(R.id.smv_speedmeterview);
        mReceiver = new SpeedTrackingBroadcastReceiver();
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
            final String action = intent.getAction();
            Log.d("devel", "SpeedTrackingBroadcastReceiver.onReceive ::  " + action);

            switch (action) {
                case SpeedTrackingService.INTENT_ACTION_SPEED_UPDATE:
                    float speed = intent.getFloatExtra(SpeedTrackingService.EXTRA_SPEED, 0f) * 3600 / 1000;
                    mSpeedTextView.setText(String.format(Locale.FRANCE, "%.01f", speed));
                    mSpeedMeterView.setSpeed(speed);
                    break;

                case SpeedTrackingService.INTENT_ACTION_STOP_MOVING:
                    mSpeedTextView.setText("Stop");
                    mSpeedMeterView.setSpeed(0f);

                    Intent summmaryIntent = new Intent(getActivity(), SummaryActivity.class);
                    summmaryIntent.putExtra(SummaryActivity.EXTRA_SESSION_ID, intent.getStringExtra(SpeedTrackingService.EXTRA_SESSION_ID));
//                    startActivity(summmaryIntent);
                    break;
            }
        }
    }
}
