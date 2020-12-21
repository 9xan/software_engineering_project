package com.example.provaapp.ModeCreate_2_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.ModeJoin_2_0.JoinActivity;
import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.AppSecurity;
import com.example.provaapp.UsefulClasses.RandomString;
import com.google.zxing.WriterException;

import java.security.NoSuchAlgorithmException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class QRCreationActivity extends AppCompatActivity {

    public String roomNameQR;
    public String myDeviceMAC = "nexus5x";
    private String QRString;
    private String secCode;
    private String hashSecCode;
    private final String algorithm = "SHA-512";
    public ImageView QrView;
    public Button toCreateMaster;
    public TextView RoomName;
    public Bundle message;
    public static final String forMasterCreation = "forMasterCreation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_creation_activity);

        Toolbar toolbar = findViewById(R.id.QRToolbar);
        setSupportActionBar(toolbar);

        toCreateMaster = findViewById(R.id.closeQRButton);
        QrView = findViewById(R.id.QrView);
        RoomName = findViewById(R.id.RoomNameView);
        Intent intent = getIntent();
        message = intent.getBundleExtra(CreateRoomStep4.RoomData); //tutti i dati della stanza sono qui
        assert message != null;
        RoomName.setText(message.getString("NickName", "an error has occurred"));
        roomNameQR = message.getString("RoomName");

        RandomString rs = new RandomString(10);  //generating a secure random string
        secCode = rs.nextString();

        try {     //generating hash starting from a random string
            hashSecCode = AppSecurity.StringToHashSHA512(secCode, algorithm);
            QRString = roomNameQR + "//" + hashSecCode + "//" + myDeviceMAC + "//" + new RandomString(300).nextString();
            QRGEncoder qrgEncoder = new QRGEncoder(QRString, null, QRGContents.Type.TEXT, QRString.length());
            Bitmap QRbits = qrgEncoder.getBitmap();
            QrView.setImageBitmap(QRbits);

        } catch (NoSuchAlgorithmException nsae) {
            Log.d("error generating hashcode ", nsae.toString());
        }

        toCreateMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Log.d(" VISUALIZZAZIONE DATI NELL'ARGOMENTO 2 : " ,  "videoN -> " + message.getInt("videoN") + " audioN -> " +message.getInt("audioN") +
                        " masterRole -> " + message.getString("masterRole") + " RoomName -> " + message.getString("RoomName") + " MyNick -> "+ message.getString("NickName"));*/
                message.putString("secureHash" , hashSecCode);
                message.putString("myDeviceMAC" , myDeviceMAC);
                sendMessage(message , forMasterCreation , MasterCreationActivity.class);

            }
        });

    }


    public void sendMessage(Bundle s, String Key, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this , nextActivity);
        intent.putExtra(Key, s);
        startActivity(intent);
    }

}
