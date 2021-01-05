package com.example.provaapp.mode_join_2_0;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.example.provaapp.operative_activity_changer_1.FirstFragment;
import com.example.provaapp.R;
import com.google.zxing.Result;


public class QRReaderActivity extends AppCompatActivity {


    private CodeScanner mCodeScanner;
    public CodeScannerView scannerView;
    private Bundle args;
    public static final String NextToRoomInfoKey = "NextToRoomInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_reader_activity);
        Intent intent = getIntent();
        args = intent.getBundleExtra(FirstFragment.JoinKey);
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        args.putString("QRData", result.getText());
                        sendMessage(args, NextToRoomInfoKey , RoomInfoActivity.class);
                    }
                });
            }
        });
        mCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                Log.d("error opening camera", error.toString());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.stopPreview();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCodeScanner.releaseResources();
        super.onDestroy();
    }

    public void sendMessage(Bundle s, String Key, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        intent.putExtra(Key, s);
        startActivity(intent);
    }
}