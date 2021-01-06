package com.example.provaapp.mode_join_2_0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PWorkerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class JoinSelectRoleActivity extends AppCompatActivity {


    public TextView roomText;
    public static TextView audioAvailable, videoAvailable;
    public static Button audioBtn, videoBtn, start;
    public static int myRole = 0;


    public static void setView(int a, int v) {

        if (JoinSelectRoleActivity.audioAvailable != null && JoinSelectRoleActivity.videoAvailable != null) {

            JoinSelectRoleActivity.audioAvailable.setText(String.valueOf(a));
            JoinSelectRoleActivity.videoAvailable.setText(String.valueOf(v));

            if (v == 0) {
                JoinSelectRoleActivity.videoBtn.setClickable(false);
            }
            if (a == 0) {
                JoinSelectRoleActivity.audioBtn.setClickable(false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_select_role_activity);
        Toolbar toolbar = findViewById(R.id.peerConfigToolbar);
        setSupportActionBar(toolbar);

        roomText = findViewById(R.id.textRoomView);
        audioBtn = findViewById(R.id.JoinAudioButton);
        videoBtn = findViewById(R.id.JoinVideoButton);
        audioAvailable = findViewById(R.id.joinAudioAvailable);
        videoAvailable = findViewById(R.id.joinVideoAvailable);
        start = findViewById(R.id.finishRoleButton);

        audioAvailable.setText(String.valueOf(P2PWorkerNearby.audioN));
        videoAvailable.setText(String.valueOf(P2PWorkerNearby.videoN));
        P2PWorkerNearby.c = this;

        roomText.setText(P2PWorkerNearby.room);

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payload bytesPayload = Payload.fromBytes("VA-0-1".getBytes()); //così significa che chiedo di avere il posto di audioRecorder
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);
                audioBtn.setClickable(false);
                myRole = 0;
                //videoBtn.setClickable(false);
                //aggiungere codice per proseguire con la prossima activity!!!!!!!!!!!!!!!!

                //QUI NON CI DOVREBBERO ESSERE PROBLEMI DI COORDINAMENTO IN QUANTO IL PAYLOADCALLBACK SI OCCUPA DI METTERE IL SETCLICKABLE DEL PULSANTE A FALSE APPENA FINISCONO I POSTI
                //C'è UN AGGIORNAMENTO CONTINUO DEI POSTI DA PARTE DEL MANAGER, BASTA TROVARE UNA VELOCITà ADATTA DI REFRESH DA PARTE DEL MANAGER PER EVITARE COLLISIONI!
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payload bytesPayload = Payload.fromBytes("VA-1-0".getBytes()); //così significa che chiedo di avere il posto di videoRecorder
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);
                videoBtn.setClickable(false);
                myRole = 1;
                //audioBtn.setClickable(false);
                //aggiungere codice per proseguire con la prossima activity!!!!!!!!!!!!!!!!!!
                //STESSO DISCORSO SOPRA
            }
        });

        /*start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myRole == 0) {
                    sendMessage("Role", "audio", ReadyToStartActivity.class);
                } else {
                    sendMessage("Role", "video", ReadyToStartActivity.class);
                }
            }
        });*/

        //visto che mi sono già collegato posso fermare la discovery, la connessione col manager non verrà persa!
        Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void sendMessage(Context c, String[] key, String[] vals, Class<? extends AppCompatActivity> nextActivity) {
        int i = 0;
        Intent intent = new Intent(c, nextActivity);
        for (i = 0; i < key.length; i++) {
            intent.putExtra(key[i], vals[i]);
        }
        c.startActivity(intent);
    }

}
