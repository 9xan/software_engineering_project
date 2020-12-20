package com.example.provaapp.ModeCreate_2_1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.ExtendsForWifiDirectBroadcastReceiver;
import com.example.provaapp.UsefulClasses.WiFiDirectBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;


public class MasterCreationActivity extends ExtendsForWifiDirectBroadcastReceiver {

    public Bundle message;
    public ArrayList<TextView> peers;

    public TextView peer1;
    public TextView peer2;
    public TextView peer3;
    public TextView peer4;
    public TextView peer5;
    public TextView peer6;
    public Button finishButton;
    private String masterRole;
    private int peerNumber;

    public final String[] permissionsList = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };
    private ArrayList<String> permissions = new ArrayList<>();


    public WifiManager wifi;
    public WifiP2pManager manager;
    public WifiP2pManager.Channel channel;
    public WiFiDirectBroadcastReceiver receiver;
    public IntentFilter intentFilter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_creation_activity);

        Toolbar toolbar = findViewById(R.id.QRToolbar);
        setSupportActionBar(toolbar);

        peers = new ArrayList<>();

        Intent intent = getIntent();
        message = intent.getBundleExtra(QRCreationActivity.forMasterCreation); //tutti i dati della stanza sono qui
        finishButton = findViewById(R.id.finishSetupButton);
        assert message != null;
        masterRole = message.getString("masterRole");
        peerNumber = message.getInt("audioN") + message.getInt("videoN");

        peer1 = findViewById(R.id.peer1);
        peer1.setVisibility(View.VISIBLE);
        peers.add(peer1);

        peer2 = findViewById(R.id.peer2);
        peers.add(peer2);

        peer3 = findViewById(R.id.peer3);
        peers.add(peer3);

        peer4 = findViewById(R.id.peer4);
        peers.add(peer4);

        peer5 = findViewById(R.id.peer5);
        peers.add(peer5);

        peer6 = findViewById(R.id.peer6);
        peers.add(peer6);

        for (int i = 1; i < 7 - peerNumber; i++) { //TODO: fixxare i numeri
            peers.get(i).setVisibility(View.INVISIBLE);
        }


        /*assert masterRole != null;
        if(masterRole.compareTo("None") == 0){
            peer1.setVisibility(View.VISIBLE);
            peer2.setVisibility(View.VISIBLE);
            peer3.setVisibility(View.INVISIBLE);
            peer4.setVisibility(View.INVISIBLE);
            peer5.setVisibility(View.INVISIBLE);
            peer6.setVisibility(View.INVISIBLE);
        }else {
            peer1.setVisibility(View.VISIBLE);
            peer2.setVisibility(View.INVISIBLE);
            peer3.setVisibility(View.INVISIBLE);
            peer4.setVisibility(View.INVISIBLE);
            peer5.setVisibility(View.INVISIBLE);
            peer6.setVisibility(View.INVISIBLE);
        }*/

//usato per visualizzare stato wifi ed accensione e spegnimento tramite pulsante
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        serverSocketList = new ArrayList<>();

        //manager e channel per wifi direct, usati in WiFiDirectBroadcastReceiver
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        //aggiunta degli intent che voglio considerare
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        for(int i = 0 ; i < permissionsList.length ;i++){
            if (ContextCompat.checkSelfPermission(this, permissionsList[i]) == PackageManager.PERMISSION_GRANTED) {
            }else {
                ActivityCompat.requestPermissions(this, permissionsList, i);
            }
        }

        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //TODO:COMPLETARE CON L'INGE

            }
            @Override
            public void onFailure(int reason) {

            }
        });

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //TODO: STAMPARE SUCCESSO AVVIO RICERCA
            }
            @Override
            public void onFailure(int reason) {
                //TODO: STAMPARE FALLIMENTO AVVIO RICERCA
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

}

