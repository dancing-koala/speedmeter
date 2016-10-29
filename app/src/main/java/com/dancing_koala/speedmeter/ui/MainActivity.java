package com.dancing_koala.speedmeter.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dancing_koala.speedmeter.R;
import com.dancing_koala.speedmeter.services.SpeedTrackingService;

public class MainActivity extends AppCompatActivity {

    private TextView speedTextView;
    private SpeedTrackingBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), SpeedTrackingService.class);
                getApplicationContext().startService(serviceIntent);
            }
        });

        findViewById(R.id.btn_stop_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), SpeedTrackingService.class);
                getApplicationContext().stopService(serviceIntent);
            }
        });

        speedTextView = (TextView) findViewById(R.id.txtv_speed);

        receiver = new SpeedTrackingBroadcastReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver != null) {
            registerReceiver(receiver, new IntentFilter(SpeedTrackingService.INTENT_ACTION_SPEED_UPDATE));
            registerReceiver(receiver, new IntentFilter(SpeedTrackingService.INTENT_ACTION_STOP_MOVING));
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null)
            unregisterReceiver(receiver);

        super.onDestroy();
    }

    private class SpeedTrackingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("devel", "SpeedTrackingBroadcastReceiver.onReceive ::  " + intent.getAction());
            String action = intent.getAction();

            switch (action) {
                case SpeedTrackingService.INTENT_ACTION_SPEED_UPDATE:
                    speedTextView.setText("" + intent.getFloatExtra(SpeedTrackingService.EXTRA_SPEED, 0f));
                    break;

                case SpeedTrackingService.INTENT_ACTION_STOP_MOVING:
                    speedTextView.setText("Stop");
                    break;
            }
        }
    }
}
