package com.example.provaapp.mode_create_2_1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.provaapp.R;

public class CreateRoomStep2 extends Fragment {

    public Button next;
    public RadioGroup radioGroup;
    public int videoN;
    public int audioN;
    public ProgressBar step;
    public String checkedRadio;
    public Bundle args;
    public Button prev;


    public CreateRoomStep2() {
        super(R.layout.create_room_step_2);
    }


    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        args = this.getArguments();

        final FragmentManager childFragmentManager = getParentFragmentManager();
        if (savedInstanceState == null) {
            radioGroup = view.findViewById(R.id.radioGroup);
            step = view.findViewById(R.id.progressBar2);
            step.setMax(4);
            step.setProgress(2);
            prev = view.findViewById(R.id.Back2);
            next = view.findViewById(R.id.Next2);
        }

        assert args != null;
        videoN = args.getInt("videoN");
        audioN = args.getInt("audioN");

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            checkedRadio = ((RadioButton) view.findViewById(checkedId)).getText().toString();
            Log.d("step2 -> radio selected :", checkedRadio);
        });

        //for the slaves connection
        next.setOnClickListener(view1 -> {
            if (checkedRadio != null) {
                switch (checkedRadio) {
                    case "Video Recorder":
                        videoN -= 1;
                        args.putString("masterRole", "Video Recorder");
                        args.putInt("videoN", videoN);
                        break;
                    case "Audio Recorder":
                        audioN -= 1;
                        args.putString("masterRole", "Audio Recorder");
                        args.putInt("audioN", audioN);
                        break;
                    case "None":
                        args.putString("masterRole", "None");
                        break;
                }
                Log.d("My Values", args.toString());
                CreateRoomStep3 nextFragment = new CreateRoomStep3();
                nextFragment.setArguments(args);
                childFragmentManager.beginTransaction()
                        .replace(R.id.room_parent_fragment_container, nextFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("step2") // name can be null
                        .commit();
            } else {
                Toast.makeText(getContext(),
                        "Please select a function",
                        Toast.LENGTH_SHORT)
                        .show();
            }

        });

        prev.setOnClickListener(v -> childFragmentManager.popBackStack());

    }

}
