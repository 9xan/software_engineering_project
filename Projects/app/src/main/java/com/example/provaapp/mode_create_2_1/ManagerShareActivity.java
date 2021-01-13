package com.example.provaapp.mode_create_2_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PManagerNearby;

public class ManagerShareActivity extends AppCompatActivity {

    public static TextView workerName, infoShare;
    public static ProgressBar masterShareBar;
    public String myFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_share);
        P2PManagerNearby.c = this;
        Intent start = getIntent();
        myFile = start.getData().getPath();
        workerName = findViewById(R.id.workerShareName);
        masterShareBar = findViewById(R.id.masterPgrBar);
        infoShare=findViewById(R.id.infoShareStatus);
        masterShareBar.setMax(P2PManagerNearby.workers.size());


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