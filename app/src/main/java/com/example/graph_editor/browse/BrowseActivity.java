package com.example.graph_editor.browse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.graph_editor.R;

import java.util.Arrays;

public class BrowseActivity extends AppCompatActivity {
    RecyclerView saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        saved = findViewById(R.id.saved);
        SavedAdapter adapter = new SavedAdapter(this,
                Arrays.asList(new Save("example", 96), new Save("example2", 7312)));
        saved.setAdapter(adapter);
        saved.setLayoutManager(new LinearLayoutManager(this));
    }
}