package com.dancing_koala.speedmeter.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.ui.fragments.SummaryFragment;

/**
 * Tracking session summary activity
 */
public class SummaryActivity extends AppCompatActivity {

    /**
     * Key to get the session id from extras
     */
    public static final String EXTRA_SESSION_ID = "com.dancing_koala.speedmeter.SummaryActivity.extra_session_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_fragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new SummaryFragment())
                .commitAllowingStateLoss();
    }
}
