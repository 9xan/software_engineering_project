package com.example.provaapp.UsefulClasses;

import androidx.annotation.NonNull;

import com.example.provaapp.ModeJoin_2_0.JoinSelectRoleActivity;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

public class P2PWorkerNearby {

    public static String room;
    public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!

    public static int videoN, audioN;

    public static boolean choosed=false;

    //qui devo METTERE TUTTO IL CODICE PER GESTIRE I VARI DATI IN INPUT, QUINDI SI PARLA DI BYTES O FILES!!!
    public static PayloadCallback workerCallback = new PayloadCallback() {


        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

            if (payload.getType() == Payload.Type.BYTES) {

                if(!choosed) {
                    String in = new String(payload.asBytes());
                    String[] out = in.split("-");
                    //il formato della stringa passata sarà "VIDEO-AUDIO" con i posti disponibili
                    videoN = Integer.parseInt(out[0]);
                    audioN = Integer.parseInt(out[1]);


                    //IMPORTANTE CONTROLLARE SE L'ACTIVITY C'è ANCORA!!! ALTRIMENTI SI ROMPE
                    //MAGARI CONTROLLARE CON ALTRI OGGETTI, TIPO .class della activity
                    if (JoinSelectRoleActivity.audioAvailable != null && JoinSelectRoleActivity.videoAvailable != null) {

                        JoinSelectRoleActivity.videoAvailable.setText(videoN);
                        JoinSelectRoleActivity.audioAvailable.setText(audioN);

                        if (videoN == 0) {
                            JoinSelectRoleActivity.videoBtn.setClickable(false);
                        }
                        if (audioN == 0) {
                            JoinSelectRoleActivity.audioBtn.setClickable(false);
                        }
                    }
                }else{
                    //qui verrà messo il codice per dare input di iniziare e stoppare la registrazione (in pratica quando il master comanda gli altri di iniziare e fermare le registrazioni!)
                }


            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };


}
