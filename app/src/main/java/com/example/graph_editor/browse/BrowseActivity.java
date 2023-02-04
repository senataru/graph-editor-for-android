package com.example.graph_editor.browse;

import static com.example.graph_editor.menu.SharedPrefNames.CURRENT_GRAPH_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.DrawActivity;
import com.example.graph_editor.fs.FSDirectories;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
//        SavesDatabase database = SavesDatabase.getDbInstance(getApplicationContext());
        File savesDirectory = new File(getFilesDir(), FSDirectories.graphsDirectory);
        List<String> names = savesDirectory.mkdirs() ?
                List.of() :
                Arrays.stream(savesDirectory.listFiles()).map(File::getName).collect(Collectors.toList());
        SavedAdapter adapter = new SavedAdapter(this, names);
        saved.setAdapter(adapter);
        saved.setLayoutManager(new LinearLayoutManager(this));

        TextView noSavedGraphs = findViewById(R.id.txt_no_saved_graphs);
        if (adapter.getData().size() > 0) noSavedGraphs.setAlpha(0f);
    }

    public void changeActivity(String graphName) {
        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CURRENT_GRAPH_NAME, graphName);
        editor.apply();

        Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);
    }
}