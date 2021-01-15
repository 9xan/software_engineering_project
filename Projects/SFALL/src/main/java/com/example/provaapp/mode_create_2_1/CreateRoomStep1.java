package com.example.provaapp.mode_create_2_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.provaapp.operative_activity_changer_1.MainActivity;
import com.example.provaapp.R;

public class CreateRoomStep1 extends Fragment {

    public ProgressBar step;
    public SeekBar seekbarVideo;
    public SeekBar seekbarAudio;
    public Button next;
    public Button prev;
    public int videoN;
    public int audioN;
    public Bundle nextStep;
    public Context parentActivity;

    public CreateRoomStep1() {
        super(R.layout.create_room_step_1);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        final FragmentManager childFragmentManager = getParentFragmentManager();
        if (savedInstanceState == null) {
            nextStep = this.getArguments();
            next = view.findViewById(R.id.Next1);
            prev = view.findViewById(R.id.Back1);
            step = view.findViewById(R.id.progressBar);
            step.setMax(4);
            step.setProgress(1);
            seekbarVideo = view.findViewById(R.id.seekBarVideo);
            seekbarAudio = view.findViewById(R.id.seekBarAudio);
        }


        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoN = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekbarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioN = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("step1 -> VideoN letto :", ((Integer) videoN).toString());
                Log.d("step1 -> AudioN letto :", ((Integer) audioN).toString());

                if (videoN >= 2 && audioN > 0) {

                    nextStep.putInt("videoN", videoN);
                    nextStep.putInt("R_videoN", videoN);

                    nextStep.putInt("audioN", audioN);
                    nextStep.putInt("R_audioN", audioN);

                    CreateRoomStep2 nextFragment = new CreateRoomStep2();
                    nextFragment.setArguments(nextStep);
                    childFragmentManager.beginTransaction()
                            .replace(R.id.room_parent_fragment_container, nextFragment)
                            .setReorderingAllowed(true)
                            .addToBackStack("step1") // name can be null
                            .commit();
                } else {
                    if (videoN == 0 && audioN > 0) {
                        Toast.makeText(getContext(),
                                "Please select at least two video recorders",
                                Toast.LENGTH_SHORT)
                                .show();
                    } else if (videoN > 2 && audioN == 0) {
                        Toast.makeText(getContext(),
                                "Please select at least one audio recorder",
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(getContext(),
                                "Please select something",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            }
        });

        prev.setOnClickListener(v -> {
            nextStep.clear();
            Intent myIntent = new Intent(v.getContext(), MainActivity.class);
            startActivity(myIntent);
        });

    }


}
