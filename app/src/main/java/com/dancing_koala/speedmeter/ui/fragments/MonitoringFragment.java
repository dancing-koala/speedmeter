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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.helpers.Formatter;
import com.dancing_koala.speedmeter.helpers.PermissionHelper;
import com.dancing_koala.speedmeter.services.SpeedTrackingService;
import com.dancing_koala.speedmeter.ui.activities.SummaryActivity;
import com.dancing_koala.speedmeter.ui.views.SpeedMeterView;

/**
 * Fragment dedicated to instant speed monitoring
 */
public class MonitoringFragment extends Fragment {

    /**
     * ID used when requesting location permission
     */
    public static final int LOCATION_PERMISSION_REQUEST_ID = 0x56;

    /**
     * Button dedicated to tracking enabling and disabling
     */
    private FloatingActionButton mToggleTrackingBtn;
    /**
     * Simple object to be used as a tag
     */
    private Object mTagHolder;
    /**
     * Visual car-style speed indicator
     */
    private SpeedMeterView mSpeedMeterView;
    /**
     * Receiver dedicated to speed tracking (speed update and stop moving)
     */
    private SpeedTrackingBroadcastReceiver mReceiver;
    /**
     * Textual speed indicator
     */
    private TextView mSpeedTextView;
    /**
     * Root view of the fragment
     */
    private View mRootView;

    /**
     * Constructor
     */
    public MonitoringFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_monitoring, container, false);
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

        mTagHolder = new Object();

        mToggleTrackingBtn = (FloatingActionButton) mRootView.findViewById(R.id.fba_toggle_tracking);

        mToggleTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionHelper.hasLocationPermission(getActivity())) {
                    Intent serviceIntent = new Intent(getActivity().getApplicationContext(), SpeedTrackingService.class);
                    if (v.getTag() == null) {
                        getActivity().getApplicationContext().startService(serviceIntent);
                        mSpeedTextView.setText(R.string.enabling);
                        v.setTag(mTagHolder);
                        mToggleTrackingBtn.setImageResource(R.drawable.icon_stop);
                    } else {
                        getActivity().getApplicationContext().stopService(serviceIntent);
                        v.setTag(null);
                        mToggleTrackingBtn.setImageResource(R.drawable.icon_play);
                    }
                } else {
                    showLocationPermissionExplanation();
                }
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

        builder.setTitle(R.string.location_explanation_title)
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

            // The location permission was granted so we can start the tracking service
            Intent serviceIntent = new Intent(getActivity().getApplicationContext(), SpeedTrackingService.class);
            getActivity().getApplicationContext().startService(serviceIntent);
            mSpeedTextView.setText(R.string.enabling);
            mToggleTrackingBtn.setTag(mTagHolder);
            mToggleTrackingBtn.setImageResource(R.drawable.icon_stop);
        }
    }

    /**
     * BroadcastReceiver dedicated to speed tracking
     */
    private class SpeedTrackingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case SpeedTrackingService.INTENT_ACTION_SPEED_UPDATE:
                    // We update the textual and the graphical speed indicators
                    float speed = intent.getFloatExtra(SpeedTrackingService.EXTRA_SPEED, 0f);
                    mSpeedTextView.setText(Formatter.getKilometersPerHour(speed));
                    mSpeedMeterView.updateSpeed(speed * 3600 / 1000);
                    break;

                case SpeedTrackingService.INTENT_ACTION_STOP_MOVING:
                    // We reset the speed indicators
                    mSpeedTextView.setText(R.string.stop);
                    mSpeedMeterView.updateSpeed(0f);

                    // If the toggle button has not been changed, we reset it too
                    if (mToggleTrackingBtn.getTag() != null) {
                        mToggleTrackingBtn.setTag(null);
                        mToggleTrackingBtn.setImageResource(R.drawable.icon_play);
                    }

                    // We show the summary of the last session
                    Intent summmaryIntent = new Intent(getActivity(), SummaryActivity.class);
                    summmaryIntent.putExtra(SummaryActivity.EXTRA_SESSION_ID, intent.getStringExtra(SpeedTrackingService.EXTRA_SESSION_ID));
                    startActivity(summmaryIntent);
                    break;
            }
        }
    }
}
