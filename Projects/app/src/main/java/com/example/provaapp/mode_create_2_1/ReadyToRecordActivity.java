package com.example.provaapp.mode_create_2_1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.EzCam;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class ReadyToRecordActivity extends AppCompatActivity {


    private Button begin;
    private long tsLong;
    private String myRole;
    private CountDownTimer ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_to_record_activity);
        Toolbar toolbar = findViewById(R.id.toolbarReadyToStart);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        myRole = intent.getStringExtra("MasterRole");

        begin = findViewById(R.id.BeginBTN);

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsLong = (System.currentTimeMillis() + 10000);
                String ts = Long.toString(tsLong);

                Payload bytesPayload = Payload.fromBytes(("TIMESTAMP-" + ts).getBytes());
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);
            }
        });

        ct = new CountDownTimer(tsLong - System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (myRole.compareTo("Video Recorder") == 0) {
                    Intent forVideoIntent = new Intent();
                    forVideoIntent.putExtra("timestamp", 5000); //poco delay per fare in modo che la fotocamera si apra in tutti i dispositivi
                    forVideoIntent.putExtra("requestCode", EzCam.MUTED_VIDEO_ACTION);//mi avvia il player in modalità video muto
                    forVideoIntent.putExtra("role", "Manager"); // devo specificargli se sono un manager o un worker
                    forVideoIntent.putExtra("outputPath", "/storage/emulated/0/DCIM/EpVideos/RecordTest/RecordVideoMaster.mp4"); //devo passargli un path todo : VEDERE COI FIOI
                } else if (myRole.compareTo("Audio Recorder") == 0) {
                    //todo:aprire player in modalità audio recorder

                } else {
                    //todo:qui ho selezionato il ruolo none quindi non farò nulla se non  decidere quando i client dovranno fermarsi
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
