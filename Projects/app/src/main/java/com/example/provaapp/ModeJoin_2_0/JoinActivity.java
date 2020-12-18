package com.example.provaapp.ModeJoin_2_0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
/*
import android.net.wifi.WifiManager;

 */
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.WiFiDirectBroadcastReceiver;

import java.util.ArrayList;
import java.util.Arrays;

public class JoinActivity extends AppCompatActivity {

    public final String[] permissionsList = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    public static final int ACCESS_FINE_LOCATION_CODE = 100;
    public WifiP2pManager manager;
    public WifiP2pManager.Channel channel;
    public WiFiDirectBroadcastReceiver receiver;
    public IntentFilter intentFilter;
    public String[] nameDevices;
    public String[] devices;
    public WifiP2pConfig config;
    public TextView status_text;
    public TextView testo;
    private ArrayList<String> permissions = new ArrayList<>();
    private Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);
        Toolbar toolbar = findViewById(R.id.toolbarJoin);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        args = intent.getBundleExtra(FirstFragment.JoinKey);


        assert args != null;
        Log.d("nel bundle della join c'è ", args.getString("QRData"));

        //ADDING PERMISSIONS
        this.permissions.addAll(Arrays.asList(permissionsList));

        // Capture the layout's TextView and set the string as its text
        TextView NickViewJoin = findViewById(R.id.NickViewJoin);
        NickViewJoin.setText(args.getString("NickName"));


        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        testo = (TextView) findViewById(R.id.ricercaText);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //status_text = (TextView) findViewById(R.id.status_text);
    }

    //metodo del bottone per provare a collegarsi al secondo telefono
    public void connectionStart(View v) {
        config = new WifiP2pConfig();
        config.deviceAddress = devices[0];

        for (String permission : this.permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
        if (devices.length == 0) {
            Toast.makeText(getApplicationContext(), "No devices available", Toast.LENGTH_SHORT).show();
        } else {
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "connessione a peer riuscita", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), "connessione a peer fallita", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Location permission granted",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Location permission denied",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    //metodo collegato al bottone di ricerca, quello che fa è usare il metodo del manager per iniziare a vedere se ci sono peer vicino, onSuccess e onFailure dicono solo se la ricerca è partita o meno!
    public void ricercaPeers(View v) {
        //TODO:DA FARE ALLA FINE ME RACCOMANDO
        //connect richiede il channel, config che non è altro che peer trovato nella lista e il listener per dire cosa fare in caso di successo o meno
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                testo.setText("Ricerca Peers avviata");
            }

            @Override
            public void onFailure(int reason) {
                testo.setText("Ricerca Peers Fallita");
            }
        });
    }


    public void inviaFile(View v) {
        Toast.makeText(getApplicationContext(), "ClientInvioFile", Toast.LENGTH_SHORT).show();

        //da finire di implementare... devo ancora vedere perchè non funziona l'uso dei socket

    }

    //TODO: ASSOCIARE CON I LISTENER
    public void riceviFile(View v) {
        Toast.makeText(getApplicationContext(), "ServerRicezioneFile", Toast.LENGTH_SHORT).show();
        //stesso discorso del metodo sopra
    }


    //questo oggetto 'listener' si occuperà di effettuare operazione quando trova la lista dei peers.... nel mio caso per provare la salvo ma mi connetto solo al primo peer, dato che sto facendo prove con solo 2 telefoni
    public WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            nameDevices = new String[(peers.getDeviceList().size())];
            devices = new String[(peers.getDeviceList().size())];
            int i = 0; // per ora conto solo il primo peer che capita, vedo come si chiama e mi connetto
            for (WifiP2pDevice dev : peers.getDeviceList()) {
                nameDevices[i] = dev.deviceName;
                devices[i] = dev.deviceAddress;
                i++;
            }
            //stampo il nome del primo peer trovato, sempre perchè provo con 2 telefoni
            testo.setText("ci sono " + nameDevices.length + " disp e il primo si chiama " + nameDevices[0]);
            //Toast.makeText(getApplicationContext(),"ci sono"+nomeDispositivi.length+"disp e il primo si chiama"+nomeDispositivi[0],Toast.LENGTH_SHORT).show();

        }
    };


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


