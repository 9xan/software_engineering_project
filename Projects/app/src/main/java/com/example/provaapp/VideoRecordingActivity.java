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


    private EzCam myCamera;
    private Intent receivedIntent;
    private int receivedRequestCode;
    private String receivedOutputPath;
    private Long timeoutMs;
    private boolean isRecording = false;
    private Button stopRecordingButton;
    private CountDownTimer cm;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recording);

        receivedIntent = getIntent();
        timeoutMs = receivedIntent.getLongExtra("timestamp", 5000);
        receivedRequestCode = receivedIntent.getIntExtra("requestCode", EzCam.ACTION_ERROR);  // returns EzCamera.NO_ACTION if no request code was provided by the calling activity
        receivedOutputPath = receivedIntent.getStringExtra("outputPath");                     // returns null if no output path was requested by the calling activity
        role = receivedIntent.getStringExtra("role");                                         //Worker oppure Manager

        try {
            Log.d("CAMERA", "onCreate: NEWEZ CAM Constructor");
            myCamera = new EzCam(this);

        } catch (Permissions.PermissionDeniedException e) {
            Toast.makeText(this, "Unable to access camera", Toast.LENGTH_LONG).show();
            Log.i("Permissions denied", e.getMessage());
            finish();
        }

        myCamera.startPreview(findViewById(R.id.previewView));

        findViewById(R.id.stopRecordingButton).setOnClickListener((v) -> recordingLogic());




        /*stopRecordingButton = findViewById(R.id.stopRecordingButton);
        stopRecordingButton.setVisibility(View.INVISIBLE);
        stopRecordingButton.setClickable(false);


        cm = new CountDownTimer(timeoutMs - System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
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
                            //finish();   // close activity and give result to caller// todo: posso spawnare la prissima activity qui al posto della finish
                        }
                );

                if (role.compareTo("Manager") == 0) {
                    stopRecordingButton.setVisibility(View.VISIBLE);
                    stopRecordingButton.setClickable(true);
                    stopRecordingButton.setOnClickListener(
                            (v) -> {
                                myCamera.stopRecording();
                                Payload bytesPayload = Payload.fromBytes(("STOPRECORDING-" + Long.toString(System.currentTimeMillis() + 7000)).getBytes());
                                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);
                            }
                    );
                }
            }
        }.start();

         */
    }


    public static void stopRecording() {
        //myCamera.stopRecording();
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