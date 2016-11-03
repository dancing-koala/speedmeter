package com.dancing_koala.speedmeter.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dancing_koala.speedmeter.R;
import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Base activity to use Typekit
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
