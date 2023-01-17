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
import com.example.graph_editor.model.graph_storage.GraphWriter;

import java.util.Map;

public class SavePopup {
    AlertDialog dialog;

    public SavePopup() {}
    public void show(Graph graph, DrawActivity context, Runnable afterTask) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.save_popup, null);

        popupView.findViewById(R.id.saveBtn).setOnClickListener(v -> {
            SavesDatabase database = SavesDatabase.getDbInstance(context.getApplicationContext());
            String name = ((EditText)popupView.findViewById(R.id.nameEditText)).getText().toString();
            if(name.equals("")) {
                Toast.makeText(popupView.getContext(), "Please enter graph name", Toast.LENGTH_LONG).show();
                return;
            }
            String newGraphString = GraphWriter.toExact(graph);
            Save save = new Save(name, newGraphString, System.currentTimeMillis());
            long graphSaveId = database.saveDao().insertSaves(save)[0];
            context.updateGraph(graphSaveId, newGraphString);

            saveAllProperties(graph, graphSaveId, context, database);

            Toast.makeText(context.getApplicationContext(), "Graph saved", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            afterTask.run();
        });

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }

    private void saveAllProperties(Graph graph, long graphSaveId, DrawActivity context, SavesDatabase database) {
        saveAllVertexProperties(graph, graphSaveId, context, database);
        saveAllEdgeProperties(graph, graphSaveId, context, database);
    }

    private void saveAllVertexProperties(Graph graph, long graphSaveId, DrawActivity context, SavesDatabase database) {
        Map<String, String> newPropertyStrings = GraphWriter.getAllVertexPropertyStrings(graph);

        for (String propertyName : newPropertyStrings.keySet()) {
            VertexPropertySave vertexPropertySave = new VertexPropertySave(graphSaveId,
                    propertyName, newPropertyStrings.get(propertyName), System.currentTimeMillis());
            database.vertexPropertySaveDao().insertPropertySave(vertexPropertySave);
        }
        context.updateGraphVertexProperties(newPropertyStrings);
    }

    private void saveAllEdgeProperties(Graph graph, long graphSaveId, DrawActivity context, SavesDatabase database) {
        Map<String, String> newPropertyStrings = GraphWriter.getAllEdgePropertyStrings(graph);

        for (String propertyName : newPropertyStrings.keySet()) {
            EdgePropertySave edgePropertySave = new EdgePropertySave(graphSaveId,
                    propertyName, newPropertyStrings.get(propertyName), System.currentTimeMillis());
            database.edgePropertySaveDao().insertPropertySave(edgePropertySave);
        }
        context.updateGraphEdgeProperties(newPropertyStrings);
    }
}
