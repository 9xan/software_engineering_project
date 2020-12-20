package com.example.provaapp.UsefulClasses;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;


public class ExtendsForWifiDirectBroadcastReceiver extends AppCompatActivity {


    public String[] nameDevices; //lista dei NOMI dei peers trovati
    public String[] devices;    //lista dei MAC address dei peers trovati, serve per connettere ad un determinato peer
    public ServerClass serverClass;
    public ClientClass clientClass;
    public SendReceive sendReceive;
    public Collection<SendReceive> serverSocketList;


    public WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            nameDevices = new String[(peers.getDeviceList().size())];
            devices = new String[(peers.getDeviceList().size())];
            int i = 0;
            for (WifiP2pDevice dev : peers.getDeviceList()) {
                nameDevices[i] = dev.deviceName;
                devices[i] = dev.deviceAddress;
                i++;
            }
            //peerListView.setText("peers trovati ("+nameDevices.length+"), lista: "+Arrays.toString(nameDevices));
            //Toast.makeText(getApplicationContext(), "peers trovati ("+nameDevices.length+")", Toast.LENGTH_SHORT).show();
        }
    };


    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            final InetAddress inetAddress = info.groupOwnerAddress;

            if (info.groupFormed && info.isGroupOwner) {
                //connectionInfoView.setText("CONN. OK! SONO GROUP OWNER");
                serverClass = new ServerClass();
                serverClass.start();
            } else if (info.groupFormed) {
                //il peer che deve mandare la foto deve collegarsi al servershish e inviare i bytes
                //qui ci sarà solo UNA CONNESSIONE!!! poichè il collegamento è 1-Server a N-Peers
                //connectionInfoView.setText("CONNECTION OK! SONO 'CLIENT'");
                //connessione riuscita, parte per il client
                clientClass = new ClientClass(inetAddress);
                clientClass.start();
            }
        }
    };


    public WifiP2pManager.GroupInfoListener groupInfoListener = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            Collection<WifiP2pDevice> listClient = group.getClientList();
            String out = "";
            int i = 0;
            for (WifiP2pDevice p : listClient) {
                out += p.deviceName;
                i++;
            }
            //socketView.setText("INFO X OWNER tot peers:" + i + " lista: " + out);
        }
    };


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
                serverSocketList.add(sendReceive);
                ///
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*********************************************************************************************************/

    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                //
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
            String tempMsg = new String(readBuff, 0, msg.arg1);
            //dataView.setText(tempMsg);
            return true;
        }
    });

    /*********************************************************************************************************/

}
