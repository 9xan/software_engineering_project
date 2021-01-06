package com.example.provaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.provaapp.useful_classes.EzMic;
import com.example.provaapp.R;

import java.io.File;
import java.io.IOException;

public class AudioRecordingActivity extends AppCompatActivity {
    boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);

        Intent receivedIntent = getIntent();
        String receivedPath = receivedIntent.getStringExtra("outputPath");
        File receivedFile = (receivedPath == null) ? null : new File(receivedPath);

        EzMic myMic = new EzMic(this);

        Button audioRecordingButton = findViewById(R.id.audioRecordingButton);
        audioRecordingButton.setText("Start audio");
        audioRecordingButton.setOnClickListener(
                (v) -> {
                    // TODO: start/stop audio recording logic, when stopped send an intent with result data (see VideoRecordingActivity) and call finish()
                    if(!isRecording) {
                        try {
                            myMic.startRecordingAudio(receivedFile);    // can handle null file paths
                        } catch (IOException e) {
                            Log.e("audio failed", "saved to " + receivedFile.getPath());
                            e.printStackTrace();
                        }
                    }
                    else {
                        myMic.stopRecordingAudio();
                        Intent intent = new Intent();
                        intent.setData(Uri.fromFile(receivedFile));
                        setResult(Activity.RESULT_OK, intent);
                        finish();   // close activity and give result to caller
                    }
                    isRecording = !isRecording;
                }
        );

    }
}