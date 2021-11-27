package com.example.graph_editor.draw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.graph_editor.R;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.graphStorage.GraphWriter;
import com.example.graph_editor.model.Graph;

import java.util.Timer;

public class SavePopup {
    DrawActivity drawActivityActivity;
    Context context;
    AlertDialog dialog;
    SavePopup(DrawActivity drawActivity, Context context) {  // TODO: rewrite this implementation
        this.drawActivityActivity = drawActivity;
        this.context = context;
    }
    public void show(Graph graph) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)drawActivityActivity.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.save_popup, null);
        popupView.findViewById(R.id.saveBtn).setOnClickListener(v -> {
            SavesDatabase database = SavesDatabase.getDbInstance(drawActivityActivity.getApplicationContext());
            String name = ((EditText)popupView.findViewById(R.id.nameEditText)).getText().toString();
            if(name.equals("")) {
                Toast.makeText(popupView.getContext(), "Please enter graph name", Toast.LENGTH_LONG).show();
                return;
            }
            database.saveDao().insertScore(new Save(name, GraphWriter.toExact(graph), System.currentTimeMillis()));
            popupView.findViewById(R.id.saveBtn).setEnabled(false);
            Toast.makeText(drawActivityActivity.getApplicationContext(), "Graph saved", Toast.LENGTH_LONG).show();
        });
        /*popupView.findViewById(R.id.mainMenuBtn).setOnClickListener(v -> {
            drawActivityActivity.finish();
            dialog.dismiss();
        });
        popupView.findViewById(R.id.playAgainBtn).setOnClickListener(v -> {
            drawActivityActivity.startActivity(new Intent(drawActivityActivity, DrawActivity.class));
            drawActivityActivity.finish();
            dialog.dismiss();
        });*/
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
