package com.example.provaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.provaapp.useful_classes.EzCam;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.example.provaapp.useful_classes.P2PWorkerNearby;
import com.example.provaapp.useful_classes.Permissions;
import com.example.provaapp.R;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

import java.io.File;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recording);
        Log.d("Creazione VideorAct->", "gg");
        receivedIntent = getIntent();
        timeoutMs = receivedIntent.getLongExtra("timestamp", 5000);
        receivedRequestCode = receivedIntent.getIntExtra("requestCode", EzCam.ACTION_ERROR);  // returns EzCamera.NO_ACTION if no request code was provided by the calling activity
        receivedOutputPath = receivedIntent.getStringExtra("outputPath");                     // returns null if no output path was requested by the calling activity
        role = receivedIntent.getStringExtra("role");                                         //Worker oppure Manager

        Log.d(" Dati letti dall'intent", " timestamp " + timeoutMs + " requestCode " + receivedRequestCode + " path " + receivedOutputPath + " role " + role);

        try {
            myCamera = new EzCam(this);
            myCamera.startPreview(findViewById(R.id.previewView));

            stopRecordingButton = findViewById(R.id.stopRecordingButton);
            stopRecordingButton.setVisibility(View.INVISIBLE);
            stopRecordingButton.setClickable(false);
            stopRecordingButton.setOnClickListener(
                    (v) -> {
                        countDownStopRecording(System.currentTimeMillis() + 3000);
                    }
            );

            if (role.compareTo("Manager") == 0) {//todo, se è master il countdown dura di piu  perche non chiama la ready to start activity
                stopRecordingButton.setVisibility(View.VISIBLE);
                stopRecordingButton.setClickable(true);
            }


        } catch (Permissions.PermissionDeniedException e) {
            Toast.makeText(this, "Unable to access camera", Toast.LENGTH_LONG).show();
            Log.i("Permissions denied", e.getMessage());
            finish();
        }

        countDownStartRecording(timeoutMs);

    }


    private void countDownStopRecording(long timeToWait) {

        Log.d("sto fermando", "gg");
        stopRecordingButton.setClickable(false);
        Payload bytesPayload = Payload.fromBytes(("STOPRECORDING-" + Long.toString(timeToWait)).getBytes());
        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);

        //Toast.makeText(getApplicationContext(), "sono prima del countdown ma ho inviato", Toast.LENGTH_LONG).show();

       // myCamera.stopRecording();


        cm2 = new CountDownTimer(timeToWait - System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {
                    Toast.makeText(getApplicationContext(), "mi fermo", Toast.LENGTH_LONG).show();
                    Log.d("mo me fermo", "mo");
                    myCamera.stopRecording();
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        }.start();
    }

    private void countDownStartRecording(long timeToWait) {

        cm = new CountDownTimer(timeToWait - System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                Log.d("PRE START RECORDING", "STO PER CHIAMARE LA START RECORDING");

                try {
                    Log.d("PRE START RECORDING", "STO PER CHIAMARE LA START RECORDING - 1");

                    myCamera.startRecording(
                            (receivedOutputPath == null) ? null : Uri.fromFile(new File(receivedOutputPath)),
                            EzCam.VIDEO_ACTION,
                            (savedVideoUri) -> {
                                // called after myCamera.stopRecording() and when the file containing the video is available
                                Log.d("Torno", "allactivity precedente");
                                Intent intent = new Intent();
                                intent.setData(savedVideoUri);
                                if (savedVideoUri != null) {
                                    setResult(Activity.RESULT_OK, intent);
                                } else {
                                    setResult(Activity.RESULT_CANCELED, new Intent());
                                }
                                Log.d("Torno", "allactivity precedente");
                                finish();   // close activity and give result to caller// todo: posso spawnare la prissima activity qui al posto della finish
                            }
                    );
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }

                  /*  if (role.compareTo("Manager") == 0) {//todo, se è master il countdown dura di piu  perche non chiama la ready to start activity
                        stopRecordingButton.setVisibility(View.VISIBLE);
                        stopRecordingButton.setClickable(true);
                    }*/
            }
        }.start();
    }


    public static void stopRecording() {
        myCamera.stopRecording();
        //todo: lo start activity per l'activity successiva va fatto al posto della finish
    }


    private void recordingLogic() {
        // TODO: here the start/stop logic must be triggered by a message from the p2p master instead of a user tapping
        if (!isRecording)
            myCamera.startRecording(
                    (receivedOutputPath == null) ? null : Uri.fromFile(new File(receivedOutputPath)),
                    EzCam.VIDEO_ACTION,
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