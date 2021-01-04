package com.example.provaapp.ModeCreate_2_1;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.P2PManagerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import static com.google.android.gms.nearby.connection.Strategy.P2P_STAR;


public class MasterCreationActivity extends AppCompatActivity {


    public Bundle message;

    public TextView peer1, peer2, peer3, peer4, peer5, peer6;
    public TextView myName;
    public ArrayList<TextView> peers;
    public static ArrayList<TextView> staticPeers;
    public Button finishButton;
    public ProgressBar peerL1, peerL2, peerL3, peerL4, peerL5, peerL6;
    public ArrayList<ProgressBar> peerLoaders;
    private String masterRole;      //da vedere a cosa serviva sta roba, non l'ho messa io
    public int tmpCounter = 1, peersCounter = tmpCounter - 1;
    public int peerNumber;
    public String myNickName, secureCode;

    public final String[] permissionsList = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private ArrayList<String> permissions = new ArrayList<>();

    /*********************************************************************************************************/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_creation_activity);

        Toolbar toolbar = findViewById(R.id.ConnToolbar);
        setSupportActionBar(toolbar);
        myName = findViewById(R.id.textViewMasterName);

        permissions.addAll(Arrays.asList(permissionsList));

        Intent intent = getIntent();
        message = intent.getBundleExtra(QRCreationActivity.forMasterCreation); //tutti i dati della stanza sono qui
        finishButton = findViewById(R.id.finishSetupButton);
        finishButton.setVisibility(View.INVISIBLE);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage(WaitForPeerConfigurationActivity.class);
                //METTERE CODICE PER PROSEGUIRE CON  WaitForPeerConfigurationActivity
            }
        });

        assert message != null;
        masterRole = message.getString("masterRole");

        P2PManagerNearby.audioN = message.getInt("audioN");
        P2PManagerNearby.videoN = message.getInt("videoN");

        peerNumber = message.getInt("audioN") + message.getInt("videoN");
        myName.setText(message.getString("RoomName"));

        peers = new ArrayList<>();
        peerLoaders = new ArrayList<>();

        myNickName = message.getString("NickName");
        secureCode = message.getString("secureHash");

        startXML(peers, peerNumber, peerLoaders); // questa funzione preapara l'interfaccia iniziale

        startAdvertising();

    }

    /**************************************************************************************************/

    public void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(P2P_STAR).build();
        Nearby.getConnectionsClient(getApplicationContext())
                .startAdvertising(
                        myNickName, secureCode, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MANAGER", "Start_Advertising_Result: SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MANAGER", "Start_Advertising_Result: FAILURE" + e.toString());
                        //di solito si ricade in qui per android service non aggiornato o permissions mancate
                    }
                });
    }

    /**************************************************************************************************/

    public ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            // Automatically accept the connection on both sides.
            Log.d("MANAGER", "onConnectionInitiated: OK");
            P2PManagerNearby.workers.put(s, connectionInfo.getEndpointName());              //aggiungo l'id (String) del worker che si è connesso e il suo nome
            P2PManagerNearby.endpoints.add(s);                                              //la lista servirà per mandare un payload a TUTTI i peers con una sola chiamata!!
            peers.get(peersCounter).setText(connectionInfo.getEndpointName() + " is Ready!");
            P2PManagerNearby.workersPayload.put(s, P2PManagerNearby.newPayloadCallback());
            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(s, P2PManagerNearby.workersPayload.get(s));
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {

            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    Log.d("MANAGER", "ConnectionsStatusCodes=STATUS_OK");
                    if (tmpCounter <= peerNumber) {
                        peerLoaders.get(peersCounter).setVisibility(View.INVISIBLE);
                        if (tmpCounter == peerNumber) {
                            finishButton.setVisibility(View.VISIBLE);
                            finishButton.setClickable(true);
                        }
                        tmpCounter++;
                        peersCounter++;
                    }
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    Log.e("MANAGER", "ConnectionsStatusCodes=STATUS_CONNECTION_REJECTED");
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.
                    Log.e("MANAGER", "ConnectionsStatusCodes=STATUS_ERROR");
                    break;
                default:
                    Log.d("MANAGER", "UNKNOW");
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            //TODO: incrementare i peers disponibili poichè si deve riconnettere
            //          tipo: activity.setReadyDevice++;
        }
    };

    /**************************************************************************************************/


    /**************************************************************************************************/

    private void startXML(ArrayList<TextView> peers, int peerNum, ArrayList<ProgressBar> loaders) {

        staticPeers = new ArrayList<>();

        peerL1 = findViewById(R.id.progressBarPeer1);
        peerL1.setIndeterminate(true);
        loaders.add(peerL1);
        peer1 = findViewById(R.id.peer1);
        peers.add(peer1);
        MasterCreationActivity.staticPeers.add(peer1);

        peerL2 = findViewById(R.id.progressBarPeer2);
        peerL2.setIndeterminate(true);
        loaders.add(peerL2);
        peer2 = findViewById(R.id.peer2);
        peers.add(peer2);
        MasterCreationActivity.staticPeers.add(peer2);

        peerL3 = findViewById(R.id.progressBarPeer3);
        peerL3.setIndeterminate(true);
        loaders.add(peerL3);
        peer3 = findViewById(R.id.peer3);
        peers.add(peer3);
        MasterCreationActivity.staticPeers.add(peer3);

        peerL4 = findViewById(R.id.progressBarPeer4);
        peerL4.setIndeterminate(true);
        loaders.add(peerL4);
        peer4 = findViewById(R.id.peer4);
        peers.add(peer4);
        MasterCreationActivity.staticPeers.add(peer4);

        peerL5 = findViewById(R.id.progressBarPeer5);
        peerL5.setIndeterminate(true);
        loaders.add(peerL5);
        peer5 = findViewById(R.id.peer5);
        peers.add(peer5);
        MasterCreationActivity.staticPeers.add(peer5);

        peerL6 = findViewById(R.id.progressBarPeer6);
        peerL6.setIndeterminate(true);
        loaders.add(peerL6);
        peer6 = findViewById(R.id.peer6);
        peers.add(peer6);
        MasterCreationActivity.staticPeers.add(peer6);

        for (int i = 5; i >= peerNum; i--) { //Mettiamo tutti su invisible tranne il primo, quando si collega il prossimo peer allora cambiamo nome e settiamo come visibile
            peers.get(i).setVisibility(View.INVISIBLE);
            loaders.get(i).setVisibility(View.INVISIBLE);
        }
    }


    /*********************************************************************************************************/

    public void sendMessage(Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this, nextActivity);

        startActivity(intent);
    }

    /*********************************************************************************************************/

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        Nearby.getConnectionsClient(getApplicationContext()).stopAdvertising();
        super.onPause();
    }

}

