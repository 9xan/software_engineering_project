package com.example.provaapp.mode_join_2_0;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;

import java.util.Objects;


public class ReadyToStartActivity extends AppCompatActivity {

    private ProgressBar pb;
    public Long timeToStart;
    private String myRole;
    private TextView seconds;

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

        Log.d("Ho avviato l'activity Ready to Start e il timeout prima di iniziare è di :" , String.valueOf(timeToStart - System.currentTimeMillis()));

        new CountDownTimer(timeToStart - System.currentTimeMillis(), 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                seconds.setText(((Long)(millisUntilFinished/1000)).toString());
            }

            public void onFinish() {
                if (myRole.compareTo("audio") == 0) {
                    Log.d("Avvio " , "audio Recorder");
                    //TODO: AVVIARE L'ACTIVITY PER REGISTRARE AUDIO

                } else if (myRole.compareTo("video") == 0) {
                    Log.d("Avvio " , "video Recorder");
                    //TODO: AVVIARE L'ACTIVITY PER REGISTRARE VIDEO

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
