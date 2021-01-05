package com.example.provaapp.mode_create_2_1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.provaapp.R;

public class CreateRoomStep3 extends Fragment {

    public Button next;
    public ProgressBar step;
    public EditText roomName;
    public Bundle args;
    public Button prev;



    public CreateRoomStep3() {
        super(R.layout.create_room_step_3);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        final FragmentManager childFragmentManager = getParentFragmentManager();
        args = this.getArguments();

        if (savedInstanceState == null) {
            roomName = view.findViewById(R.id.editTextRoomName);
            step = view.findViewById(R.id.progressBar3);
            step.setMax(4);
            step.setProgress(3);
            prev = view.findViewById(R.id.Back3);
            next = view.findViewById(R.id.Next3);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roomName.getText().toString().compareTo("") == 0 || roomName.getText().toString().compareTo(" ") == 0) {
                    Toast.makeText(getContext(),
                            "Please select a Room Name",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    args.putString("RoomName", roomName.getText().toString());
                    //Log.d("Step3 -> Ho premuto next e ho letto dall'EditText :" , args.getString("RoomName"));

                    CreateRoomStep4 nextFragment = new CreateRoomStep4();
                    nextFragment.setArguments(args);
                    childFragmentManager.beginTransaction()
                            .replace(R.id.room_parent_fragment_container, nextFragment)
                            .setReorderingAllowed(true)
                            .addToBackStack("step3") // name can be null
                            .commit();
                }

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recover first values
                args.putInt("audioN", args.getInt("R_audioN"));
                args.putInt("videoN", args.getInt("R_audioN"));
                childFragmentManager.popBackStack();
            }
        });

    }


}
