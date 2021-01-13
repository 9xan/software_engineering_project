package com.example.provaapp.useful_classes;

import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.provaapp.AudioRecordingActivity;
import com.example.provaapp.VideoRecordingActivity;
import com.example.provaapp.mode_join_2_0.FileShareActivity;
import com.example.provaapp.mode_join_2_0.JoinSelectRoleActivity;
import com.example.provaapp.mode_join_2_0.ReadyToStartActivity;
import com.example.provaapp.operative_activity_changer_1.MainActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class P2PWorkerNearby {

    public static String room;
    public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!
    public static Context c;
    public static int videoN, audioN;
    private static Payload incomingFile = null;
    private static HashMap<String, Payload> filePayloads = new HashMap<>();

    //qui devo METTERE TUTTO IL CODICE PER GESTIRE I VARI DATI IN INPUT, QUINDI SI PARLA DI BYTES O FILES!!!
    public static PayloadCallback workerCallback = new PayloadCallback() {

        private boolean dataShared = false;

        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

            if (payload.getType() == Payload.Type.BYTES) {

                String[] in = new String(payload.asBytes()).split("-", 0);

                switch (in[0]) {
                    case "GO_ON":
                    case "DONE":
                        /*JoinSelectRoleActivity.start.setVisibility(View.VISIBLE);
                        JoinSelectRoleActivity.start.setClickable(true);*/
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
                        String[] keys = {"Time", "Role"};
                        String[] vals = new String[2];
                        vals[0] = in[1];

                        if (JoinSelectRoleActivity.myRole == 0) {
                            vals[1] = "audio";
                        } else
                            vals[1] = "video";

                        JoinSelectRoleActivity.sendMessage(c, keys, vals, ReadyToStartActivity.class);
                        c = null;
                        break;

                    case "STOPRECORDING":

                        new CountDownTimer(Long.parseLong(in[1]) - System.currentTimeMillis(), 1000) {
                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {

                                if (c instanceof VideoRecordingActivity) {              //se non è un video recorder allora è per forza un audio recorder(Worker)
                                    ((VideoRecordingActivity) c).recordingLogic();
                                } else {
                                    ((AudioRecordingActivity) c).recordingLogic();
                                }
                                c = null;
                            }
                        }.start();

                        break;

                    case "DATA":    //quando ricevo questo, devo inviare al master il mio file!!!

                        ParcelFileDescriptor pfd = null;
                        try {
                            pfd = c.getContentResolver().openFileDescriptor(Uri.fromFile(new File(FileShareActivity.filePath)), "r");
                        } catch (FileNotFoundException e) {
                            Log.e("TAG", "File not Found!!!");
                            e.printStackTrace();
                        }

                        if (pfd != null) {
                            Payload sharePayload = Payload.fromFile(pfd);
                            filePayloads.put(s, sharePayload);
                            Nearby.getConnectionsClient(c).sendPayload(managerEndpointID, sharePayload);
                            FileShareActivity.shareText.setText("Condivisione col Manager...");
                        }
                        c = null;

                        break;

                    case "AVAILABLE":
                        FileShareActivity.downloadText.setText("Pacchetto disponibile per Download!");
                        FileShareActivity.downloadBtn.setClickable(true);

                        break;
                }

                //qui verrà messo il codice per dare input di iniziare e stoppare la registrazione (in pratica quando il master comanda gli altri di iniziare e fermare le registrazioni!)
            } else if (payload.getType() == Payload.Type.FILE) {
                Log.d("WORKER", "inizio condivisione pacchetto files da master");
                incomingFile = payload;
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

            //BISOGNA FARE QUESTO CONTROLLO PERCHè QUESTO CODICE DEVE ESSERE CHIAMATO SOLO PER CONDIVISIONE FILE, COSì SI TRALASCIA IN CASO DI BYTES
            if(filePayloads.get(s)!=null && filePayloads.get(s).getType() == Payload.Type.FILE) {

                if (!dataShared) {
                    //mettere roba per la progress bar... capire come usare l'incremento oppure lasciare senza
                }


                if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    if (!dataShared) {
                        //se ricado qui vuol dire che la condivisione del mio file al manager è finita!
                        FileShareActivity.sharePgrBar.setVisibility(View.INVISIBLE);
                        FileShareActivity.sharePgrBar.setIndeterminate(false);
                        FileShareActivity.shareText.setText("Invio File al Manager Completato!");
                        FileShareActivity.finishShareBtn.setClickable(true);
                        FileShareActivity.finishShareBtn.setVisibility(View.VISIBLE);
                        dataShared = true;

                    } else {
                        //GESTIRE RICEZIONE FILE DAL MANAGER
                    }

                }
            }
        }
    };

}
