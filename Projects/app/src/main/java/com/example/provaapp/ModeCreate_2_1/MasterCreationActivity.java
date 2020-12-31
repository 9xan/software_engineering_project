package com.example.provaapp.ModeCreate_2_1;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.P2PStarConnection;

import java.util.ArrayList;
import java.util.Arrays;

//TODO: REINSERIRE QUESTO CODICE ->
//QUANDO TI SI CONNETTONO TUTTI I DISPOSITIVI :

           /* if (setReadyDevices == 0) {
                    finishButton.setVisibility(View.VISIBLE);
                    finishButton.setClickable(true);
                    }*/
//QUANDO TI SI CONNETTE UN SINGOLO DISPOSITIVO:

                /*if(tmpCounter == peerNumber) {
                        finishButton.setVisibility(View.VISIBLE);
                        finishButton.setClickable(true);
                        }*/

                /*if(tmpCounter <= peerNumber){
                *       staticPeers.get(peersCounter).setText("Ready");
                        peerLoaders.get(peersCounter).setVisibility(View.INVISIBLE);
                        * if(tmpCounter == peerNumber){
                            finishButton.setVisibility(View.VISIBLE);
                            finishButton.setClickable(true);
                        * }
                        tmpCounter++;
                        peersCounter++;
                        *
                * }
                * */
   /* for (String i : permissions) {

            if (ActivityCompat.checkSelfPermission(this, i) != PackageManager.PERMISSION_GRANTED) {
                // PERMISSIONS
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }*/

public class MasterCreationActivity extends AppCompatActivity {


    public Bundle message;

    public TextView peer1, peer2, peer3, peer4, peer5, peer6;
    public TextView myName;
    public ArrayList<TextView> peers;
    public static ArrayList<TextView> staticPeers;
    public Button finishButton;
    public ProgressBar peerL1, peerL2, peerL3, peerL4, peerL5, peerL6;
    public ArrayList<ProgressBar> peerLoaders;
    private String masterRole;
    public int tmpCounter = 1, peersCounter = tmpCounter - 1;
    public static int peerNumber;
    public P2PStarConnection managerConnection;
    public String myNickName, secureCode;

    public final String[] permissionsList = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private ArrayList<String> permissions = new ArrayList<>();


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

            }
        });

        assert message != null;
        masterRole = message.getString("masterRole");
        peerNumber = message.getInt("audioN") + message.getInt("videoN");
        myName.setText(message.getString("RoomName"));

        peers = new ArrayList<>();
        peerLoaders = new ArrayList<>();

        myNickName = message.getString("NickName");
        secureCode = message.getString("secureHash");

        startXML(peers, peerNumber, peerLoaders); // questa funzione preapara l'interfaccia iniziale

        managerConnection = new P2PStarConnection(this, myNickName, secureCode);

        managerConnection.startAdvertising();

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

