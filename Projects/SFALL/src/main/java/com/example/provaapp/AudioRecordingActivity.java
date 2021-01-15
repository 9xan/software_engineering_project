package com.example.provaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.provaapp.useful_classes.EzMic;
import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.example.provaapp.useful_classes.P2PWorkerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AudioRecordingActivity extends AppCompatActivity {
    boolean isRecording = false;
    private EzMic myMic;
    private File receivedFile;
    private TextView elapsedTime;
    private Button stopAudioRecordingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent receivedIntent = getIntent();
        long timeoutMs = receivedIntent.getLongExtra("timestamp", System.currentTimeMillis() + 5000);
        String receivedPath = receivedIntent.getStringExtra("outputPath");
        String role = receivedIntent.getStringExtra("role");
        elapsedTime = findViewById(R.id.audioRecordingTime);
        stopAudioRecordingButton = findViewById(R.id.stopAudioRecordingButton);

        if (role.compareTo("Worker") == 0) {
            stopAudioRecordingButton.setClickable(false);
            stopAudioRecordingButton.setVisibility(View.INVISIBLE);//I'm a Worker, i'm setting my context in p2pWorkerNearby class
            P2PWorkerNearby.c = this;
        }

        receivedFile = (receivedPath == null) ? null : new File(receivedPath);
        myMic = new EzMic(this);

        stopAudioRecordingButton.setOnClickListener(
                (v) -> {
                    countDownStopRecording(System.currentTimeMillis() + 3000);
                }
        );

        countDownStartRecording(timeoutMs);

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

    private void countDownStartRecording(long timeToWait) {

        new CountDownTimer(timeToWait - System.currentTimeMillis(), 200) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {
                    aRecordingLogic();
                    startTiming(System.currentTimeMillis());
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        }.start();
    }


    private void countDownStopRecording(long timeToWait) {

        Log.d("sto fermando", "gg");
        stopAudioRecordingButton.setClickable(false);
        Payload bytesPayload = Payload.fromBytes(("STOPRECORDING-" + timeToWait).getBytes());
        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);

        new CountDownTimer(timeToWait - System.currentTimeMillis(), 200) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {
                    Toast.makeText(getApplicationContext(), "fermo la registrazione", Toast.LENGTH_LONG).show();;
                    aRecordingLogic();
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        }.start();
    }


    public void aRecordingLogic() {

        if (!isRecording) {
            try {
                myMic.startRecordingAudio(receivedFile);    // can handle null file paths
            } catch (IOException e) {
                Log.e("audio failed", "saved to " + receivedFile.getPath());
                e.printStackTrace();
            }
        } else {
            myMic.stopRecordingAudio();
            Intent intent = new Intent();
            intent.setData(Uri.fromFile(receivedFile));
            setResult(Activity.RESULT_OK, intent);
            finish();   // close activity and give result to caller
        }
        isRecording = !isRecording;
    }


}