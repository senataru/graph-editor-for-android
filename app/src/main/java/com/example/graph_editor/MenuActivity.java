package com.example.graph_editor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        System.out.println("elo");

        Button btnBrowse = findViewById(R.id.btnBrowse);
        btnBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(this, BrowseActivity.class);
            startActivity(intent);
        });
    }
}