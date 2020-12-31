package com.example.videoplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    final int READ_EXTERNAL_STORAGE_CODE = 101;

    /* TODO modify dirPath to change source directory*/
    final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    final String dirPath = "/storage/emulated/0/DCIM/EpVideos/";
    ListView myListView;
    List<String> selectedVideosName;
    ArrayList<String> selectedVideosPath;
    Button montageButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileslist);

        final AppCompatActivity ctx = this;

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ctx, new String[]{permissions[0]}, READ_EXTERNAL_STORAGE_CODE);

        } else {

            selectedVideosPath = new ArrayList<>();
            selectedVideosName = new ArrayList<>();

            myListView = findViewById(R.id.myListView);
            montageButton = findViewById(R.id.montageButton);

            List<String> filePaths = MediaCreator.getFilesNameFromDirPath(dirPath);
            String[] paths = filePaths.toArray(new String[0]);

            ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item1,
                    R.id.myTextView1, paths);
            myListView.setAdapter(myAdapter);


            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemValue = (String) myListView.getItemAtPosition(position);
                    MediaCreator.addOrRemoveElement(selectedVideosPath, dirPath + itemValue);
                    if (MediaCreator.addOrRemoveElement(selectedVideosName, itemValue)) {
                        view.setBackgroundColor(0xFF00FF00);
                    } else {
                        view.setBackgroundColor(0xFFFFFFFF);
                    }
                    Log.d("selectedVideos", selectedVideosPath.toString());
                }
            });

            montageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedVideosPath.size() >= 2) {
                        Bundle b = new Bundle();
                        String videosPathKey = "videospath";
                        b.putStringArrayList(videosPathKey, selectedVideosPath);
                        switchActivity(b, MainActivity.class);
                    } else {
                        Toast.makeText(getApplicationContext(), "Not enough videos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    public void switchActivity(Bundle b, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Storage permission granted",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Storage permission denied",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

}
