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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.WiFiBroadcastMaster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class MasterCreationActivity extends AppCompatActivity {

    public Bundle message;

    public TextView peer1, peer2, peer3, peer4, peer5, peer6;
    public TextView myName;
    public ArrayList<TextView> peers;
    public static ArrayList<TextView> staticPeers;
    public Button finishButton;
    public ProgressBar peerL1, peerL2, peerL3, peerL4, peerL5, peerL6;
    private ArrayList<ProgressBar> peerLoaders;
    private String masterRole;
    private int setReadyDevices;
    public static int peerNumber;

    private Collection<SendReceive> socketCollection;
    private SendReceive sendReceive;
    private ServerClass serverClass;

    public final String[] permissionsList = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
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

            if (info.groupFormed && info.isGroupOwner) {
                //faccio partire il thread per socket con un client  e dentro il thread c'è il codice per aggiungere la connessione al pool di socket
                serverClass = new ServerClass(); //Devo mandare l'address per mantere un collegamento socket-peer per distinguere le connessioni
                serverClass.start();

                if (setReadyDevices > 0) {
                    peers.get(setReadyDevices).setText("Ready");
                    peerLoaders.get(setReadyDevices).setVisibility(View.INVISIBLE);
                    setReadyDevices--;
                }
                //TODO: CANAGLIA AL CRISTO 2
            }
        }
    };

    /*********************************************************************************************************/

    //finite le connessioni, verrà chiamato questo campo come callback, qui si arriva con tutte le connessioni, e il parametro group contiene la lista di peers!
    public WifiP2pManager.GroupInfoListener groupInfoListener = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            Collection<WifiP2pDevice> listClient = group.getClientList();

            //TODO: CANAGLIA IL CRISTO
            onPeersConnectedChangeView(listClient);

            if (setReadyDevices == 0) {
                finishButton.setVisibility(View.VISIBLE);
                finishButton.setClickable(true);
            }
            //vediamo dopo se c'è da aggiungere altro

        }
    };

    /*********************************************************************************************************/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_creation_activity);

        Toolbar toolbar = findViewById(R.id.ConnToolbar);
        setSupportActionBar(toolbar);
        myName = findViewById(R.id.textViewMasterName);

        permissions.addAll(Arrays.asList(permissionsList));


        Intent intent = getIntent();
        message = intent.getBundleExtra(QRCreationActivity.forMasterCreation); //tutti i dati della stanza sono qui
        finishButton = findViewById(R.id.finishSetupButton);
        finishButton.setVisibility(View.INVISIBLE);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: START THE NEXT ACTIVITY
            }
        });

        assert message != null;
        masterRole = message.getString("masterRole");
        peerNumber = message.getInt("audioN") + message.getInt("videoN");
        myName.setText(message.getString("RoomName"));

        setReadyDevices = peerNumber - 1;
        peers = new ArrayList<>();
        peerLoaders = new ArrayList<>();


        startXML(peers, peerNumber, peerLoaders); // questa funzione preapara l'interfaccia iniziale

        //da vedere se serve, lo scopo di wifi sarebbe solo gestire i servizi inerenti al wifi, ma se mettiamo avvisi prima di creare la room oppure quando si apre l'app allora qui non serve più
        //wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        //manager e channel per wifi direct, usati in WiFiDirectBroadcastReceiver
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null); //qui sarebbe da aggiungere un listener in caso di disconnessione del channel e che riprova subito a connettere
        receiver = new WiFiBroadcastMaster(manager, channel, this, peerNumber); //TODO controllare bene la parte dei numero di peers da connettere

        //aggiunta degli intent che voglio considerare
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //Permissions.requestPermissions(this, permissionsList, 101);

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

        //questa sleep bisogna vedere se mantenerla oppure no, bisogna vedere se togliendola ci sono problemi tra l'inizio della discover e le varie remove precedenti
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String i : permissions) {

            if (ActivityCompat.checkSelfPermission(this, i) != PackageManager.PERMISSION_GRANTED) {
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

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //TODO: AVVISARE SUCCESSO AVVIO RICERCA
            }

            @Override
            public void onFailure(int reason) {
                //TODO: AVVISARE FALLIMENTO AVVIO RICERCA
                //si ricade qui principalmente perchè non è acceso il WIFI, bisogna vedere se mettere un avviso prima oppure avvisare e dare la possibilità di far ripartire la ricerca senza rifare la room!
            }
        });


    }

    public void onPeersConnectedChangeView(Collection<WifiP2pDevice> peersName) {
        int i = 1;
        for (WifiP2pDevice p : peersName) {
            MasterCreationActivity.staticPeers.get(i).setText(p.deviceName);
            MasterCreationActivity.staticPeers.get(i).setVisibility(View.VISIBLE);
            i++;
        }
    }


    private void startXML(ArrayList<TextView> peers, int peerNum, ArrayList<ProgressBar> loaders) {

        staticPeers = new ArrayList<>();

        peerL1 = findViewById(R.id.progressBarPeer1);
        peerL1.setIndeterminate(true);
        loaders.add(peerL1);
        peer1 = findViewById(R.id.peer1);
        peers.add(peer1);
        MasterCreationActivity.staticPeers.add(peer1);

        peerL2 = findViewById(R.id.progressBarPeer2);
        peerL2.setIndeterminate(true);
        loaders.add(peerL2);
        peer2 = findViewById(R.id.peer2);
        peers.add(peer2);
        MasterCreationActivity.staticPeers.add(peer2);

        peerL3 = findViewById(R.id.progressBarPeer3);
        peerL3.setIndeterminate(true);
        loaders.add(peerL3);
        peer3 = findViewById(R.id.peer3);
        peers.add(peer3);
        MasterCreationActivity.staticPeers.add(peer3);

        peerL4 = findViewById(R.id.progressBarPeer4);
        peerL4.setIndeterminate(true);
        loaders.add(peerL4);
        peer4 = findViewById(R.id.peer4);
        peers.add(peer4);
        MasterCreationActivity.staticPeers.add(peer4);

        peerL5 = findViewById(R.id.progressBarPeer5);
        peerL5.setIndeterminate(true);
        loaders.add(peerL5);
        peer5 = findViewById(R.id.peer5);
        peers.add(peer5);
        MasterCreationActivity.staticPeers.add(peer5);

        peerL6 = findViewById(R.id.progressBarPeer6);
        peerL6.setIndeterminate(true);
        loaders.add(peerL6);
        peer6 = findViewById(R.id.peer6);
        peers.add(peer6);
        MasterCreationActivity.staticPeers.add(peer6);

        for (int i = 5; i >= peerNum; i--) { //Mettiamo tutti su invisible tranne il primo, quando si collega il prossimo peer allora cambiamo nome e settiamo come visibile
            peers.get(i).setVisibility(View.INVISIBLE);
            loaders.get(i).setVisibility(View.INVISIBLE);
        }
    }


    /*********************************************************************************************************/

    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //devo usare write quando voglio inviare dati poichè fa la write sul outputstream del socket di connessione
        public void myWrite(byte[] bytes) throws IOException {
            outputStream.write(bytes);
        }
    }

    /*********************************************************************************************************/

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                socketCollection.add(sendReceive);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*********************************************************************************************************/

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            byte[] readBuff = (byte[]) msg.obj;
            String returnedMsg = new String(readBuff, 0, msg.arg1);
            //qui ricevo il messaggio alla prima connessione e conterrà il codice della room e nickname scelto dal peer che si collega
            //il nickname sarà usato anche per salvare la corrispondenza fra socket e peer connesso
            String[] dataPeers = returnedMsg.split("//");

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

