package com.example.provaapp.ModeJoin_2_0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.WiFiBroadcastPeer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class JoinActivity extends AppCompatActivity {

    public final String[] permissionsList = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    public static final int ACCESS_FINE_LOCATION_CODE = 100;
    public WifiP2pManager manager;
    public WifiP2pManager.Channel channel;
    public WiFiBroadcastPeer receiver;
    public IntentFilter intentFilter;

    private ProgressBar pr;
    private TextView connectionText;
    private String[] qrData;

    private ArrayList<String> permissions = new ArrayList<>();
    private Bundle args;

    private ClientClass clientClass;
    private SendReceive sendReceive;

    public String[] nameDevices; //lista dei NOMI dei peers trovati
    public String[] devices;    //lista dei MAC address dei peers trovati, serve per connettere ad un determinato peer



    /*********************************************************************************************************/

    public WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {

            //ogni volta creo l'array
            nameDevices = new String[(peers.getDeviceList().size())];
            devices = new String[(peers.getDeviceList().size())];
            //salvo tutti i peerAddress e nomi sui vettori
            int i =0;
            for(WifiP2pDevice dev : peers.getDeviceList()){
                nameDevices[i]=dev.deviceName;
                devices[i]=dev.deviceAddress;
                i++;
            }
            //Toast.makeText(getApplicationContext(), "peers trovati ("+nameDevices.length+")", Toast.LENGTH_SHORT).show();
        }
    };

    /*********************************************************************************************************/

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            final InetAddress inetAddress = info.groupOwnerAddress;

            if(info.groupFormed){

                //collegamento al servershish per inviare i bytes
                //qui ci sarà solo UNA CONNESSIONE!!! poichè il collegamento è 1-Server a N-Peers
                //connessione riuscita, parte per il client
                pr.setVisibility(View.INVISIBLE);
                connectionText.setText("Connection to  established");

                clientClass = new ClientClass(inetAddress);
                clientClass.start();
            }
        }
    };


    /*********************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);
        Toolbar toolbar = findViewById(R.id.toolbarJoin);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        args = intent.getBundleExtra(FirstFragment.JoinKey);

        assert args != null;
        //Log.d("nel bundle della join c'è ", Objects.requireNonNull(args.getString("QRData")));
        qrData = Objects.requireNonNull(args.getString("QRData")).split("//" , 0);
        int i = 0;
        for (String s : qrData){
            Log.d("contenuto" + i , s);
            i++;
        }


        //ADDING PERMISSIONS
        this.permissions.addAll(Arrays.asList(permissionsList));

        // Capture the layout's TextView and set the string as its text
        TextView NickViewJoin = findViewById(R.id.NickViewJoin);
        NickViewJoin.setText(args.getString("NickName"));

        pr = findViewById(R.id.progressBarConnection);
        pr.setIndeterminate(true);
        connectionText = findViewById(R.id.connectionText);
        connectionText.setText("Connessione a stanza...");

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiBroadcastPeer(manager, channel, this);


        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Location permission granted",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Location permission denied",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default: {
                break;
            }
        }
    }


    /*********************************************************************************************************/

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt){
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while(socket!=null){
                try {
                    bytes=inputStream.read(buffer);
                    if(bytes>0){
                        handler.obtainMessage(1, bytes,-1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        public void myWrite(byte[] bytes) throws IOException {
            outputStream.write(bytes);
        }
    }

    /*********************************************************************************************************/

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress){
            hostAdd=hostAddress.getHostAddress();
            socket=new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                sendReceive=new SendReceive(socket);
                sendReceive.start();
                //
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /*********************************************************************************************************/
    //handler sarebbe per ricevere un messaggio... viene chiamato quando c'è
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            byte[] readBuff = (byte[]) msg.obj;
            String tempMsg = new String(readBuff,0,msg.arg1);
            //tempMsg è il messaggio arrivato
            return true;
        }
    });


    /*********************************************************************************************************/

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


}


