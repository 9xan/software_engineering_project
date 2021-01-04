package com.example.provaapp.UsefulClasses;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.provaapp.ModeJoin_2_0.JoinSelectRoleActivity;
import com.example.provaapp.ModeJoin_2_0.ReadyToStartActivity;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

public class P2PWorkerNearby {

    public static String room;
    public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!
    public static Context c;
    public static int videoN, audioN;


    //qui devo METTERE TUTTO IL CODICE PER GESTIRE I VARI DATI IN INPUT, QUINDI SI PARLA DI BYTES O FILES!!!
    public static PayloadCallback workerCallback = new PayloadCallback() {

        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

            if (payload.getType() == Payload.Type.BYTES) {

                String[] in = new String(payload.asBytes()).split("-", 0);

                switch (in[0]) {
                    case "GO_ON":
                    case "DONE":
                        /*JoinSelectRoleActivity.start.setVisibility(View.VISIBLE);
                        JoinSelectRoleActivity.start.setClickable(true);*/
                        Log.e("TAG", "onPayloadReceived: TUTTO OK SELEZIONE DATI" );
                        //todo: settare una textview peravvisare che lo scambio dati ha avuto successo
                        break;

                    case "VA":
                        //il formato della stringa passata sarà "VIDEO-AUDIO" con i posti disponibili
                        videoN = Integer.parseInt(in[1]);
                        audioN = Integer.parseInt(in[2]);
                        //IMPORTANTE CONTROLLARE SE L'ACTIVITY C'è ANCORA!!! ALTRIMENTI SI ROMPE
                        //MAGARI CONTROLLARE CON ALTRI OGGETTI, TIPO .class della activity
                        JoinSelectRoleActivity.setView(audioN, videoN);
                        break;

                    case "FAILA":
                        Toast.makeText(c,
                                "The role you want to choose isn't available",
                                Toast.LENGTH_SHORT)
                                .show();
                        JoinSelectRoleActivity.videoBtn.setClickable(true);
                        break;

                    case "FAILV":
                        Toast.makeText(c,
                                "The role you want to choose isn't available",
                                Toast.LENGTH_SHORT)
                                .show();
                        JoinSelectRoleActivity.audioBtn.setClickable(true);
                        break;

                    case "TIMESTAMP":
                        //ReadyToStartActivity.timeToStart = Long.parseLong(in[1]);
                        Log.d("DIOMERDOSO", "E ARRIVATO IL TIMESTAMP");
                        String[] keys = {"Time", "Role"};
                        String[] vals = new String[2];
                        vals[0] = in[1];

                        if (JoinSelectRoleActivity.myRole == 0) {
                            vals[1] = "audio";
                        } else
                            vals[1] = "video";

                        JoinSelectRoleActivity.sendMessage(c, keys, vals, ReadyToStartActivity.class);
                        break;
                }


                //qui verrà messo il codice per dare input di iniziare e stoppare la registrazione (in pratica quando il master comanda gli altri di iniziare e fermare le registrazioni!)
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };


}
