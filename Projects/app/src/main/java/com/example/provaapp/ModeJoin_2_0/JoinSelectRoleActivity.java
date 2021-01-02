package com.example.provaapp.ModeJoin_2_0;

import android.Manifest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.P2PWorkerNearby;
import com.example.provaapp.UsefulClasses.Permissions;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class JoinSelectRoleActivity extends AppCompatActivity {


    public TextView roomText;
    public static TextView audioAvailable, videoAvailable;
    public static Button audioBtn, videoBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_select_role_activity);
        Toolbar toolbar = findViewById(R.id.JoinRoleToolbar);
        //setSupportActionBar(toolbar);

        roomText = findViewById(R.id.textRoomView);
        audioBtn = findViewById(R.id.JoinAudioButton);
        videoBtn = findViewById(R.id.JoinVideoButton);
        audioAvailable = findViewById(R.id.joinAudioAvailable);
        videoAvailable = findViewById(R.id.joinVideoAvailable);

        audioAvailable.setText(P2PWorkerNearby.audioN);
        videoAvailable.setText(P2PWorkerNearby.videoN);

        roomText.setText(P2PWorkerNearby.room);

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payload bytesPayload = Payload.fromBytes("0-1".getBytes()); //così significa che chiedo di avere il posto di audioRecorder
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);
                P2PWorkerNearby.choosed=true;

                //aggiungere codice per proseguire con la prossima activity!!!!!!!!!!!!!!!!!!

                //QUI NON CI DOVREBBERO ESSERE PROBLEMI DI COORDINAMENTO IN QUANTO IL PAYLOADCALLBACK SI OCCUPA DI METTERE IL SETCLICKABLE DEL PULSANTE A FALSE APPENA FINISCONO I POSTI
                //C'è UN AGGIORNAMENTO CONTINUO DEI POSTI DA PARTE DEL MANAGER, BASTA TROVARE UNA VELOCITà ADATTA DI REFRESH DA PARTE DEL MANAGER PER EVITARE COLLISIONI!
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payload bytesPayload = Payload.fromBytes("1-0".getBytes()); //così significa che chiedo di avere il posto di videoRecorder
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);
                P2PWorkerNearby.choosed=true;
                //aggiungere codice per proseguire con la prossima activity!!!!!!!!!!!!!!!!!!
                //STESSO DISCORSO SOPRA
            }
        });


        //visto che mi sono già collegato posso fermare la discovery, la connessione col manager non verrà persa!
        Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
