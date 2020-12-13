package com.example.provaapp.ModeCreate_2_1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.provaapp.ModeJoin_2_0.JoinActivity;
import com.example.provaapp.R;

public class CreateRoomStep1 extends Fragment {

    public ProgressBar step;
    public SeekBar seekbarVideo;
    public SeekBar seekbarAudio;
    public Button next;
    public int videoN;
    public int audioN;

    public CreateRoomStep1() {
        super(R.layout.create_room_step_1);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        final FragmentManager childFragmentManager = getParentFragmentManager();
        //this.RoomName = view.findViewById(R.id.editTextTextEmailAddress);
        if(savedInstanceState == null){

            //int someInt = requireArguments().getInt("2"); //get arguments from activity
            next = view.findViewById(R.id.Next1);
            step = view.findViewById(R.id.progressBar);
            step.setMax(4);
            step.setProgress(1);
            seekbarVideo = view.findViewById(R.id.seekBarVideo);
            seekbarAudio = view.findViewById(R.id.seekBarAudio);
        }


        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d("progress", ((Integer) progress).toString());
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
                Bundle nextStep = new Bundle();
                Log.d("step1 -> VideoN letto :" , ((Integer) videoN).toString());
                Log.d("step1 -> AudioN letto :" , ((Integer) audioN).toString());

                nextStep.putInt("videoN" , videoN);
                nextStep.putInt("audioN" , audioN);
                CreateRoomStep2 nextFragment = new CreateRoomStep2();
                nextFragment.setArguments(nextStep);
                childFragmentManager.beginTransaction()
                        .replace(R.id.room_parent_fragment_container, nextFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
            }
        });

    }


}
