package com.example.provaapp.UsefulClasses;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.provaapp.ModeJoin_2_0.JoinActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.ArrayList;

import static com.google.android.gms.nearby.connection.Strategy.P2P_STAR;

public class P2PClientConnection {


    private ArrayList<String> permissions = new ArrayList<>();
    private JoinActivity activity;
    private Context context;
    private String nickName;
    private String securityCode;

    public P2PClientConnection(JoinActivity act, String n, String secureIdCode) {

        activity = act;
        context = activity.getApplicationContext();
        nickName = n;
        securityCode = secureIdCode;

        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.BLUETOOTH);
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    /**************************************************************************************************/

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(P2P_STAR).build();

        String[] st = new String[6];
        Permissions.requestPermissions(activity, permissions.toArray(st), 200);
        Nearby.getConnectionsClient(context)
                .startDiscovery(securityCode, endpointDiscoveryCallback, discoveryOptions)
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

            activity.pr.setVisibility(View.INVISIBLE);
            activity.connectionText.setVisibility(View.INVISIBLE);
            // An endpoint was found. We request a connection to it.

            final String st=s;
            /*final String[] st= new String[1];
            st[0]=s;*/

            activity.connectBtn.setVisibility(View.VISIBLE);
            activity.connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Nearby.getConnectionsClient(context)
                            .requestConnection(nickName, st, connectionLifecycleCallback)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("CONNECTION", "requestConnection: SUCCESS");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("CONNECTION", "requestConnection: FAILURE");
                                }
                            });
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
            Nearby.getConnectionsClient(context).acceptConnection(s, payloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    Log.d("CONNECTION", "ConnectionsStatusCodes=STATUS_OK");
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

    public PayloadCallback payloadCallback = new PayloadCallback() {

        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().

            /*byte[] receivedBytes = payload.asBytes();
            String out = new String(receivedBytes);
            dataText.setText(out);*/


            //File payloadFile = payload.asFile().asJavaFile();
            // Rename the file.
            //payloadFile.renameTo(new File(payloadFile.getParentFile(), "finito.mp4"));


        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

            }
        }


    };

}
