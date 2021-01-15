package com.example.provaapp.mode_join_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.provaapp.R;
import com.example.provaapp.operative_activity_changer_1.MainActivity;
import com.example.provaapp.useful_classes.P2PWorkerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class FileShareActivity extends AppCompatActivity {

    public static String filePath;
    public static Button downloadBtn, finishShareBtn;
    public static TextView shareText, downloadText, downloadCompletedText;
    public static ProgressBar sharePgrBar, downloadPgrBar;

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
        downloadCompletedText = findViewById(R.id.downlaadCompletedText);
        downloadPgrBar=findViewById(R.id.sharePgrBar2);

        finishShareBtn.setOnClickListener(v -> {
            finishShareBtn.setClickable(false);

            //mettere codice per tornare al menu principale dell'app, chi lo preme non potrà più scaricare i file
            //TODO: METTERE ANCHE CODICE PER DISCONNETTERE DAL MANAGER
            Payload bytesPayload = Payload.fromBytes("GOODBYE-".getBytes()); //avviso il manager che mi disconnetto dal gruppo
            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);

            Nearby.getConnectionsClient(getApplicationContext()).disconnectFromEndpoint(P2PWorkerNearby.managerEndpointID);
            Nearby.getConnectionsClient(getApplicationContext()).stopAllEndpoints();

            Intent i = new Intent(FileShareActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            //TODO FARE CHE QUANDO TORNI ALLA HOME NON CI SIA IL VECCHIO NICKNAME VISUALIZZATO

        });

        downloadBtn.setOnClickListener(v -> {
            Payload bytesPayload = Payload.fromBytes("DATAREQUEST-".getBytes()); //così significa che richiedo il pacchetto di file
            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PWorkerNearby.managerEndpointID, bytesPayload);
            downloadText.setText("Download Pacchetto in corso...");
            downloadBtn.setVisibility(View.INVISIBLE);
            downloadBtn.setClickable(false);
        });


    }
}