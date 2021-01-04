package com.example.provaapp.UsefulClasses;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.provaapp.ModeCreate_2_1.WaitForPeerConfigurationActivity;
import com.example.provaapp.ModeJoin_2_0.JoinActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

public class P2PManagerNearby {

    public static String room;
    //public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!

    private static Semaphore mutex = new Semaphore(1);
    public static HashMap<String, String> workers = new HashMap<>();
    public static HashMap<String, PayloadCallback> workersPayload = new HashMap<>();
    public static Context c;
    public static List<String> endpoints = new ArrayList<>(); //ho aggiunto anche questa dato che c'è un metodo che permette  di inviare una roba a tutti i peers presenti nella lista
    //cosi da non fare un for earch

    //gli altri campi per ora non so se tenerli o meno, sto facendo prove
    public static int audioN, videoN;

    public static PayloadCallback newPayloadCallback() {
        return new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {

                    if (payload.getType() == Payload.Type.BYTES) {

                        //il formato della stringa passata sarà "VIDEO-AUDIO" con valore 1 sul ruolo che il peer vuole gestire!
                        String in = new String(payload.asBytes());

                        String[] out = in.split("-", 0);

                        int v = Integer.parseInt(out[0]);
                        int a = Integer.parseInt(out[1]);

                        if (v == 1) {
                            if (videoN > 0) {
                                --videoN;
                                Nearby.getConnectionsClient(c).sendPayload(endpointId,  Payload.fromBytes("DONE-".getBytes()));
                            } else {
                                Nearby.getConnectionsClient(c).sendPayload(endpointId, Payload.fromBytes("FAILV-".getBytes()));
                                //TODO:INVIO NON CI SONO POSTI
                            }
                        } else if (a == 1) {
                            if (audioN > 0) {
                                --audioN;
                                Nearby.getConnectionsClient(c).sendPayload(endpointId,  Payload.fromBytes("DONE-".getBytes()));
                            } else {
                                Nearby.getConnectionsClient(c).sendPayload(endpointId,  Payload.fromBytes("FAILA-".getBytes()));
                                //TODO:INVIO NON CI SONO POSTI
                            }
                        }

                        if (WaitForPeerConfigurationActivity.audioView != null && WaitForPeerConfigurationActivity.videoView != null) {
                            WaitForPeerConfigurationActivity.audioView.setText(String.valueOf(audioN));
                            WaitForPeerConfigurationActivity.videoView.setText(String.valueOf(videoN));
                            if (audioN + videoN == 0) {
                                WaitForPeerConfigurationActivity.finishBtn.setClickable(true);
                                WaitForPeerConfigurationActivity.finishBtn.setVisibility(View.VISIBLE);
                                //WaitForPeerConfigurationActivity.finishView.setVisibility(View.VISIBLE);
                            }
                        }
                    }else if(payload.getType() == Payload.Type.FILE){
                        //TODO : RICEVO UN VIDEO O UN AUDIO
                    }
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                //qui verrà messo il codice per gestire gli arrivi dei file più avanti
            }
        };
    }


}
