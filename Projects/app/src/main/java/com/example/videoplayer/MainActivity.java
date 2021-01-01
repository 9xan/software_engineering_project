package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int READ_EXTERNAL_STORAGE_CODE = 101;

    /* TODO modify dirPath to change source directory*/
    final String dirPath = "/storage/emulated/0/DCIM/EpVideos/";
    final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    List<Integer> videoViewIds;
    List<String> videoViewPaths;
    List<VideoView> videoViews;
    Button getMediaButton;
    Button startPlaybackButton;
    Button pausePlaybackButton;
    Button restartPlaybackButton;
    Button backwardPlaybackButton;
    Button forwardPlaybackButton;
    Button switchActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent myIntent = getIntent();

        videoViewPaths = myIntent.getStringArrayListExtra("videospath");

        assert videoViewPaths != null;
        videoViewIds = startVideoFragmentBy(videoViewPaths.size(), R.id.myFragmentContainer);

        getMediaButton = findViewById(R.id.myButton);
        startPlaybackButton = findViewById(R.id.playButton);
        pausePlaybackButton = findViewById(R.id.pauseButton);
        restartPlaybackButton = findViewById(R.id.restartButton);
        backwardPlaybackButton = findViewById(R.id.backwardButton);
        forwardPlaybackButton = findViewById(R.id.forwardButton);
        switchActivityButton = findViewById(R.id.switchActivityButton);

        final AppCompatActivity ctx = this;


        // TODO prepare video automatically instead of pressing button

        getMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ctx, new String[]{permissions[0]}, READ_EXTERNAL_STORAGE_CODE);

                } else {
                    videoViews = MediaCreator.createVideoViews(ctx, videoViewIds, videoViewPaths, null);
                }
            }
        });


        startPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaCreator.startVideoViews(videoViews);
                }
            }
        });

        pausePlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaCreator.stopVideoViews(videoViews);
                }
            }
        });

        restartPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaCreator.restartVideoViews(videoViews);
                }
            }
        });

        backwardPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaCreator.seekVideoViewsBackward(videoViews, 1000);
                }
            }
        });

        forwardPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaCreator.seekVideoViewsForward(videoViews, 1000);
                }
            }
        });

        switchActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), FileListActivity.class);
                startActivity(myIntent);
            }
        });
    }

    public List<Integer> startVideoFragmentBy(int videoN, int containerId) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
        List<Integer> ids = new ArrayList<>();
        switch (videoN) {
            case 2:
                trans.add(containerId, TwoVideoFragment.class, null).commit();
                ids.add(R.id.twoVideo1);
                ids.add(R.id.twoVideo2);
                break;
            case 3:
                trans.add(containerId, ThreeVideoFragment.class, null).commit();
                ids.add(R.id.threeVideo1);
                ids.add(R.id.threeVideo2);
                ids.add(R.id.threeVideo3);
                break;
        }
        return ids;
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