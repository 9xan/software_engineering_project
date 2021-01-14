package com.example.provaapp.mode_create_2_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.provaapp.R;
import com.example.provaapp.operative_activity_changer_1.MainActivity;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class ManagerShareActivity extends AppCompatActivity {

    public static TextView workerNameReceive, infoShareReceive, workerNameSend, infoShareSend;
    public static ProgressBar masterShareBar, send2WorkerPrgBar;
    public String myFile;
    public static Button lastManagerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_share);
        P2PManagerNearby.c = this;
        Intent start = getIntent();
        myFile = start.getData().getPath();
        workerNameReceive = findViewById(R.id.workerShareName);
        masterShareBar = findViewById(R.id.masterPgrBar);
        infoShareReceive=findViewById(R.id.infoShareStatus);
        masterShareBar.setMax(P2PManagerNearby.workers.size());
        lastManagerBtn=findViewById(R.id.lastFinishManagerBtn);
        workerNameSend=findViewById(R.id.workerShareName2);
        infoShareSend=findViewById(R.id.infoShareStatus2);
        send2WorkerPrgBar=findViewById(R.id.masterPgrBar2);

        lastManagerBtn.setOnClickListener(v -> {
            //mettere il codice per tornare nel menù principale dell'app
            //inviare anche a tutti i peer che il manager ha chiuso la room e quindi disconnettere tutti e farli tornare nella main
            //se il manager chiude qui, i worker non saranno più in grado di scaricare il pacchetto, le persone a voce decidono quando chiudere il tutto una volta che i worker che vogliono il pacchetto lo avranno scaricato
            //TODO METTERE CODICE PER CHIUDERE LE CONNESSIONI DEL MASTER
            P2PManagerNearby.c = null;
            Payload bytesPayload = Payload.fromBytes("GOODBYE-".getBytes()); //avviso tutti i peer che il manager chiude tutte le connessioni
            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, bytesPayload);

            Nearby.getConnectionsClient(getApplicationContext()).stopAllEndpoints();
            Intent i = new Intent(ManagerShareActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            //TODO FARE CHE QUANDO TORNI ALLA HOME NON CI SIA IL VECCHIO NICKNAME VISUALIZZATO
        });


        //lascio un delay per dare il tempo ai workers di fermare il video e lanciare l'activity!
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.i("MANAGER","Richiesta video ai Workers!");
                        P2PManagerNearby.requestPeerVideo();
                    }
                }, 5000);




    }
}