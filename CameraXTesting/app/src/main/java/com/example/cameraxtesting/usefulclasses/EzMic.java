package com.example.cameraxtesting.usefulclasses;

import android.media.MediaRecorder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class EzMic {
    MediaRecorder recorder;
    AppCompatActivity context;

    public EzMic(AppCompatActivity context) {
        //recorder = new MediaRecorder();
        this.context = context;
    }

    public void startRecordingAudio(File outputFile) throws IOException {
        stopRecordingAudio();
        /*if(recorder != null) {
            recorder.release();
        }*/

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        if(outputFile == null) outputFile = new File(context.getExternalMediaDirs()[0], "ezmic.mp3");
        recorder.setOutputFile(outputFile.getPath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recorder.prepare(); // throws IOException
        recorder.start();
    }

    public void stopRecordingAudio() {
        if(recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }
}
