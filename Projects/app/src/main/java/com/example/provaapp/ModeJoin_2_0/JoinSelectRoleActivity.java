package com.example.provaapp.ModeJoin_2_0;

import android.Manifest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.provaapp.R;
import com.example.provaapp.UsefulClasses.Permissions;

public class JoinSelectRoleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_select_role_activity);
        Toolbar toolbar = findViewById(R.id.JoinRoleToolbar);
        //setSupportActionBar(toolbar);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
