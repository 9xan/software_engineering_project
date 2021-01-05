package com.example.cameraxtesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.cameraxtesting.usefulclasses.EzCam;
import com.example.cameraxtesting.usefulclasses.Permissions;
import com.example.cameraxtesting.usefulclasses.UiSettings;

public class MainActivity extends AppCompatActivity {
    EzCam myCamera;
    Uri myOutputUri;

    private boolean recordingState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // We need PreviewView (view object from XML file) to display a camera preview in the UI
            // this is required by the startCamera() method
            PreviewView previewView = findViewById(R.id.viewFinder);

            myCamera = new EzCam(this);
            myCamera.startPreview(previewView);

            // setup recording button
            setupRecordingButton();

            // setup rotation handling
            //setupRotationHandling();
            UiSettings.setupFullscreenRotationHandling(this);

            // setup replay button
            setupReplayButton();

            findViewById(R.id.recordIntentButton).setOnClickListener(
                    (v) -> {
                        Intent intent = new Intent(this, VideoRecordingActivity.class);
                        intent.putExtra("requestCode", EzCam.MUTED_VIDEO_ACTION);
                        //intent.putExtra("outputPath", outputFile.getPath());
                        //intent.setData(null);
                        startActivityForResult(intent, EzCam.REQUEST_CODE);
                    }
            );

            findViewById(R.id.audioRecordingIntentButton).setOnClickListener(
                    (v) -> {
                        Intent intent = new Intent(this, AudioRecordingActivity.class);
                        //intent.putExtra("outputFile", outputFile.getPath());
                        startActivityForResult(intent, 100);
                    }

            );

        } catch (Permissions.PermissionDeniedException e) {
            Toast.makeText(this, "Unable to access camera", Toast.LENGTH_LONG).show();
            Log.i("Permissions denied", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myCamera.stopRecording();   // EzCamera is smart enough to behave if we call stopRecording() while we are not recording
        findViewById(R.id.cameraCaptureButton).setBackgroundColor(Color.GRAY);  // set gray button color since recording was stopped
        recordingState = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UiSettings.hideSystemUI(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EzCam.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                myOutputUri = data.getData();
                findViewById(R.id.replayButton).setEnabled(true);
            }
        }
    }

    void setupRecordingButton() {
        // setup ImageButton that starts/stops recording
        ImageButton cameraCaptureButton = findViewById(R.id.cameraCaptureButton);
        cameraCaptureButton.setBackgroundColor(Color.GRAY);
        cameraCaptureButton.setOnClickListener(
                v -> {
                    if (!recordingState) {
                        // here if user requested a recording to be started
                        cameraCaptureButton.setBackgroundColor(Color.RED | Color.BLUE);

                        myCamera.startRecording(EzCam.MUTED_VIDEO_ACTION,
                                (Uri savedVideoUri) -> {
                                    // after a video has been successfully saved after a myCamera.stopRecording() we get here
                                    //if (myOutputUri != null)
                                    //    new File(myOutputUri.getPath()).delete();   // delete last video
                                    if (savedVideoUri != null) {    // if recording did not fail
                                        myOutputUri = savedVideoUri;    // we remember the saved video URI

                                        // NOTE: for some reason running the code below crashes the app
                                        //findViewById(R.id.replayButton).setEnabled(true);
                                        //findViewById(R.id.cameraCaptureButton).setEnabled(true);   // enable recording button which allows the user to record again
                                    }
                                }
                        );
                        Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();
                    } else {
                        findViewById(R.id.replayButton).setEnabled(true);
                        // here if user requested a recording to be stopped
                        findViewById(R.id.cameraCaptureButton).setEnabled(true);   // disable recording button (until onVideoSaved callback enables it again)
                        cameraCaptureButton.setBackgroundColor(Color.GRAY);
                        myCamera.stopRecording();   // stop recording and start saving video in the background
                        Toast.makeText(this, "Recording stopped", Toast.LENGTH_LONG).show();
                    }
                    recordingState = !recordingState;
                }
        );
    }

    void setupRotationHandling() {
        // activity view, it is manipulated to enable immersive mode in portrait and landscape device rotations
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                // TODO: The system bars are visible. Make any desired change
                // adjustments to your UI, such as showing the action bar or
                // other navigational controls.
                UiSettings.hideSystemUI(this);
            } else {
                // TODO: The system bars are NOT visible. Make any desired change
                // adjustments to your UI, such as hiding the action bar or
                // other navigational controls.
            }
        });
    }

    void setupReplayButton() {
        Button replayButton = findViewById(R.id.replayButton);
        replayButton.setEnabled(false);  // disable replay button
        replayButton.setOnClickListener(
                (v) -> {
                    Intent replayVideoIntent = new Intent(this, VideoDisplayActivity.class);    // prepare video replay intent
                    replayVideoIntent.setData(myOutputUri);     // set an URI to be retrieved by the called activity (it points to the video)
                    startActivity(replayVideoIntent);
                }
        );
    }
}