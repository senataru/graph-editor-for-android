package com.example.graph_editor.draw.popups;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.DrawActivity;
import com.example.graph_editor.file_serialization.FileData;
import com.example.graph_editor.file_serialization.Saver;
import com.example.graph_editor.fs.FSDirectories;
import com.example.graph_editor.model.GraphType;

import java.io.File;
import java.io.Serializable;

import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class SavePopup {
    AlertDialog dialog;

    public SavePopup() {}
    public void show(GraphVisualization<PropertySupportingGraph> visualization, GraphType type, DrawActivity context, Runnable afterTask) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.save_popup, null);

        popupView.findViewById(R.id.saveBtn).setOnClickListener(v -> {
            String name = ((EditText)popupView.findViewById(R.id.nameEditText)).getText().toString();
            if(name.equals("")) {
                Toast.makeText(popupView.getContext(), "Please enter graph name", Toast.LENGTH_LONG).show();
                return;
            }
            Saver.save(context, new File(context.getFilesDir(), FSDirectories.graphsDirectory), name, new FileData(visualization, type));
            context.setName(name);

            Toast.makeText(context.getApplicationContext(), "Graph saved", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            afterTask.run();
        });

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
