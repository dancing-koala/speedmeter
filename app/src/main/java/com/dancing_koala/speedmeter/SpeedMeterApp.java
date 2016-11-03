package com.dancing_koala.speedmeter;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * SpeedMeter application class
 */
public class SpeedMeterApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // We add the digital font
        Typekit.getInstance().addCustom1(Typekit.createFromAsset(this, "fonts/digital-7/digital-7-regular.ttf"));
    }
}
