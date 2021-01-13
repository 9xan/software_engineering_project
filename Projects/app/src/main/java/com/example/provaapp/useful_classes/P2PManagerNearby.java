package com.example.provaapp.useful_classes;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;

import com.example.provaapp.mode_create_2_1.ManagerShareActivity;
import com.example.provaapp.mode_create_2_1.WaitForPeerConfigurationActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class P2PManagerNearby {

    public static String room;
    //public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!

    public static SimpleArrayMap<String, Payload> incomingFilePayloads = new SimpleArrayMap<>();
    public static HashMap<String, String> workers = new HashMap<>(); //map ENDPOINTS-NICKNAME
    public static HashMap<String, PayloadCallback> workersPayload = new HashMap<>();
    public static HashMap<String, String> workerRole = new HashMap<>();
    public static Context c;
    public static ArrayList<String> endpoints = new ArrayList<>(); //ho aggiunto anche questa dato che c'è un metodo che permette  di inviare una roba a tutti i peers presenti nella lista
    //cosi da non fare un for earch
    public static int audioN, videoN, shareDataN = 0;

    public static PayloadCallback newPayloadCallback() {
        return new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {

                if (payload.getType() == Payload.Type.BYTES) {

                    String[] in = new String(payload.asBytes()).split("-", 0);

                    switch (in[0]) {
                        case "VA":  //si ricade in questo case nel caso in cui il client invii una richiesta di prenotazione di un ruolo che è identificata da un preambolo particolare(VA)

                            int v = Integer.parseInt(in[1]);
                            int a = Integer.parseInt(in[2]);

                            if (v == 1) {
                                if (videoN > 0) {
                                    --videoN;
                                    workerRole.put(endpointId, "video");
                                    Nearby.getConnectionsClient(c).sendPayload(endpointId, Payload.fromBytes("DONE-".getBytes()));
                                } else {
                                    Nearby.getConnectionsClient(c).sendPayload(endpointId, Payload.fromBytes("FAILV-".getBytes()));
                                    //TODO:INVIO NON CI SONO POSTI
                                }
                            } else if (a == 1) {
                                if (audioN > 0) {
                                    --audioN;
                                    workerRole.put(endpointId, "audio");
                                    Nearby.getConnectionsClient(c).sendPayload(endpointId, Payload.fromBytes("DONE-".getBytes()));
                                } else {
                                    Nearby.getConnectionsClient(c).sendPayload(endpointId, Payload.fromBytes("FAILA-".getBytes()));
                                }
                            }

                            if (WaitForPeerConfigurationActivity.audioView != null && WaitForPeerConfigurationActivity.videoView != null) {
                                WaitForPeerConfigurationActivity.audioView.setText(String.valueOf(audioN));
                                WaitForPeerConfigurationActivity.videoView.setText(String.valueOf(videoN));
                                if (audioN + videoN == 0) {
                                    WaitForPeerConfigurationActivity.finishBtn.setClickable(true);
                                    WaitForPeerConfigurationActivity.finishBtn.setVisibility(View.VISIBLE);
                                    WaitForPeerConfigurationActivity.finishView.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case "DATAREQUEST":

                            //mettere il codice per condividere tutto il pacchetto dei file multimediali


                            break;

                        //TODO: VERRANNO AGGIUNTI ALTRI CASE
                    }

                } else if (payload.getType() == Payload.Type.FILE) {

                    // : RICEVO UN VIDEO O UN AUDIO
                    Log.e("WORKER: " + workers.get(endpointId) + "  ", "CONDIVISIONE FILE INIZIATA");
                    incomingFilePayloads.put(endpointId, payload);

                }
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate update) {

                //qui verrà messo il codice per gestire gli arrivi dei file più avanti
                //aggiornare la progress bar
                //usare per la Progress barrrrrr
                //update.getBytesTransferred()/update.getTotalBytes()

                //CONTROLLO PER LO STESSO MOTIVO CHE HO SPIEGATO IN P2PWORKER


                if (incomingFilePayloads.get(s) != null && update.getStatus() == PayloadTransferUpdate.Status.SUCCESS && incomingFilePayloads.get(s).getType() == Payload.Type.FILE) {
                    //resettare la progress bar

                    Payload dataPayload = incomingFilePayloads.get(s);

                    //TODO FARE CONTROLLI CON TRY CATCH
                    File payloadFile = dataPayload.asFile().asJavaFile();

                    if (workerRole.get(s).compareTo("video") == 0) {
                        payloadFile.renameTo(new File(payloadFile.getParentFile(), workers.get(s) + ".mp4"));
                    } else if (workerRole.get(s).compareTo("audio") == 0) {
                        payloadFile.renameTo(new File(payloadFile.getParentFile(), workers.get(s) + ".mp3"));
                    }

                    if (++shareDataN < endpoints.size()) {//vedo se c'è un altro peer a cui devo chiedere il file

                        ManagerShareActivity.masterShareBar.setProgress(shareDataN + 1);
                        ManagerShareActivity.workerName.setText(workers.get(endpoints.get(shareDataN)));
                        Payload fin = Payload.fromBytes("DATA-".getBytes());   //request data
                        Nearby.getConnectionsClient(c).sendPayload(endpoints.get(shareDataN), fin);

                    } else {
                        //ricado qui se ho tutti i video, avviso i workers che è disponibile il pacchetto da scaricare
                        ManagerShareActivity.workerName.setText("");
                        ManagerShareActivity.infoShare.setText("Condivisione Finita!");
                        Payload mes = Payload.fromBytes("AVAILABLE-".getBytes());
                        Nearby.getConnectionsClient(c).sendPayload(P2PManagerNearby.endpoints, mes);
                    }

                }

            }
        };
    }


    private static void sendFilesFromFolder(File folder, String endpointWorker) {
        //mettere codice per condividere tutto il contenuto deentro la cartella a endpoint
    }


    public static void requestPeerVideo() {
        Payload fin = Payload.fromBytes("DATA-".getBytes());   //request data
        Nearby.getConnectionsClient(c).sendPayload(endpoints.get(shareDataN), fin);

        ManagerShareActivity.workerName.setText(workers.get(endpoints.get(shareDataN)));
        ManagerShareActivity.masterShareBar.setProgress(shareDataN + 1);
    }


}
