package com.example.provaapp.UsefulClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.provaapp.ModeCreate_2_1.MasterCreationActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.google.android.gms.nearby.connection.Strategy.P2P_STAR;

public class P2PStarConnection {

    private MasterCreationActivity activity;
    private Context context;
    private String nickName;
    private String securityCode;



    public P2PStarConnection(MasterCreationActivity act, String n, String secureIdCode) {

        activity = act;
        context = activity.getApplicationContext();
        nickName = n;
        securityCode = secureIdCode;
    }

    /**************************************************************************************************/

    public void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(P2P_STAR).build();

        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        nickName, securityCode, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("Advertising", "Start_Advertising_Result: SUCCESS");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("Advertising", "Start_Advertising_Result: FAILURE");
                    }
                });
    }

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

                    if(activity.setReadyDevices>0){
                        activity.peers.get(activity.setReadyDevices).setText(s+" is Ready!");
                        activity.peerLoaders.get(activity.setReadyDevices).setVisibility(View.INVISIBLE);
                        activity.setReadyDevices--;
                    }else if(activity.setReadyDevices==0){
                        activity.finishButton.setVisibility(View.VISIBLE);
                        activity.finishButton.setClickable(true);
                    }
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
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            //TODO: incrementare i peers disponibili poichè si deve riconnettere
            //tipo: activity.setReadyDevice++;
        }
    };

    /**************************************************************************************************/

    public PayloadCallback payloadCallback = new PayloadCallback() {

        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
            byte[] receivedBytes = payload.asBytes();
            String out = new String(receivedBytes);
            //out sarà il messaggio ricevuto



            //File payloadFile = payload.asFile().asJavaFile();

            //Rename the file.
            //payloadFile.renameTo(new File(payloadFile.getParentFile(), "finito.mp4"));

        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
           // if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

            //}
        }


    };

    /**************************************************************************************************/


}
