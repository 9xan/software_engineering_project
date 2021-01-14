package com.example.provaapp.useful_classes;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class P2PManagerNearby {

    public static String room;
    //public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!

    public static String managerAppMediaFolderPath;     // = Environment.getExternalStorageDirectory() + "/DCIM/multi_rec/"+P2PManagerNearby.room+"/";
    public static SimpleArrayMap<String, Payload> incomingFilePayloads = new SimpleArrayMap<>();
    public static HashMap<String, String> workers = new HashMap<>(); //map ENDPOINTS-NICKNAME
    public static HashMap<String, PayloadCallback> workersPayload = new HashMap<>();
    public static HashMap<String, String> workerRole = new HashMap<>();
    public static Context c;
    public static ArrayList<String> endpoints = new ArrayList<>(); //ho aggiunto anche questa dato che c'è un metodo che permette  di inviare una roba a tutti i peers presenti nella lista
    //cosi da non fare un for earch
    public static ArrayList<String> filesInFolder = new ArrayList<>();
    public static int audioN, videoN, shareDataN = 0;
    public static String managerNickName;

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
                            ManagerShareActivity.send2WorkerPrgBar.setIndeterminate(true);
                            allFilesFromFolder(managerAppMediaFolderPath, filesInFolder);
                            for(String f : filesInFolder){

                                ParcelFileDescriptor pfd = null;
                                try {
                                    pfd = c.getContentResolver().openFileDescriptor(Uri.fromFile(new File(f)), "r");
                                } catch (FileNotFoundException e) {
                                    Log.e("TAG", "File not Found!!!");
                                    e.printStackTrace();
                                }

                                if (pfd != null) {
                                    Payload filesFolderPayload = Payload.fromFile(pfd);
                                    Nearby.getConnectionsClient(c).sendPayload(endpointId, filesFolderPayload);
                                    Log.d("", "onPayloadReceived: condivisione pacchetto a worker");
                                }
                            }
                            ManagerShareActivity.workerNameSend.setText(workers.get(endpointId));
                            ManagerShareActivity.infoShareSend.setText("Invio pacchetto a:");

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


                if (incomingFilePayloads.get(s) != null && incomingFilePayloads.get(s).getType() == Payload.Type.FILE && update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    Payload dataPayload = incomingFilePayloads.get(s);

                    //TODO FARE CONTROLLI CON TRY CATCH
                    File payloadFile = dataPayload.asFile().asJavaFile();

                    if (workerRole.get(s).compareTo("video") == 0) {

                        String videoFileName = workers.get(s) + ".mp4";
                        payloadFile.renameTo(new File(payloadFile.getParentFile(), videoFileName));
                        Log.e("TAG", "onPayloadTransferUpdate: " + videoFileName);
                        moveFileFromDownload2Folder(videoFileName, "/storage/emulated/0/Download/Nearby/" + videoFileName, managerAppMediaFolderPath);
                        incomingFilePayloads.remove(s);
                    } else if (workerRole.get(s).compareTo("audio") == 0) {
                        String audioFileName = workers.get(s) + ".mp3";
                        payloadFile.renameTo(new File(payloadFile.getParentFile(), audioFileName));
                        Log.e("TAG", "onPayloadTransferUpdate: " + audioFileName);
                        moveFileFromDownload2Folder(audioFileName, "/storage/emulated/0/Download/Nearby/" + audioFileName, managerAppMediaFolderPath);
                        incomingFilePayloads.remove(s);
                    }


                    if (++shareDataN < endpoints.size()) {//vedo se c'è un altro peer a cui devo chiedere il file

                        ManagerShareActivity.masterShareBar.setProgress(shareDataN + 1);
                        ManagerShareActivity.workerNameReceive.setText(workers.get(endpoints.get(shareDataN)));
                        Payload fin = Payload.fromBytes("DATA-".getBytes());   //request data
                        Nearby.getConnectionsClient(c).sendPayload(endpoints.get(shareDataN), fin);

                    } else {
                        //ricado qui se ho tutti i video, avviso i workers che è disponibile il pacchetto da scaricare
                        ManagerShareActivity.workerNameReceive.setText("");
                        ManagerShareActivity.infoShareReceive.setText("Condivisione Finita!");
                        Payload mes = Payload.fromBytes("AVAILABLE-".getBytes());
                        Nearby.getConnectionsClient(c).sendPayload(P2PManagerNearby.endpoints, mes);
                        ManagerShareActivity.lastManagerBtn.setClickable(true);
                        ManagerShareActivity.lastManagerBtn.setVisibility(View.VISIBLE);
                    }

                }

            }
        };
    }


    private static void sendFilesFromFolder(File folder, String endpointWorker) {
        //mettere codice per condividere tutto il contenuto deentro la cartella a endpoint
    }

    private static void moveFileFromDownload2Folder(String file2MoveName, String file2MovePath, String destinationFolder) {

        Path source = Paths.get(file2MovePath);
        Path target = Paths.get(destinationFolder + file2MoveName);

        try {
            Files.move(source, target);
            //Files.delete(source);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void requestPeerVideo() {
        Payload fin = Payload.fromBytes("DATA-".getBytes());   //request data
        Nearby.getConnectionsClient(c).sendPayload(endpoints.get(shareDataN), fin);

        ManagerShareActivity.workerNameReceive.setText(workers.get(endpoints.get(shareDataN)));
        ManagerShareActivity.masterShareBar.setProgress(shareDataN + 1);
    }

    public static void allFilesFromFolder(String folderPath, ArrayList<String> filePathList){

        File f = new File(folderPath);
        File[] files = f.listFiles();
        for (File i : files) {
            filePathList.add(i.getAbsolutePath());
            Log.e("", "allFilesFromFolder: "+i.getAbsolutePath());
        }

    }

}
