package com.example.provaapp.player;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.provaapp.R;
import com.example.provaapp.mode_create_2_1.ManagerShareActivity;
import com.example.provaapp.operative_activity_changer_1.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaPlayerActivity extends AppCompatActivity {

    MediaPlayer musicPlayer;
    String mp3TrackPath;
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
        setContentView(R.layout.activity_mediaplayer);

        Intent myIntent = getIntent();

        // get video path from the bundle received from FileListActivity
        List<String> filePaths = myIntent.getStringArrayListExtra("paths");

        // FIXME currentModification while removing elements
        for (Iterator<String> iterator = filePaths.iterator(); iterator.hasNext(); ) {
            String p = iterator.next();
            if (MediaHandler.isInFormat(p, "mp3")) {
                mp3TrackPath = p;
                iterator.remove();
                Log.d("info", mp3TrackPath);
            }
        }

        Log.d("info", filePaths.toString());

        videoViewPaths = filePaths;

        // initialize the right fragment given the number of videos
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
                // if videoviews doesn't exist
                if (videoViews == null) {
                    // create videoviews
                    videoViews = MediaHandler.createVideoViews(ctx, videoViewIds, videoViewPaths, null);
                }
                if (musicPlayer == null) {
                    if (mp3TrackPath != null) {
                        musicPlayer = MediaPlayer.create(ctx, Uri.parse(mp3TrackPath));
                        musicPlayer.setVolume(100, 100);
                    }
                }
                // if videos are playing
                if (MediaHandler.areVideosPlaying(videoViews)) {
                    // stop videos
                    if (musicPlayer != null) {
                        musicPlayer.pause();
                    }
                    MediaHandler.stopVideoViews(videoViews);
                } else {
                    // else proceed to start
                    if (musicPlayer != null) {
                        musicPlayer.start();
                    }
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
                Intent i = new Intent(getApplicationContext(), FileListActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if(musicPlayer != null){
                    musicPlayer.stop();
                }
                startActivity(i);
                finish();
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
}