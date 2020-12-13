package com.example.provaapp.OperativeActivityChanger_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.provaapp.ModeCreate_2_1.CreateActivity;
import com.example.provaapp.ModeJoin_2_0.JoinActivity;
import com.example.provaapp.R;

public class FirstFragment extends Fragment {

    public static final String JoinKey = "JoinNickName";
    public static final String CreateKey = "CreateNickName";
    public String NickName;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState

    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button Join = view.findViewById(R.id.Join);
        Button Create = view.findViewById(R.id.Create);
        final EditText NickNameInput = view.findViewById(R.id.NickNameInput);

        Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               NickName = NickNameInput.getText().toString();
               sendMessage(NickName , JoinKey , JoinActivity.class);
            }
        });

        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NickName = NickNameInput.getText().toString();
                /*Snackbar.make(view, "Creo una stanza e il mio nome è: "+  NickNameInput.getText(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                sendMessage(NickName , CreateKey , CreateActivity.class);
            }
        });
    }

    /** Called when the user taps the Send button */
    public void sendMessage(String s , String Key , Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(getContext() , nextActivity);
        intent.putExtra(Key, s);
        startActivity(intent);
    }
}