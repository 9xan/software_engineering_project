package com.example.provaapp.ModeCreate_2_1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.Permissions;
import com.example.provaapp.UsefulClasses.WiFiBroadcastMaster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;


public class MasterCreationActivity extends AppCompatActivity {

    public Bundle message;

    public TextView peer1,peer2, peer3, peer4, peer5, peer6;
    public ArrayList<TextView> peers;
    public Button finishButton;
    private String masterRole;
    public static int peerNumber;
    private ProgressBar pr;

    private Collection<SendReceive> serverSocketList;
    private SendReceive sendReceive;
    private ServerClass serverClass;

    public final String[] permissionsList = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };
    private ArrayList<String> permissions = new ArrayList<>();


    //public WifiManager wifi;  da vedere se serve, il suo scopo sarebbe solo gestire il wifi ma se mettiamo avvisi prima di creare la room oppure quando si apre l'app allora qui non serve più
    public WifiP2pManager manager;
    public WifiP2pManager.Channel channel;
    public WiFiBroadcastMaster receiver;
    public IntentFilter intentFilter;



    //i due campi che seguono sono usati come callback sul broadcast del master e sono creati apposta per il master per avviare il gruppo ed il pool di socket quando ci sono nuove connessioni
    //IMPORTANTE: qui ci sarà solo codice di connessione per il master, questo perchè la parte dei client sta in un punto diverso, non serve più vedere questo codice come una "fork", sappiamo già che questo codice saarà chiamato solo dal master
    /*********************************************************************************************************/

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            final InetAddress inetAddress = info.groupOwnerAddress;
            //info ha 4 campi, di cui 3 essensiali, nel nostro caso, essendo master, basterà vedere se c'è connessione effettiva ed avviare il socket per ogni peer
            //la add delle connessioni alla collection è dentro il codice del server Thread, li dovrò  verificare la sicurezza del peer connesso poichè verrà passata una stringa appena connesso

            if(info.groupFormed && info.isGroupOwner) {
                //faccio partire il thread per socket con un client  e dentro il thread c'è il codice per aggiungere la connessione al pool di socket
                serverClass = new ServerClass();
                serverClass.start();

            }
        }
    };

    /*********************************************************************************************************/

    //finite le connessioni, verrà chiamato questo campo come callback, qui si arriva con tutte le connessioni, e il parametro group contiene la lista di peers!
    public WifiP2pManager.GroupInfoListener groupInfoListener = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            Collection<WifiP2pDevice> listClient =  group.getClientList();
            String out ="";
            int i=0;
            for(WifiP2pDevice p : listClient){
                out += p.deviceName;
                i++;
            }
            //socketView.setText("INFO X OWNER tot peers:"+i+" lista: "+out);

        }
    };

    /*********************************************************************************************************/



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_creation_activity);

        Toolbar toolbar = findViewById(R.id.QRToolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        message = intent.getBundleExtra(QRCreationActivity.forMasterCreation); //tutti i dati della stanza sono qui
        finishButton = findViewById(R.id.finishSetupButton);
        assert message != null;
        masterRole = message.getString("masterRole");
        peerNumber = message.getInt("audioN") + message.getInt("videoN");
        peers = new ArrayList<>();

        startXML(peers , peerNumber);


        //da vedere se serve, lo scopo di wifi sarebbe solo gestire i servizi inerenti al wifi, ma se mettiamo avvisi prima di creare la room oppure quando si apre l'app allora qui non serve più
        //wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

       // serverSocketList = new ArrayList<>();
/*
        //manager e channel per wifi direct, usati in WiFiDirectBroadcastReceiver
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null); //qui sarebbe da aggiungere un listener in caso di disconnessione del channel e che riprova subito a connettere
        receiver = new WiFiBroadcastMaster(manager, channel, this, peerNumber); //TODO controllare bene la parte dei numero di peers da connettere

        //aggiunta degli intent che voglio considerare
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);*/

        Permissions.requestPermissions(this  , permissionsList , 101);
      /*
        //Cancel any ongoing p2p group negotiation
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });

        //Clear all registered local services of service discovery.
        manager.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });

        //Clear all registered service discovery requests.
        manager.clearServiceRequests(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });

        //Remove the current p2p group.
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //TODO:COMPLETARE CON L'INGE
                //non serve mettere nulla
            }
            @Override
            public void onFailure(int reason) {
                //magari avvisare se non funziona e dire di ricreare la stanza
            }
        });

        //Le fasi di remove sono essenziali per far si che le connessioni siano "nuove" e che non ci siano connessioni automatiche di telefoni già salvati prima di leggere il QR!

        //adesso parte discovery dei peers, nel caso del master qeusto servirà solamente a renderlo visibile, in quanto non tutti i telefoni sono già visibili su wifi direct con solo wifi acceso
        //magari sta funzione è da chiamare qualche millesimo dopo la rimozione delle vecchie configurazioni per non avere problemi di avvio della ricerca
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //TODO: STAMPARE SUCCESSO AVVIO RICERCA
            }
            @Override
            public void onFailure(int reason) {
                //TODO: STAMPARE FALLIMENTO AVVIO RICERCA
                //si ricade qui principalmente perchè non è acceso il WIFI, bisogna vedere se mettere un avviso prima oppure avvisare e dare la possibilità di far ripartire la ricerca senza rifare la room!
            }
        });
*/

    }

    private void startXML(ArrayList<TextView> peers  , int peerNum){

        peer1 = findViewById(R.id.peer1);
        peers.add(peer1);

        peer2 = findViewById(R.id.peer2);
        peers.add(peer2);

        peer3 = findViewById(R.id.peer3);
        peers.add(peer3);

        peer4 = findViewById(R.id.peer4);
        peers.add(peer4);

        peer5 = findViewById(R.id.peer5);
        peers.add(peer5);

        peer6 = findViewById(R.id.peer6);
        peers.add(peer6);

        for (int i = 5; i >= peerNum ; i--) { //Mettiamo tutti su invisible tranne il primo, quando si collega il prossimo peer allora cambiamo nome e settiamo come visibile
            peers.get(i).setVisibility(View.INVISIBLE);
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

    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;
        //qui c'è il codice per il ServerThread (group owner) che dete tenere tutto il pool di connessioni aperte
        @Override
        public void run() {
            try{
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                serverSocketList.add(sendReceive);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    /*********************************************************************************************************/

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            byte[] readBuff = (byte[]) msg.obj;
            String tempMsg = new String(readBuff,0,msg.arg1);
          //  dataView.setText(tempMsg);
            return true;
        }
    });

    /*********************************************************************************************************/


    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(receiver);
    }

}

