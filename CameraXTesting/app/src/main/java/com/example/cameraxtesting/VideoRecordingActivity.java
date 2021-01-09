package com.example.cameraxtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.example.cameraxtesting.usefulclasses.EzCam;
import com.example.cameraxtesting.usefulclasses.Permissions;

import java.io.File;

public class VideoRecordingActivity extends AppCompatActivity {
    EzCam myCamera;
    Intent receivedIntent;
    int receivedRequestCode;
    String receivedOutputPath;

    boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recording);

        receivedIntent = getIntent();
        receivedRequestCode = receivedIntent.getIntExtra("requestCode", EzCam.ACTION_ERROR);  // returns EzCamera.NO_ACTION if no request code was provided by the calling activity
        receivedOutputPath = receivedIntent.getStringExtra("outputPath");                     // returns null if no output path was requested by the calling activity

        try {
            myCamera = new EzCam(this);
            myCamera.startPreview(findViewById(R.id.previewView));

            findViewById(R.id.stopRecordingButton).setEnabled(false);
            findViewById(R.id.stopRecordingButton).setOnClickListener(
                    (v) -> recordingLogic()
            );

            AppCompatActivity context = this;
            new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("startTick", "tick, tock...");
                }

                @Override
                public void onFinish() {
                    recordingLogic();
                    Toast.makeText(context, "Recording started", Toast.LENGTH_LONG).show();
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.d("stopTick", "tick, tock...");
                        }

                        @Override
                        public void onFinish() {
                            recordingLogic();
                        }
                    }.start();
                    //isRecording = !isRecording;
                    findViewById(R.id.stopRecordingButton).setEnabled(true);
                }
            }.start();

        } catch (Permissions.PermissionDeniedException e) {
            Toast.makeText(this, "Unable to access camera", Toast.LENGTH_LONG).show();
            Log.i("Permissions denied", e.getMessage());
            finish();
        }
    }

    private void recordingLogic() {
        // TODO: here the start/stop logic must be triggered by a message from the p2p master instead of a user tapping
        if (!isRecording)
            myCamera.startRecording(
                    (receivedOutputPath == null) ? null : Uri.fromFile(new File(receivedOutputPath)),
                    EzCam.MUTED_VIDEO_ACTION,
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
        else {
            myCamera.stopRecording();
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_LONG).show();
        }
        isRecording = !isRecording;
    }
}