package com.example.provaapp.operative_activity_changer_1;

import android.Manifest;
import android.os.Bundle;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.Permissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String appMediaFolderPath = Environment.getExternalStorageDirectory() + "/DCIM/multi_rec/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarActivityChanger);
        setSupportActionBar(toolbar);
        createStorageDir();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permissions.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void createStorageDir() {
        //create folder
        File file = new File(appMediaFolderPath);
        if (!file.mkdirs()) {
            file.mkdirs();
        }
        String filePath = file.getAbsolutePath() + File.separator;
    }
}

