package com.example.provaapp.useful_classes;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.CountDownTimer;
import android.os.Environment;
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
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class P2PWorkerNearby {

    public static String workerAppMediaFolderPath; // = Environment.getExternalStorageDirectory() + "/DCIM/multi_rec/"+P2PManagerNearby.room+"/";
    public static String room, myNickName;
    public static String managerEndpointID;  //ESSENZIALE PER LE CHIAMATE A METODI DI CONDIVISIONE DATI!!!
    public static Context c;
    public static int videoN, audioN;
    private static HashMap<String, Payload> incomingZipFile = new HashMap<>();
    private static HashMap<String, Payload> filePayloads = new HashMap<>();

    //qui devo METTERE TUTTO IL CODICE PER GESTIRE I VARI DATI IN INPUT, QUINDI SI PARLA DI BYTES O FILES!!!
    public static PayloadCallback workerCallback = new PayloadCallback() {

        //private boolean dataShared = false;

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

                    case "GETREADY4THEPARTY":
                        JoinSelectRoleActivity.finishAlertText.setVisibility(View.VISIBLE);
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
                        new CountDownTimer(Long.parseLong(in[1]) - System.currentTimeMillis(), 200) {
                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {

                                if (c instanceof VideoRecordingActivity) {              //se non è un video recorder allora è per forza un audio recorder(Worker)
                                    ((VideoRecordingActivity) c).vRecordingLogic();
                                } else {
                                    ((AudioRecordingActivity) c).aRecordingLogic();
                                }
                                c = null;
                            }
                        }.start();
                        break;

                    case "DATA":
                        //quando ricevo questo, devo inviare al master il mio file!!!
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
                            FileShareActivity.shareText.setText("Sharing with the Manager...");
                        }
                        c = null;
                        break;

                    case "AVAILABLE":
                        FileShareActivity.downloadText.setText("Package available for Download!");
                        FileShareActivity.downloadBtn.setClickable(true);
                        break;

                }

            } else if (payload.getType() == Payload.Type.FILE) {
                Log.d("WORKER", "inizio condivisione pacchetto files da master");
                incomingZipFile.put(managerEndpointID, payload);
                FileShareActivity.downloadText.setText("File sharing... Please wait");
                FileShareActivity.finishShareBtn.setVisibility(View.INVISIBLE);
                FileShareActivity.downloadPgrBar.setVisibility(View.VISIBLE);
            }
        }

        public int i = 0;

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

            //BISOGNA FARE QUESTO CONTROLLO PERCHè QUESTO CODICE DEVE ESSERE CHIAMATO SOLO PER CONDIVISIONE FILE, COSì SI TRALASCIA IN CASO DI BYTES
            if (filePayloads.get(s) != null && filePayloads.get(s).getType() == Payload.Type.FILE) {

                //mettere roba per la progress bar... capire come usare l'incremento oppure lasciare senza e che si veda solo indeterminate

                if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                    //se ricado qui vuol dire che la condivisione del mio file al manager è finita!
                    FileShareActivity.sharePgrBar.setVisibility(View.INVISIBLE);
                    FileShareActivity.sharePgrBar.setIndeterminate(false);
                    FileShareActivity.shareText.setText("Sharing package to Manager!");
                    FileShareActivity.finishShareBtn.setClickable(true);
                    FileShareActivity.finishShareBtn.setVisibility(View.VISIBLE);
                }
            }
            if (incomingZipFile.get(s) != null) {


                if(i%20==0)Log.e("TAG", "onPayloadTransferUpdate: " + i++);
                if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    File payloadFile = incomingZipFile.get(s).asFile().asJavaFile();
                    String zipFileName = room + ".zip";
                    payloadFile.renameTo(new File(payloadFile.getParentFile(), zipFileName));

                    FileShareActivity.downloadPgrBar.setVisibility(View.INVISIBLE);
                    FileShareActivity.downloadPgrBar.setIndeterminate(false);
                    FileShareActivity.downloadCompletedText.setVisibility(View.VISIBLE);
                    FileShareActivity.downloadText.setText("Donwload Terminato!");

                    //qui va il codice per quando si ha finito di ricevere il pacchetto
                    //mettere il codice per estrarre spostare lo zip, estrarlo e eliminare quello vecchio
                    P2PManagerNearby.moveFileFromDownload2Folder(room + ".zip",
                            Environment.getExternalStorageDirectory() + "/Download/Nearby/" + room + ".zip",
                            Environment.getExternalStorageDirectory() + "/DCIM/multi_rec/" + room + "/");

                    try {
                        unzipFolderWorker(Environment.getExternalStorageDirectory() + "/DCIM/multi_rec/" + room + "/" + room + ".zip",
                                Environment.getExternalStorageDirectory() + "/DCIM/multi_rec/" + room + "/");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    //metodo per worker per unzippare il pacchetto ed eliminare lo zip che non serve più
    private static void unzipFolderWorker(String zipPath, String folderPath) throws IOException {
        String fileZip = zipPath;
        File destDir = new File(folderPath);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            // write file content
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        try {
            Files.delete(Paths.get(zipPath));
        } catch (FileNotFoundException e) {
            Log.d("cannot delete Zip file -> ", e.getMessage());
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
