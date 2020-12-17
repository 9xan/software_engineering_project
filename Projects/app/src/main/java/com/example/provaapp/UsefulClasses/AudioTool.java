package com.example.provaapp.UsefulClasses;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import java.io.IOException;

public class AudioTool {
    private static final String LOG_TAG = "AudioTool";
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;




    public void startPlaying(String path) {
        fileName=path;
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        player.release();
        player = null;
    }

    public void startRecording(String path) {
        fileName=path;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }




}