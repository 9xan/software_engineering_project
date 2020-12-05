package com.example.provaapp.OperativeActivityChanger_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.provaapp.ModeJoin_2_0.JoinActivity;
import com.example.provaapp.R;
import com.google.android.material.snackbar.Snackbar;

public class FirstFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "JoinNickName";

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
               String NickName = NickNameInput.getText().toString();

                /*Snackbar.make(view, "Joino con nome: " + NickName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                sendMessage(NickName);
            }
        });

        /*Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String NickName = NickNameInput.getText().toString();
                Snackbar.make(view, "Creo una stanza e il mio nome è: "+  NickNameInput.getText(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    /** Called when the user taps the Send button */
    public void sendMessage(String s) {
        Intent intent = new Intent(getContext() , JoinActivity.class);
        intent.putExtra(EXTRA_MESSAGE, s);
        startActivity(intent);
    }
}