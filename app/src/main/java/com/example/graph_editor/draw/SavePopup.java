package com.example.graph_editor.draw;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graph_editor.R;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.graph_storage.GraphWriter;
import com.example.graph_editor.model.Graph;

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
            database.saveDao().insertSaves(new Save(name, GraphWriter.toExact(graph), System.currentTimeMillis()));
            popupView.findViewById(R.id.saveBtn).setEnabled(false);
            Toast.makeText(drawActivityActivity.getApplicationContext(), "Graph saved", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
