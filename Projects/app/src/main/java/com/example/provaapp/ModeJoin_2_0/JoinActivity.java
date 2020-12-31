package com.example.provaapp.ModeJoin_2_0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.P2PClientConnection;

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

    public ProgressBar pr;
    public TextView connectionText;
    private String[] qrData;
    private String myNicknameDevice;
    public Button connectBtn;


    public P2PClientConnection connection;
    private ArrayList<String> permissions = new ArrayList<>();
    private Bundle args;

    /*********************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);
        Toolbar toolbar = findViewById(R.id.toolbarJoin);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        args = intent.getBundleExtra(RoomInfoActivity.NextToJoinKey);
        myNicknameDevice = args.getString("NickName");

        assert args != null;
        //Log.d("nel bundle della join c'è ", Objects.requireNonNull(args.getString("QRData")));
        qrData = Objects.requireNonNull(args.getString("QRData")).split("//", 0);
        int i = 0;
        for (String s : qrData) {
            Log.d("contenuto" + i, s);
            i++;
        }


        //ADDING PERMISSIONS
        this.permissions.addAll(Arrays.asList(permissionsList));

        // Capture the layout's TextView and set the string as its text
        TextView NickViewJoin = findViewById(R.id.NickViewJoin);
        NickViewJoin.setText(myNicknameDevice);
        connectBtn = findViewById(R.id.connectBtn);

        pr = findViewById(R.id.progressBarConnection);
        connectionText = findViewById(R.id.connectionText);
        connectionText.setText("Ricerca stanza -" + qrData[0]+"-");

        connection = new P2PClientConnection(this, myNicknameDevice, qrData[1]);

        connection.startDiscovery();



    }

    /*********************************************************************************************************/

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void sendMessage(Bundle s, String Key, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        intent.putExtra(Key, s);
        startActivity(intent);
    }


}


