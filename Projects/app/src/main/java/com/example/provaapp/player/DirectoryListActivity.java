package com.example.provaapp.player;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.provaapp.R;
import com.example.provaapp.mode_create_2_1.ManagerShareActivity;
import com.example.provaapp.operative_activity_changer_1.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class DirectoryListActivity extends AppCompatActivity {

    /* TODO modify dirPath to change source directory*/
    final String dirPath = MainActivity.mainFolderPath;
    ListView myListView;
    private Bundle args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_list_activity);
        myListView = findViewById(R.id.myDirListView);

        List<String> filePaths = MediaHandler.getFilesNameFromDirPath(dirPath);
        String[] paths = filePaths.toArray(new String[0]);

        if (filePaths.size() == 0) {
            Toast.makeText(getApplicationContext(), "No such directory", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        } else {

            ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item1,
                    R.id.myTextView1, paths);
            myListView.setAdapter(myAdapter);

            myListView.setOnItemClickListener((parent, view, position, id) -> {
                String itemValue = (String) myListView.getItemAtPosition(position);
                args = new Bundle();
                args.putString("dirPath", dirPath + itemValue+"/");
                switchActivity(args, FileListActivity.class);
            });
        }

    }

    public void switchActivity(Bundle b, Class<? extends AppCompatActivity> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        intent.putExtras(b);
        startActivity(intent);
    }
}
