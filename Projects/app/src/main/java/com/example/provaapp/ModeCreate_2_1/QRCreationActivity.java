package com.example.provaapp.ModeCreate_2_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.AppSecurity;
import com.example.provaapp.UsefulClasses.RandomString;
import com.google.zxing.WriterException;

import java.security.NoSuchAlgorithmException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class QRCreationActivity extends AppCompatActivity {

    public String roomNameQR;
    public String myDeviceID = "nexus5x";
    private String QRString;
    private String secCode;
    private String hashSecCode;
    private final String algorithm = "SHA-512";
    public ImageView QrView;
    public TextView RoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_creation_activity);

        Toolbar toolbar = findViewById(R.id.QRToolbar);
        setSupportActionBar(toolbar);

        QrView = findViewById(R.id.QrView);
        RoomName = findViewById(R.id.RoomNameView);
        Intent intent = getIntent();
        Bundle message = intent.getBundleExtra(CreateRoomStep4.RoomData); //tutti i dati della stanza sono qui
        assert message != null;
        RoomName.setText(message.getString("NickName", "an error has occurred"));
        roomNameQR = message.getString("RoomName");

        RandomString rs = new RandomString(10);  //generating a secure random string
        secCode = rs.nextString();


        try {     //generating hash starting from a random string
            hashSecCode = AppSecurity.StringToHashSHA512(secCode, algorithm);
            QRString = roomNameQR + "|" + hashSecCode + "|" + myDeviceID + "|" + new RandomString(300).nextString();
            QRGEncoder qrgEncoder = new QRGEncoder(QRString, null, QRGContents.Type.TEXT, QRString.length());
            Bitmap QRbits = qrgEncoder.getBitmap();
            QrView.setImageBitmap(QRbits);
        } catch (NoSuchAlgorithmException nsae) {
            Log.d("error generating hashcode ", nsae.toString());
        }

    }


}
