package com.example.provaapp.mode_create_2_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.provaapp.R;

public class CreateRoomStep4 extends Fragment {

    public ProgressBar step;
    public TextView textAudio;
    public TextView textVideo;
    public TextView textRoomName;
    public TextView textMasterRole;
    public Bundle args;
    public Button complete;
    public Button prev;
    public static final String RoomData = "RoomData";


    public CreateRoomStep4() {
        super(R.layout.create_room_step_4);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        final FragmentManager childFragmentManager = getParentFragmentManager();
        args = this.getArguments();

        if (savedInstanceState == null) {

            textAudio = view.findViewById(R.id.TextEffectiveAudio);
            textAudio.setText(((Integer) args.getInt("audioN")).toString());

            textVideo = view.findViewById(R.id.TextEffectiveVideo);
            textVideo.setText(((Integer) args.getInt("videoN")).toString());

            textRoomName = view.findViewById(R.id.TextEffectiveRoomName);
            textRoomName.setText(args.getString("RoomName"));

            textMasterRole = view.findViewById(R.id.TextEffectiveMasterRole);
            textMasterRole.setText(args.getString("masterRole"));


            step = view.findViewById(R.id.progressBar4);
            prev = view.findViewById(R.id.LastBack);
            complete = view.findViewById(R.id.LastNext);
            step.setMax(4);
            step.setProgress(4);
        }

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(" VISUALIZZAZIONE DATI NELL'ARGOMENTO 1:", "videoN -> " + args.getString("videoN") + " audioN -> " + args.getString("audioN") +
                        " masterRole -> " + args.getString("masterRole") + " RoomName -> " + args.getString("RoomName") + " MyNick -> " + args.getString("NickName"));

                sendMessage(args, RoomData, QRCreationActivity.class);
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

    public void sendMessage(Bundle args, String Key, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(getContext(), nextActivity);
        intent.putExtra(Key, args);
        startActivity(intent);
    }

}
