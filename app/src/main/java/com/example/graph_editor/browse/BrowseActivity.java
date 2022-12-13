package com.example.graph_editor.browse;

import static com.example.graph_editor.menu.SharedPrefNames.CURRENT_GRAPH;
import static com.example.graph_editor.menu.SharedPrefNames.CURRENT_GRAPH_ID;
import static com.example.graph_editor.menu.SharedPrefNames.EDGE_PROPERTIES;
import static com.example.graph_editor.menu.SharedPrefNames.VERTEX_PROPERTIES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.DrawActivity;

import java.util.HashSet;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {
    private RecyclerView saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        saved = findViewById(R.id.saved);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SavesDatabase database = SavesDatabase.getDbInstance(getApplicationContext());
        SavedAdapter adapter = new SavedAdapter(this, database.saveDao().getAllSaves(),
                database.vertexPropertySaveDao().getAllPropertySaves(),
                database.edgePropertySaveDao().getAllPropertySaves(), this);
        saved.setAdapter(adapter);
        saved.setLayoutManager(new LinearLayoutManager(this));

        TextView noSavedGraphs = findViewById(R.id.txt_no_saved_graphs);
        if (adapter.getData().size() > 0) noSavedGraphs.setAlpha(0f);
    }

    public void changeActivity(String graphString, long graphId,
                               List<String> vertexPropertyStrings, List<String> edgePropertyStrings) {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CURRENT_GRAPH, graphString);
        editor.putLong(CURRENT_GRAPH_ID, graphId);
        editor.putStringSet(VERTEX_PROPERTIES, new HashSet<>(vertexPropertyStrings));
        editor.putStringSet(EDGE_PROPERTIES, new HashSet<>(edgePropertyStrings));
        editor.apply();

        Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);
    }
}