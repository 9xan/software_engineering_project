package com.example.provaapp.ModeCreate_2_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.R;


public class QRCreationActivity extends AppCompatActivity {

    public String roomNameQR;
    public String myDeviceID;
    private String secCode;
    private String hashSecCode;

   /* public void HashingSha256FromString(String secretCode) {

        final HashFunction hashFunction = Hashing.sha256();
        final HashCode hc = hashFunction
                .newHasher()
                .putString(secretCode, Charsets.UTF_8)
                .hash();
        final String sha256 = hc.toString();

        Log.d("Hascode" , sha256);
        Log.d("from code:" , secretCode);
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_creation_activity);
        Toolbar toolbar = findViewById(R.id.QRToolbar);
        setSupportActionBar(toolbar);
        TextView RoomName = findViewById(R.id.RoomNameView);

        Intent intent = getIntent();
        Bundle message = intent.getBundleExtra(CreateRoomStep4.RoomData); //tutti i dati della stanza sono qui

        //Log.d("QR GENERATION PAGE DATA ->" , message.getString("RoomName"));
        RoomName.setText(message.getString("NickName" , "an error has occurred"));
        //HashingSha256FromString("ciaociao");
    }


}
