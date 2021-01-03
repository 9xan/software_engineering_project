package com.example.provaapp.ModeCreate_2_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.P2PManagerNearby;
import com.example.provaapp.UsefulClasses.P2PWorkerNearby;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

public class ReadyToRecordActivity extends AppCompatActivity {


    private Button begin;
    private long tsLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_to_record_activity);
        Toolbar toolbar = findViewById(R.id.toolbarReadyToStart);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        begin = findViewById(R.id.BeginBTN);

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsLong = (System.currentTimeMillis() + 5000);
                String ts = Long.toString(tsLong);

                Payload bytesPayload = Payload.fromBytes(("TIMESTAMP-" + ts).getBytes());
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload( P2PManagerNearby.endpoints  ,  bytesPayload);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void sendMessage(Bundle s, String Key, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        intent.putExtra(Key, s);
        startActivity(intent);
    }

}
