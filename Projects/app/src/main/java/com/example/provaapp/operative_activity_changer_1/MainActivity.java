package com.example.provaapp.operative_activity_changer_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.provaapp.R;
import com.example.provaapp.useful_classes.P2PManagerNearby;
import com.example.provaapp.useful_classes.Permissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String mainFolderPath = Environment.getExternalStorageDirectory() + "/DCIM/multi_rec/";
    private List<String> permList;
    private String[] permArr = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbarActivityChanger);
        setSupportActionBar(toolbar);
        permList = Arrays.asList(permArr);
        ActivityCompat.requestPermissions(this, new String[]{permArr[0]}, 100);
    }

    public static void createStorageDir(String path) {
        //create folder
        File file = new File(path);
        if (!file.mkdirs()) {
            file.mkdirs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[1]}, 101);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 101:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[2]}, 102);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 102:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[3]}, 103);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 103:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[4]}, 104);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 104:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[5]}, 105);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 105:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[6]}, 106);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 106:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[7]}, 107);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 107:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[8]}, 108);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 108:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[9]}, 109);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 109:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permArr[10]}, 110);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case 110:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

