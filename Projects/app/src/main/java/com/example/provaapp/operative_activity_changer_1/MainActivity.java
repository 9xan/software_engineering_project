package com.example.provaapp.operative_activity_changer_1;

import android.Manifest;
import android.os.Bundle;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.Permissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarActivityChanger);
        setSupportActionBar(toolbar);
        Permissions.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        Permissions.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}