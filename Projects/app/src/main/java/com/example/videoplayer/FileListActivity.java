package com.example.videoplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {
    /* TODO modify dirPath to change source directory*/
    final String dirPath = "/storage/emulated/0/DCIM/EpVideos/";
    ListView myListView;
    List<String> selectedVideos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileslist);

        selectedVideos = new ArrayList<>();
        myListView = findViewById(R.id.myListView);

        List<String> filePaths = MediaCreator.getFilesNameFromDirPath(dirPath);
        String[] paths = filePaths.toArray(new String[0]);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item1,
                R.id.myTextView1, paths);
        myListView.setAdapter(myAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) myListView.getItemAtPosition(position);
                if (MediaCreator.addOrRemoveElement(selectedVideos, itemValue)) {
                    view.setBackgroundColor(0xFF00FF00);
                } else {
                    view.setBackgroundColor(0xFFFFFFFF);
                }
                Log.d("selectedVideos", selectedVideos.toString());
            }
        });
    }
}
