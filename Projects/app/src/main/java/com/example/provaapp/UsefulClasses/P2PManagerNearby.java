package com.example.provaapp.UsefulClasses;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class P2PManagerNearby {

    public static String room;
    //public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!


    public static HashMap<String, String> workers = new HashMap<>();
    public static HashMap<String, PayloadCallback> workersPayload = new HashMap<>();
    public static List<String> endpoints = new ArrayList<>(); //ho aggiunto anche questa dato che c'è un metodo che permette  di inviare una roba a tutti i peers presenti nella lista
                                                                            //cosi da non fare un for earch

                                                                            //gli altri campi per ora non so se tenerli o meno, sto facendo prove

    public static int audioN, videoN;

    public static PayloadCallback newPayloadCallback(){
        return new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

                if (payload.getType() == Payload.Type.BYTES) {

                    //il formato della stringa passata sarà "VIDEO-AUDIO" con valore 1 sul ruolo che il peer vuole gestire!
                    String in = new String(payload.asBytes());
                    String[] out = in.split("-");

                    int v=Integer.parseInt(out[0]);
                    int a=Integer.parseInt(out[1]);
                    if(v==1){
                        --P2PManagerNearby.videoN;
                    }else if(a==1){
                        --P2PManagerNearby.audioN;
                    }
                }

            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

                //qui verrà messo il codice per gestire
            }
        };
    }


}
