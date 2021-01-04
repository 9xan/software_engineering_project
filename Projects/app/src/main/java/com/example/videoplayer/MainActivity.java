package com.example.videoplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int READ_EXTERNAL_STORAGE_CODE = 101;

    final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    List<Integer> videoViewIds;
    List<String> videoViewPaths;
    List<VideoView> videoViews;
    Button playPausePlaybackButton;
    Button restartPlaybackButton;
    Button backwardPlaybackButton;
    Button forwardPlaybackButton;
    Button switchActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent myIntent = getIntent();

        // get video path from the bundle received from FileListActivity
        videoViewPaths = myIntent.getStringArrayListExtra("videospath");

        // initialize the right fragment given the number of videos
        assert videoViewPaths != null;
        videoViewIds = startVideoFragmentBy(videoViewPaths.size(), R.id.myFragmentContainer);

        // initialize buttons
        playPausePlaybackButton = findViewById(R.id.playPauseButton);
        restartPlaybackButton = findViewById(R.id.restartButton);
        backwardPlaybackButton = findViewById(R.id.backwardButton);
        forwardPlaybackButton = findViewById(R.id.forwardButton);
        switchActivityButton = findViewById(R.id.switchActivityButton);


        final AppCompatActivity ctx = this;


        // set play/pause button listener. The button will also initialize the video views
        playPausePlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if videoviews don't exist
                if (videoViews == null) {
                    // check for read storage permission
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ctx, new String[]{permissions[0]}, READ_EXTERNAL_STORAGE_CODE);
                    } else {
                        // create videoviews
                        videoViews = MediaHandler.createVideoViews(ctx, videoViewIds, videoViewPaths, null);
                    }
                }
                // if videos are playing
                if (MediaHandler.areVideosPlaying(videoViews)) {
                    // stop videos
                    MediaHandler.stopVideoViews(videoViews);
                } else {
                    // else proceed to start
                    MediaHandler.startVideoViews(videoViews);
                }
            }
        });


        // set restart button listener
        restartPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaHandler.restartVideoViews(videoViews);
                }
            }
        });

        // set backward step button listener
        backwardPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaHandler.seekVideoViewsBackward(videoViews, 1000);
                }
            }
        });

        // set forward step button listener
        forwardPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViews != null) {
                    MediaHandler.seekVideoViewsForward(videoViews, 1000);
                }
            }
        });

        // set return to file list activity button listener
        switchActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), FileListActivity.class);
                startActivity(myIntent);
            }
        });
    }


    // given a number of videos and a fragment container id, this method will start the transaction to
    // the right fragment of the container and return the ids of the video views contained in the
    // fragment
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
            case 4:
                trans.add(containerId, FourVideoFragment.class, null).commit();
                ids.add(R.id.fourVideo1);
                ids.add(R.id.fourVideo2);
                ids.add(R.id.fourVideo3);
                ids.add(R.id.fourVideo4);
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