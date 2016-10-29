package com.dancing_koala.speedmeter.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.services.LocationService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);
                getApplicationContext().startService(serviceIntent);
            }
        });

        findViewById(R.id.btn_stop_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);
                getApplicationContext().stopService(serviceIntent);
            }
        });


    }
}
