package com.example.cameraxtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);

        Intent receivedIntent = getIntent();
        Uri receivedData = receivedIntent.getData();

        MediaController mediaController = new MediaController(this); // manages pause, playback of video views
        VideoView videoView = findViewById(R.id.videoView);

        // make MediaController and VideoView aware of each other (a listener on viewView is set that displays the overlay when tapping)
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // set video resource and start video
        videoView.setVideoURI(receivedData);
        videoView.start();
    }
}