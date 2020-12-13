package com.example.provaapp.ModeCreate_2_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.OperativeActivityChanger_1.FirstFragment;
import com.example.provaapp.R;

public class QRCreationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_creation_activity);
        Toolbar toolbar = findViewById(R.id.QRToolbar);
        setSupportActionBar(toolbar);
        TextView RoomName = findViewById(R.id.RoomNameView);

        Intent intent = getIntent();
       /* String message = intent.getStringExtra(CreateRoomStep2.Room);
        RoomName.setText(message);*/
    }


}
