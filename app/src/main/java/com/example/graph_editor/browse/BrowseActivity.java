package com.example.graph_editor.browse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.graph_editor.R;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SavesDatabase;

import java.util.Arrays;

public class BrowseActivity extends AppCompatActivity {
    RecyclerView saved;
    SavesDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        saved = findViewById(R.id.saved);
        database = SavesDatabase.getDbInstance(getApplicationContext());
        SavedAdapter adapter = new SavedAdapter(this,
                database.saveDao().getAllScores());
        saved.setAdapter(adapter);
        saved.setLayoutManager(new LinearLayoutManager(this));
    }
}