package com.example.graph_editor.draw;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graph_editor.R;

public class DrawActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        String choice = sharedPref.getString("GraphType", null);

        TextView txtChoice = findViewById(R.id.txtChoice);
        txtChoice.setText(choice);

        TextView txtChoice2 = findViewById(R.id.txtChoice2);

        Button btnVertex = findViewById(R.id.btnVertex);
        btnVertex.setOnClickListener(v -> txtChoice2.setText(R.string.Vertex));

        Button btnEdge = findViewById(R.id.btnEdge);
        btnEdge.setOnClickListener(v -> txtChoice2.setText(R.string.Edge));

    }
}