package com.dancing_koala.speedmeter.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.ui.fragments.MainFragment;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Main activity of the application
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Fragment displayed by the main activity
     */
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_fragment);

        mainFragment = new MainFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mainFragment)
                .commitAllowingStateLoss();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mainFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
