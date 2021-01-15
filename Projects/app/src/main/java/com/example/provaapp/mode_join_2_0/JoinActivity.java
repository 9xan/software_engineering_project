package com.example.provaapp.mode_join_2_0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PWorkerNearby;
import com.example.provaapp.useful_classes.Permissions;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.google.android.gms.nearby.connection.Strategy.P2P_STAR;

public class JoinActivity extends AppCompatActivity {


    public ProgressBar pr;
    public TextView connectionText;
    private String[] qrData;
    private String myNicknameDevice;
    public Button continueBtn;
    private String roomName, secureCode;
    private Bundle args;

    /*********************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbarJoin);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        args = intent.getBundleExtra(RoomInfoActivity.NextToJoinKey);
        myNicknameDevice = args.getString("NickName");
        P2PWorkerNearby.myNickName = myNicknameDevice;
        assert args != null;
        //Log.d("nel bundle della join c'è ", Objects.requireNonNull(args.getString("QRData")));
        qrData = Objects.requireNonNull(args.getString("QRData")).split("//", 0);
        int i = 0;
        for (String s : qrData) {
            Log.d("contenuto" + i, s);
            i++;
        }

        roomName = qrData[0];
        secureCode = qrData[1];

        P2PWorkerNearby.videoN = Integer.parseInt(qrData[3]);
        P2PWorkerNearby.audioN = Integer.parseInt(qrData[4]);

        P2PWorkerNearby.room = roomName;

        // Capture the layout's TextView and set the string as its text
        TextView NickViewJoin = findViewById(R.id.NickViewJoin);
        NickViewJoin.setText(myNicknameDevice);
        continueBtn = findViewById(R.id.continueBtn);

        pr = findViewById(R.id.progressBarConnection);
        connectionText = findViewById(R.id.connectionText);
        connectionText.setText("Searching Room -" + roomName + "-");


    }


    /**************************************************************************************************/

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(P2P_STAR).build();

        String[] st = new String[6];

        Nearby.getConnectionsClient(getApplicationContext())
                .startDiscovery(secureCode, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("PEER_DISCOVERY", "result: SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("PEER_DISCOVERY", "result: FAILURE");
                    }
                });
    }

    /**************************************************************************************************/

    public EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {

            // An endpoint was found. We request a connection to it.
            Nearby.getConnectionsClient(getApplicationContext())
                    .requestConnection(myNicknameDevice, s, connectionLifecycleCallback)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("CONNECTION", "requestConnection: SUCCESS");
                            connectionText.setText("Room -" + roomName + "- Found! Connecting...");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("CONNECTION", "requestConnection: FAILURE");
                        }
                    });


        }

        @Override
        public void onEndpointLost(@NonNull String s) {
            Log.d("ENDPOINT_DISCOVERY", "EndpointLost");
            // A previously discovered endpoint has gone away.
        }
    };

    /**************************************************************************************************/

    public ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            // Automatically accept the connection on both sides.
            // finisco qui se sono effettivamente connesso!! Salvo Id del manager che serve più avanti
            P2PWorkerNearby.managerEndpointID = s;
            //l'oggetto che passo per la callback, deve occuparsi di tutti i dati in input!
            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(s, P2PWorkerNearby.workerCallback);

        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    Log.d("CONNECTION", "ConnectionsStatusCodes=STATUS_OK");

                    pr.setVisibility(View.INVISIBLE);
                    pr.setIndeterminate(false);
                    connectionText.setText("Connection Accepted!");

                    continueBtn.setClickable(true);
                    continueBtn.setVisibility(View.VISIBLE);
                    continueBtn.setOnClickListener(v -> sendMessage(JoinSelectRoleActivity.class));

                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    Log.e("CONNECTION", "ConnectionsStatusCodes=STATUS_CONNECTION_REJECTED");
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.
                    Log.e("CONNECTION", "ConnectionsStatusCodes=STATUS_ERROR");
                    break;
                default:
                    Log.d("CONNECTION", "UNKNOW");
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            Log.e("TAG", "onDisconnected: DISCONNECTED");
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
        }
    };

    /**************************************************************************************************/


    /*********************************************************************************************************/

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

    /*********************************************************************************************************/

    @Override
    protected void onResume() {
        super.onResume();
        startDiscovery();
    }

    @Override
    protected void onPause() {
        Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
        super.onPause();
    }

    public void sendMessage(Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }
}


