package com.example.provaapp.ModeJoin_2_0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.OperativeActivityChanger_1.MainActivity;
import com.example.provaapp.R;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);
        Toolbar toolbar = findViewById(R.id.toolbarJoin);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String message = intent.getStringExtra(FirstFragment.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView NickNameViewer = findViewById(R.id.NickNameViewer);
        NickNameViewer.setText(message);

    }


}