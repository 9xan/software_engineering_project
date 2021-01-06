package com.example.provaapp.operative_activity_changer_1;

import android.Manifest;
import android.os.Bundle;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.Permissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> permissions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarActivityChanger);
        setSupportActionBar(toolbar);


        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.BLUETOOTH);
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.RECORD_AUDIO);

        String[] st = new String[9];

        Permissions.requestPermissions(this, permissions.toArray(st), 100);
        //Permissions.requestPermissions(this, new String[]{}, 101);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}