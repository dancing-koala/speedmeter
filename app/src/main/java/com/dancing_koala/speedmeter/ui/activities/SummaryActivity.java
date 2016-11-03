package com.dancing_koala.speedmeter.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.ui.fragments.SummaryFragment;

/**
 * Tracking session summary activity
 */
public class SummaryActivity extends BaseActivity {

    /**
     * Key to get the session id from extras
     */
    public static final String EXTRA_SESSION_ID = "com.dancing_koala.speedmeter.SummaryActivity.extra_session_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.summary_title);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new SummaryFragment())
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
