package com.example.provaapp.ModeCreate_2_1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.P2PManagerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class WaitForPeerConfigurationActivity extends AppCompatActivity {

    public TextView audioView, videoView, finishView,roomView;
    public Button finishBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_peer_configuration_activity);
        //Toolbar toolbar = findViewById(R.id.JoinRoleToolbar);
        //setSupportActionBar(toolbar);


        audioView = findViewById(R.id.audioManagerView);
        videoView = findViewById(R.id.videoManagerView);
        finishView = findViewById(R.id.finishView);
        finishBtn = findViewById(R.id.finishConfView);
        roomView = findViewById(R.id.roomManagerView);
        roomView.setText(P2PManagerNearby.room);
        audioView.setText(P2PManagerNearby.audioN);
        videoView.setText(P2PManagerNearby.videoN);


        //quando si è qua posso fermare l advertising del manager in quanto tutti si sono connessi e non manca nessuno
        Nearby.getConnectionsClient(getApplicationContext()).stopAdvertising();


        // ci vuole qualcosa di simile a setIntervall di Js per mandare ai peers i posti disponibili, praticamente il manager condivide ogni tot i posti disponibili a tutti i workers
        //poi deve fermarsi appena tutti hanno scelto e
        {

            String s = String.valueOf(P2PManagerNearby.videoN)+"-"+String.valueOf(P2PManagerNearby.audioN);
            Payload mes = Payload.fromBytes(s.getBytes());

            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(P2PManagerNearby.endpoints, mes);

            //if(finito){ finished(); }
        }


    }

    public void finished(){
        finishBtn.setClickable(true);
        finishBtn.setVisibility(View.VISIBLE);
        finishView.setVisibility(View.VISIBLE);
    }










    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}