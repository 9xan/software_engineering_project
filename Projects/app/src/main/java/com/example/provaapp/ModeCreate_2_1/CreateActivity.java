package com.example.provaapp.ModeCreate_2_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.R;

public class CreateActivity extends AppCompatActivity { //HOST ACTIVITY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);

        if (savedInstanceState == null) { //ritorna un manager per la creazione di transizioni
            /*Bundle c = new Bundle();
            c.putInt("2" , 3);*/
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.room_creation_fragment_container, RoomParentFragment.class, null)
                    .commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbarCreate);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String message = intent.getStringExtra(FirstFragment.CreateKey);
        // Capture the layout's TextView and set the string as its text
        TextView NickViewCreate = findViewById(R.id.NickViewCreate);
        NickViewCreate.setText(message);



    }
}