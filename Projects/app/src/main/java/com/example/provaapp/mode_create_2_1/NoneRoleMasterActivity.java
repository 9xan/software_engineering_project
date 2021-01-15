package com.example.provaapp.mode_create_2_1;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class NoneRoleMasterActivity extends AppCompatActivity {

    private Button stopRecording;
    private Long timeoutMs;
    private Intent receivedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.none_role_master_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        stopRecording = findViewById(R.id.stopNoneRecordingButton);
        stopRecording.setVisibility(View.INVISIBLE);
        stopRecording.setClickable(false);
        receivedIntent = getIntent();
        timeoutMs = receivedIntent.getLongExtra("timestamp", System.currentTimeMillis() + 5000);

        stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownStopRecording(System.currentTimeMillis() + 3000);       //This button is clickable and visible only by master(see -> countDownStarRecording)
            }
        });
        countDownStartRecording(timeoutMs);
    }

    private void countDownStartRecording(long timeToWait) {

        new CountDownTimer(timeToWait - System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                stopRecording.setVisibility(View.VISIBLE);
                stopRecording.setClickable(true);
            }
        }.start();
    }


    private void countDownStopRecording(long timeToWait) {

        Payload bytesPayload = Payload.fromBytes(("STOPRECORDING-" + Long.toString(timeToWait)).getBytes());
        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);

        new CountDownTimer(timeToWait - System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
