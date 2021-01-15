package com.example.provaapp.mode_join_2_0;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;

import java.util.Objects;

public class RoomInfoActivity extends AppCompatActivity {

    private Bundle args;
    private String[] qrData;
    private TextView textRoom1, textVideo1, textAudio1, textSecCode1, textMacMaster1;
    private Button next;
    public static final String NextToJoinKey = "NextToJoin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_info_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbarRoomInfo);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        args = intent.getBundleExtra(QRReaderActivity.NextToRoomInfoKey);
        next = findViewById(R.id.NextToJoin);

        textRoom1 = findViewById(R.id.textRoom1);
        textVideo1 = findViewById(R.id.textVideo1);
        textAudio1 = findViewById(R.id.textAudio1);
        textSecCode1 = findViewById(R.id.textSecCode1);
        textMacMaster1 = findViewById(R.id.textMacMaster1);

        qrData = Objects.requireNonNull(args.getString("QRData")).split("//", 0);
        textRoom1.setText(qrData[0]);
        textSecCode1.setText(qrData[1]);
        textMacMaster1.setText(qrData[2]);
        textVideo1.setText(qrData[3]);
        textAudio1.setText(qrData[4]);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(args, NextToJoinKey, JoinActivity.class);
            }
        });
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
