package com.dancing_koala.speedmeter.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.ui.fragments.MainFragment;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_fragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new MainFragment())
                .commitAllowingStateLoss();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
