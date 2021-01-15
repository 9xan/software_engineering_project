package com.example.provaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.provaapp.useful_classes.EzCam;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.example.provaapp.useful_classes.P2PWorkerNearby;
import com.example.provaapp.useful_classes.Permissions;
import com.example.provaapp.R;
import com.example.provaapp.useful_classes.UiSettings;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VideoRecordingActivity extends AppCompatActivity {

    private static EzCam myCamera;
    private Intent receivedIntent;
    private int receivedRequestCode;
    private String receivedOutputPath;
    private Long timeoutMs;
    private boolean isRecording = false;
    private Button stopRecordingButton;
    public CountDownTimer cm, cm2;
    private String role;
    private TextView elapsedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recording);
        Log.d("Creazione VideorAct->", "gg");
        receivedIntent = getIntent();
        timeoutMs = receivedIntent.getLongExtra("timestamp", System.currentTimeMillis() + 5000);
        receivedRequestCode = receivedIntent.getIntExtra("requestCode", EzCam.ACTION_ERROR);  // returns EzCamera.NO_ACTION if no request code was provided by the calling activity
        receivedOutputPath = receivedIntent.getStringExtra("outputPath");                     // returns null if no output path was requested by the calling activity
        role = receivedIntent.getStringExtra("role");                                         //Worker or Manager

        elapsedTime = findViewById(R.id.audioRecordingTime);

        if (role.compareTo("Worker") == 0) {                                                        //I'm a Worker, i'm setting my context in p2pWorkerNearby class
            P2PWorkerNearby.c = this;
        }

        Log.d(" Dati letti dall'intent", " timestamp " + timeoutMs + " requestCode " + receivedRequestCode + " path " + receivedOutputPath + " role " + role);

        try {
            myCamera = new EzCam(this);
            myCamera.startPreview(findViewById(R.id.previewView));
            stopRecordingButton = findViewById(R.id.stopRecordingButton);
            stopRecordingButton.setVisibility(View.INVISIBLE);
            stopRecordingButton.setClickable(false);
            stopRecordingButton.setOnClickListener(
                    (v) -> {
                        countDownStopRecording(System.currentTimeMillis() + 3000);       //This button is clickable and visible only by master(see -> countDownStarRecording)
                    }
            );

        } catch (Permissions.PermissionDeniedException e) {
            Toast.makeText(this, "Unable to access camera", Toast.LENGTH_LONG).show();
            Log.i("Permissions denied", e.getMessage());
            finish();
        }

        countDownStartRecording(timeoutMs);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UiSettings.hideSystemUI(this);
        }
    }

    private void startTiming(long startTime) {

        final Timer myTimer = new Timer();

        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                String time = String.format(Locale.getDefault(), "%02d min: %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) % 60);
                elapsedTime.setText(time);
            }
        }, 0, 100);
    }

    private void countDownStopRecording(long timeToWait) {

        Log.d("sto fermando", "gg");
        stopRecordingButton.setClickable(false);
        Payload bytesPayload = Payload.fromBytes(("STOPRECORDING-" + timeToWait).getBytes());
        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);

        //Toast.makeText(getApplicationContext(), "sono prima del countdown ma ho inviato", Toast.LENGTH_LONG).show();

        // myCamera.stopRecording();

        new CountDownTimer(timeToWait - System.currentTimeMillis(), 200) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {
                    Toast.makeText(getApplicationContext(), "fermo la registrazione", Toast.LENGTH_LONG).show();
                    Log.d("mo me fermo", "mo");
                    vRecordingLogic();
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        }.start();
    }

    private void countDownStartRecording(long timeToWait) {

        new CountDownTimer(timeToWait - System.currentTimeMillis(), 200) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                Log.d("PRE START RECORDING", "STO PER CHIAMARE LA START RECORDING");

                try {
                    Log.d("PRE START RECORDING", "STO PER CHIAMARE LA START RECORDING - 1");
                    vRecordingLogic();
                    startTiming(System.currentTimeMillis());
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
                if (role.compareTo("Manager") == 0) {//todo, se è master il countdown dura di piu  perche non chiama la ready to start activity
                    stopRecordingButton.setVisibility(View.VISIBLE);
                    stopRecordingButton.setClickable(true);
                }

            }
        }.start();
    }

/*
    public static void recordingLogic(VideoRecordingActivity c) {
        c.recordingLogic();
        //myCamera.stopRecording();
        //todo: lo start activity per l'activity successiva va fatto al posto della finish
    }
*/

    public void vRecordingLogic() {
        // TODO: here the start/stop logic must be triggered by a message from the p2p master instead of a user tapping
        if (!isRecording)
            myCamera.startRecording(
                    (receivedOutputPath == null) ? null : Uri.fromFile(new File(receivedOutputPath)),
                    receivedRequestCode,
                    (savedVideoUri) -> {
                        // called after myCamera.stopRecording() and when the file containing the video is available
                        Intent intent = new Intent();
                        intent.setData(savedVideoUri);
                        if (savedVideoUri != null) {
                            setResult(Activity.RESULT_OK, intent);
                        } else {
                            setResult(Activity.RESULT_CANCELED, new Intent());
                        }
                        finish();   // close activity and give result to caller
                    }
            );
        else
            myCamera.stopRecording();
        isRecording = !isRecording;
    }


    @Override
    protected void onDestroy() {
        myCamera = null;
        super.onDestroy();
    }
}