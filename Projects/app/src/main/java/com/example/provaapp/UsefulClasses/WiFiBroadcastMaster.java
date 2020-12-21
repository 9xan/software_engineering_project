package com.example.provaapp.UsefulClasses;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.provaapp.ModeCreate_2_1.MasterCreationActivity;

import java.util.ArrayList;

public class WiFiBroadcastMaster extends BroadcastReceiver {

    //RECEIVER PER IL MASTER

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MasterCreationActivity activity;
    private ArrayList<String> permissions = new ArrayList<>();
    private int peers2Connect;

    public WiFiBroadcastMaster(WifiP2pManager manager, WifiP2pManager.Channel channel, MasterCreationActivity activity, int nPeers) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        peers2Connect= nPeers;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.CHANGE_NETWORK_STATE);
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify with toast
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "WIFI ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "WIFI OFF", Toast.LENGTH_SHORT).show();
            }

        }else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            // si ricade qui in caso di connessione

            if(manager != null){
                for (String i : permissions) {

                    if (ActivityCompat.checkSelfPermission(activity, i) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                assert networkInfo != null;
                if (networkInfo.isConnected()) {
                    peers2Connect--;
                    manager.requestConnectionInfo(channel, activity.connectionInfoListener);
                }
                //controllo se è stato raggiunto il numero di peers connessi, se si allora termino la ricerca, bisogna provare se per caso sta roba fa cadere tutte le connessioni!!
                if(peers2Connect==0) {

                    manager.requestGroupInfo(channel, activity.groupInfoListener);
                    manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            //blablabla magari una call back o metodo statico che avvisa che tutti si sono connessi
                        }

                        @Override
                        public void onFailure(int reason) {
                            //blablabla
                        }
                    });
                }

            }


        }

    }
}
