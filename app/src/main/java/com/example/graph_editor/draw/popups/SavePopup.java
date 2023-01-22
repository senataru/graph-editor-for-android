package com.example.graph_editor.draw.popups;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graph_editor.R;
import com.example.graph_editor.database.EdgePropertySave;
import com.example.graph_editor.database.VertexPropertySave;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.DrawActivity;
import com.example.graph_editor.file_serialization.Saver;
import com.example.graph_editor.file_serialization.SerializationConstants;

import java.io.Serializable;
import java.util.Map;

import graph_editor.graph.Graph;
import graph_editor.visual.GraphVisualization;

public class SavePopup {
    AlertDialog dialog;

    public SavePopup() {}
    public void show(GraphVisualization visualization, DrawActivity context, Runnable afterTask) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.save_popup, null);

        popupView.findViewById(R.id.saveBtn).setOnClickListener(v -> {
            String name = ((EditText)popupView.findViewById(R.id.nameEditText)).getText().toString();
            if(name.equals("")) {
                Toast.makeText(popupView.getContext(), "Please enter graph name", Toast.LENGTH_LONG).show();
                return;
            }
            Saver.save(context, SerializationConstants.savesDirectory + name, (Serializable) visualization);
            context.setName(name);

            Toast.makeText(context.getApplicationContext(), "Graph saved", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            afterTask.run();
        });

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '//' marks
    private void saveAllProperties(Graph graph, long graphSaveId, DrawActivity context, SavesDatabase database) {
        saveAllVertexProperties(graph, graphSaveId, context, database);
        saveAllEdgeProperties(graph, graphSaveId, context, database);
    }
    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '//' marks
    private void saveAllVertexProperties(Graph graph, long graphSaveId, DrawActivity context, SavesDatabase database) {
        Map<String, String> newPropertyStrings = null; //GraphWriter.getAllVertexPropertyStrings(graph);

        for (String propertyName : newPropertyStrings.keySet()) {
            VertexPropertySave vertexPropertySave = new VertexPropertySave(graphSaveId,
                    propertyName, newPropertyStrings.get(propertyName), System.currentTimeMillis());
            database.vertexPropertySaveDao().insertPropertySave(vertexPropertySave);
        }
        context.updateGraphVertexProperties(newPropertyStrings);
    }
    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '//' marks
    private void saveAllEdgeProperties(Graph graph, long graphSaveId, DrawActivity context, SavesDatabase database) {
        Map<String, String> newPropertyStrings = null; //GraphWriter.getAllEdgePropertyStrings(graph);

        for (String propertyName : newPropertyStrings.keySet()) {
            EdgePropertySave edgePropertySave = new EdgePropertySave(graphSaveId,
                    propertyName, newPropertyStrings.get(propertyName), System.currentTimeMillis());
            database.edgePropertySaveDao().insertPropertySave(edgePropertySave);
        }
//        context.updateGraphEdgeProperties(newPropertyStrings);
    }
}
