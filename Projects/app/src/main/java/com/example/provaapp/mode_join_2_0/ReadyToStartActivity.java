package com.example.provaapp.mode_join_2_0;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.AudioRecordingActivity;
import com.example.provaapp.R;
import com.example.provaapp.VideoRecordingActivity;
import com.example.provaapp.operative_activity_changer_1.MainActivity;
import com.example.provaapp.useful_classes.EzCam;

import java.util.Objects;


public class ReadyToStartActivity extends AppCompatActivity {

    private ProgressBar pb;
    public Long timeToStart;
    private String myRole;
    private TextView seconds;
    public static String uriPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_to_start_activity);
        Toolbar toolbar = findViewById(R.id.toolbarWaitToStart);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        //myRole = intent.getStringExtra("Role");
        timeToStart = Long.parseLong(Objects.requireNonNull(intent.getStringExtra("Time")));
        myRole = intent.getStringExtra("Role");

        seconds = findViewById(R.id.TextCountDown);
        pb = findViewById(R.id.progressBarStart);

        pb.setIndeterminate(true);

        new CountDownTimer(timeToStart - System.currentTimeMillis(), 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                seconds.setText(((Long) (millisUntilFinished / 1000)).toString());
            }

            public void onFinish() {


                if (myRole.compareTo("audio") == 0) {
                    Log.d("Avvio ", "audio Recorder");
                    //TODO: AVVIARE L'ACTIVITY PER REGISTRARE AUDIO
                    Intent forVideoIntent = new Intent(getApplicationContext(), AudioRecordingActivity.class);
                    forVideoIntent.putExtra("timestamp", System.currentTimeMillis() + 5000); //poco delay per fare in modo che la fotocamera si apra in tutti i dispositivi
                    forVideoIntent.putExtra("role", "Worker"); // devo specificargli se sono un manager o un worker
                    forVideoIntent.putExtra("outputPath", MainActivity.appMediaFolderPath + "RecordAudioWorker.mp3"); //devo passargli un path todo : VEDERE COI FIOI
                    startActivityForResult(forVideoIntent, EzCam.REQUEST_CODE);

                } else if (myRole.compareTo("video") == 0) {
                    Log.d("Avvio ", "video Recorder");
                    //TODO: AVVIARE L'ACTIVITY PER REGISTRARE VIDEO
                    Intent forVideoIntent = new Intent(getApplicationContext(), VideoRecordingActivity.class);
                    forVideoIntent.putExtra("timestamp", System.currentTimeMillis() + 5000); //poco delay per fare in modo che la fotocamera si apra in tutti i dispositivi
                    forVideoIntent.putExtra("requestCode", EzCam.MUTED_VIDEO_ACTION);//mi avvia il player in modalità video muto
                    forVideoIntent.putExtra("role", "Worker"); // devo specificargli se sono un manager o un worker
                    forVideoIntent.putExtra("outputPath", MainActivity.appMediaFolderPath + "RecordVideoWorker.mp4"); //devo passargli un path todo : VEDERE COI FIOI
                    startActivityForResult(forVideoIntent, EzCam.REQUEST_CODE);
                } else {
                    Log.d("error:", "wrong parameter");
                }
                //mTextField.setText("done!");
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EzCam.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                uriPath = data.getData().getPath();
                //TODO::QUI IL VIDEO E' SALVATO E DISPONIBILE
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void sendMessage(Bundle s, String Key, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        intent.putExtra(Key, s);
        startActivity(intent);
    }

}
