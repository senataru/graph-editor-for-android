package com.example.graph_editor.extentions;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.extentions.model.Extension;

import java.util.List;

public class ExtensionsActivity extends AppCompatActivity {
    RecyclerView installedExtensions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extensions);
        installedExtensions = findViewById(R.id.installedExtensions);
    }
    @Override
    protected void onResume() {
        super.onResume();
        installedExtensions.setAdapter(new ExtensionsRecyclerViewAdapter(this, hardcoded));
    }

    private static final List<Extension> hardcoded = List.of(
            new Extension("UIPlugin", "file1.js", true),
            new Extension("PushRelabelPlugin", "file2.js", false)
    );
}
