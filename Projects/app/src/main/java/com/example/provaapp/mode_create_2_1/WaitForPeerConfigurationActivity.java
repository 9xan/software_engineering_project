package com.example.provaapp.mode_create_2_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

import java.util.Timer;
import java.util.TimerTask;

public class WaitForPeerConfigurationActivity extends AppCompatActivity {

    public static Button finishBtn;
    public static TextView audioView, videoView, finishView, roomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_peer_configuration_activity);
        Toolbar toolbar = findViewById(R.id.peerConfigToolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        audioView = findViewById(R.id.audioManagerView);
        videoView = findViewById(R.id.videoManagerView);
        finishView = findViewById(R.id.finishView);
        finishBtn = findViewById(R.id.finishConfBTN);

        roomView = findViewById(R.id.roomManagerView);
        roomView.setText(P2PManagerNearby.room);
        audioView.setText(String.valueOf(P2PManagerNearby.audioN));
        videoView.setText(String.valueOf(P2PManagerNearby.videoN));

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(intent.getStringExtra("MasterRole") , ReadyToRecordActivity.class);
            }
        });

        //quando si è qua posso fermare l advertising del manager in quanto tutti si sono connessi e non manca nessuno
        Nearby.getConnectionsClient(getApplicationContext()).stopAdvertising();

        Log.d("MANAGER", "stopAdvertising");
        P2PManagerNearby.c = this;

        update();

    }


    // ci vuole qualcosa di simile a setIntervall di Js per mandare ai peers i posti disponibili, praticamente il manager condivide ogni tot i posti disponibili a tutti i workers
    //poi deve fermarsi appena tutti hanno scelto ..... un esempio che mi è venuto in mente al volo
    private void update() {

        final Timer myTimer = new Timer();

        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("MANAGER", "Sending updates to peers");
                if (P2PManagerNearby.videoN + P2PManagerNearby.audioN != 0) {
                    String s = "VA-" + (P2PManagerNearby.videoN) + "-" + (P2PManagerNearby.audioN);
                    Payload mes = Payload.fromBytes(s.getBytes());
                    Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, mes);
                } else {
                    Payload mes = Payload.fromBytes("VA-0-0".getBytes());
                    Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, mes);
                    //ultimo messaggio a tutti i peer per dire di stare pronti che si passa alla prossima fare

                    //metttere il codice della payload finale qui
                    Payload fin = Payload.fromBytes("GO_ON-".getBytes());
                    Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, fin);
                    myTimer.cancel();
                }
            }
        }, 0, 200);
    }

    private void finished(Timer t) {
        t.cancel();

        finishBtn.setClickable(true);
        finishBtn.setVisibility(View.VISIBLE);
        finishView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void sendMessage(String role , Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        intent.putExtra("MasterRole" , role);
        startActivity(intent);
    }

}