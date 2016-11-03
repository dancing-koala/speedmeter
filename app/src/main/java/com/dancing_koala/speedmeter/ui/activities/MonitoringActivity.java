package com.dancing_koala.speedmeter.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.ui.fragments.MonitoringFragment;

/**
 * Monitoring activity of the application
 */
public class MonitoringActivity extends BaseActivity {
    /**
     * Fragment displayed by the monitoring activity
     */
    private MonitoringFragment monitoringFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        monitoringFragment = new MonitoringFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, monitoringFragment)
                .commitAllowingStateLoss();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // All permissions result are passed directly to the monitoring fragment
        monitoringFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
