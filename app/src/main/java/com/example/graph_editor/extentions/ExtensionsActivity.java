package com.example.graph_editor.extentions;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;

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
        installedExtensions.setAdapter(new ExtensionsRecyclerViewAdapter(
                this,
                ExtensionsProvider.provideExtensions())
        );
    }
}
