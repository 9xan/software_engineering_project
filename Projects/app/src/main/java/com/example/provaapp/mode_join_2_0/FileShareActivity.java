package com.example.provaapp.mode_join_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PWorkerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class FileShareActivity extends AppCompatActivity {

    public static String filePath;
    public static Button downloadBtn, finishShareBtn;
    public static TextView shareText, downloadText;
    public static ProgressBar sharePgrBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_share);
        P2PWorkerNearby.c = this;

        Intent start = getIntent();

        filePath = start.getData().getPath();
        downloadBtn = findViewById(R.id.downloadFileBtn);
        finishShareBtn = findViewById(R.id.finishShareBtn);
        shareText = findViewById(R.id.dataShareText2);
        downloadText = findViewById(R.id.dataShareText3);
        sharePgrBar = findViewById(R.id.sharePgrBar);

        finishShareBtn.setOnClickListener(v -> {
            //mettere codice per tornare al menu principale dell'app, chi lo preme non potrà più scaricare i file
            //TODO: METTERE ANCHE CODICE PER DISCONNETTERE DAL MANAGER
            Payload bytesPayload = Payload.fromBytes("GOODBYE-".getBytes()); //avviso il manager che mi disconnetto dal gruppo
            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);


        });

        downloadBtn.setOnClickListener(v -> {
            Payload bytesPayload = Payload.fromBytes("DATAREQUEST-".getBytes()); //così significa che richiedo il pacchetto di file
            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);
        });


    }
}