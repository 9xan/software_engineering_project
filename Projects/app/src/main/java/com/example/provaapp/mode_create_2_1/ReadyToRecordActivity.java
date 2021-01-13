package com.example.provaapp.mode_create_2_1;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.AudioRecordingActivity;
import com.example.provaapp.R;
import com.example.provaapp.VideoRecordingActivity;
import com.example.provaapp.operative_activity_changer_1.MainActivity;
import com.example.provaapp.useful_classes.EzCam;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class ReadyToRecordActivity extends AppCompatActivity {


    private Button begin;
    private long tsLong;
    private String myRole;
    private TextView recordResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_to_record_activity);
        Toolbar toolbar = findViewById(R.id.toolbarReadyToStart);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        P2PManagerNearby.c = this;

        recordResult = findViewById(R.id.textViewRecorded);
        recordResult.setVisibility(View.INVISIBLE);

        myRole = intent.getStringExtra("MasterRole");

        begin = findViewById(R.id.BeginBTN);

        begin.setOnClickListener(v -> {
            tsLong = (System.currentTimeMillis() + 10000);
            String ts = Long.toString(tsLong);

            recordResult.setText("Start Recording...");

            Payload bytesPayload = Payload.fromBytes(("TIMESTAMP-" + ts).getBytes());
            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);

            new CountDownTimer(tsLong - System.currentTimeMillis(), 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (myRole.compareTo("Video Recorder") == 0) {
                        Intent forVideoIntent = new Intent(getApplicationContext(), VideoRecordingActivity.class);
                        forVideoIntent.putExtra("timestamp", System.currentTimeMillis() + 5000); //poco delay per fare in modo che la fotocamera si apra in tutti i dispositivi
                        forVideoIntent.putExtra("requestCode", EzCam.MUTED_VIDEO_ACTION);//mi avvia il player in modalità video muto
                        forVideoIntent.putExtra("role", "Manager"); // devo specificargli se sono un manager o un worker
                        forVideoIntent.putExtra("outputPath", MainActivity.appMediaFolderPath + "RecordVideoMaster.mp4"); //devo passargli un path todo : VEDERE COI FIOI
                        startActivityForResult(forVideoIntent, EzCam.REQUEST_CODE);
                    } else if (myRole.compareTo("Audio Recorder") == 0) {
                        //todo:aprire player in modalità audio recorder
                        Intent forVideoIntent = new Intent(getApplicationContext(), AudioRecordingActivity.class);
                        forVideoIntent.putExtra("timestamp", System.currentTimeMillis() + 5000); //poco delay per fare in modo che la fotocamera si apra in tutti i dispositivi
                        forVideoIntent.putExtra("role", "Manager"); // devo specificargli se sono un manager o un worker
                        forVideoIntent.putExtra("outputPath", MainActivity.appMediaFolderPath + "RecordAudioMaster.mp3"); //devo passargli un path todo : VEDERE COI FIOI
                        startActivityForResult(forVideoIntent, EzCam.REQUEST_CODE);
                    } else {
                        //todo:qui ho selezionato il ruolo none quindi non farò nulla se non  decidere quando i client dovranno fermarsi
                        Intent forVideoIntent = new Intent(getApplicationContext(), NoneRoleMasterActivity.class);
                        forVideoIntent.putExtra("timestamp", System.currentTimeMillis() + 5000); //poco delay per fare in modo che la fotocamera si apra in tutti i dispositivi
                        startActivityForResult(forVideoIntent, EzCam.REQUEST_CODE);
                    }
                    //mTextField.setText("done!");
                }
            }.start();
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EzCam.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recordResult.setText("Recorded Successfully");
                recordResult.setVisibility(View.VISIBLE);

                Intent nextAct = new Intent(this, ManagerShareActivity.class);
                nextAct.setData(data.getData());
                startActivity(nextAct);
                //TODO::QUI IL VIDEO E' SALVATO E DISPONIBILE

            }
        }
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
