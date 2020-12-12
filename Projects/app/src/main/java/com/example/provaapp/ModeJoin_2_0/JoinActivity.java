package com.example.provaapp.ModeJoin_2_0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
/*
import android.net.wifi.WifiManager;

 */
import android.graphics.Paint;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.WiFiDirectBroadcastReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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
    public WiFiDirectBroadcastReceiver receiver;
    public IntentFilter intentFilter;
    public String[] nameDevices;
    public String[] devices;
    public WifiP2pConfig config;
    public ServerClass serverClass;
    public ClientClass clientClass;
    public SendReceive sendReceive;

    public TextView dataView, wifiView, peerView, clienthostView;                   //textview che uso per debug e vedere gli stati
    private ArrayList<String> permissions = new ArrayList<>();      //array usato per velocizzare il controllo delle permissions
    private Button srcButton, connButton, dataButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);
        Toolbar toolbar = findViewById(R.id.toolbarJoin);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String message = intent.getStringExtra(FirstFragment.JoinKey);

        //ADDING PERMISSIONS
        this.permissions.addAll(Arrays.asList(permissionsList));

        // Capture the layout's TextView and set the string as its text
        TextView NickViewJoin = findViewById(R.id.NickViewJoin);
        NickViewJoin.setText(message);

        inizializeXML();

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);      //manager del wifip2pservice
        channel = manager.initialize(this, getMainLooper(), null);  //channel si ricava dal metodo inizialize del manager
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);     //receiver creato col costruttore della classe che ho fatto per gestire le azioni che succedono riguardanti il wifi direct
        intentFilter = new IntentFilter();                                              //aggiungo al filter tutte le azioni che mi interessano
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    public void inizializeXML(){

        peerView = (TextView) findViewById(R.id.ricerca_view);     //una textview per visualizzare il peer disponibile alla connessione... anche questo è temporaneo
        wifiView = (TextView) findViewById(R.id.wifi_view);     //lo lascio per adesso così da facilitare il debug
        dataView = (TextView) findViewById(R.id.data_text);     //qui si vedrà il materiale inviato da peer a peer, per adesso sarà una stringa
        clienthostView = (TextView) findViewById(R.id.clienthostView);

        this.srcButton = (Button) findViewById(R.id.researcbtn);
        this.connButton = (Button) findViewById(R.id.connectionbtn);
        this.dataButton = (Button) findViewById(R.id.transferbtn);

        ///////////////////////////////SEARCH BUTTON LISTENER///////////////////////////////
        srcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestedPermission = Manifest.permission.ACCESS_FINE_LOCATION;
                // check if the requested permission has already be granted or not. If not, enter the 'if' body
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), requestedPermission) != PackageManager.PERMISSION_GRANTED) {
                    // spawn a dialog with the specified permission. As soon as the user grant or deny the permission, onRequestPermissionsResult will be called
                    ActivityCompat.requestPermissions(JoinActivity.this, new String[]{requestedPermission}, ACCESS_FINE_LOCATION_CODE);
                }
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        peerView.setText("Ricerca Peers avviata");
                    }

                    @Override
                    public void onFailure(int reason) { peerView.setText("Ricerca Peers Fallita"); }
                });
            }
        });
        ///////////////////////////////CONNECTION BUTTON LISTENER///////////////////////////////
        connButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestedPermission = Manifest.permission.ACCESS_FINE_LOCATION;
                config = new WifiP2pConfig();
                config.deviceAddress = devices[0];

                if (ActivityCompat.checkSelfPermission(JoinActivity.this, requestedPermission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(JoinActivity.this, new String[]{requestedPermission}, ACCESS_FINE_LOCATION_CODE);
                }
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() { Toast.makeText(getApplicationContext(), "CONNECTION OK", Toast.LENGTH_SHORT).show();}
                    @Override
                    public void onFailure(int reason){ Toast.makeText(getApplicationContext(), "CONNECTION ERROR", Toast.LENGTH_SHORT).show();}
                });
            }
        });
        ///////////////////////////////DATA TRANSFER BUTTON LISTENER///////////////////////////////
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"INVIO IN CORSO",Toast.LENGTH_SHORT).show();
                String s = "ciao";
                try {
                    sendReceive.write(s.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    // called when a user grant or reject the permission for a functionality
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_FINE_LOCATION_CODE: {
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
                        handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        public void write(byte[] bytes) throws IOException {
            outputStream.write(bytes);
        }
    }

    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try{
                serverSocket=new ServerSocket(8888);
                socket=serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                ///
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


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


    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            byte[] readBuff = (byte[]) msg.obj;
            String tempMsg = new String(readBuff,0,msg.arg1);
            dataView.setText(tempMsg);
            return true;
        }
    });


    //questo oggetto 'listener' si occuperà di effettuare operazione quando trova la lista dei peers.... nel mio caso per provare la salvo ma mi connetto solo al primo peer, dato che sto facendo prove con solo 2 telefoni
    public WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            nameDevices = new String[(peers.getDeviceList().size())];
            devices = new String[(peers.getDeviceList().size())];
            int i = 0; // per ora conto solo il primo peer che capita, vedo come si chiama e mi connetto
            for (WifiP2pDevice dev : peers.getDeviceList()) {
                nameDevices[i] = dev.deviceName;
                devices[i] = dev.deviceAddress;
                i++;
            }
            //stampo il nome del primo peer trovato, sempre perchè provo con 2 telefoni
            peerView.setText("ci sono " + nameDevices.length + " disp, il primo è " + nameDevices[0]);
            //Toast.makeText(getApplicationContext(),"ci sono"+nomeDispositivi.length+"disp e il primo si chiama"+nomeDispositivi[0],Toast.LENGTH_SHORT).show();

        }
    };

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress inetAddress = info.groupOwnerAddress;


            if(info.groupFormed && info.isGroupOwner){
                clienthostView.setText("sono HOST e devo ricevere una stringa");
                serverClass = new ServerClass();
                serverClass.start();
            }else if(info.groupFormed){
                clienthostView.setText("sono CLIENT e devo inviare una stringa");
                clientClass = new ClientClass(inetAddress);
                clientClass.start();
            }
        }
    };


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


