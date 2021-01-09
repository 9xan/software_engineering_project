package com.example.provaapp.operative_activity_changer_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.provaapp.mode_create_2_1.CreateActivity;
import com.example.provaapp.mode_join_2_0.QRReaderActivity;
import com.example.provaapp.R;
import com.example.provaapp.player.FileListActivity;

public class FirstFragment extends Fragment {

    public static final String JoinKey = "JoinNickName";
    public static final String CreateKey = "CreateNickName";
    public String nickName;
    private Bundle args;
    private Button join, create, openPlayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        join = view.findViewById(R.id.Join);
        openPlayer = view.findViewById(R.id.OpenPlayer);
        create = view.findViewById(R.id.Create);
        args = new Bundle();
        final EditText NickNameInput = view.findViewById(R.id.NickNameInput);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nickName = NickNameInput.getText().toString();
                if (nickName.compareTo("") == 0) {
                    Toast.makeText(getContext(),
                            "Please insert a name!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    args.putString("NickName", nickName);
                    sendMessage(args, JoinKey, QRReaderActivity.class);
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickName = NickNameInput.getText().toString();


                if (nickName.compareTo("") == 0) {
                    Toast.makeText(getContext(),
                            "Please insert a name!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    args.putString("NickName", nickName);
                    sendMessage(args, CreateKey, CreateActivity.class);
                }

            }
        });

        openPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(null, "", FileListActivity.class);
            }
        });

    }

    public void sendMessage(Bundle s, String Key, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(getContext(), nextActivity);
        if (s != null) {
            intent.putExtra(Key, s);
        }
        startActivity(intent);
    }

}