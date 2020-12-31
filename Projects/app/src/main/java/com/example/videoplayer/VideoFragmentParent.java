package com.example.videoplayer;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.*;


public class VideoFragmentParent extends Fragment {
    public VideoFragmentParent() {
        super(R.layout.video_parent_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    }

}
