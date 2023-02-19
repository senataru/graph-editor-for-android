package com.example.graph_editor.draw;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;

import com.example.graph_editor.file_serialization.FileData;
import com.example.graph_editor.file_serialization.Loader;
import com.example.graph_editor.file_serialization.Saver;
import com.example.graph_editor.model.GraphType;

import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class ImportExportLaunchers {
    public static void importCommand(ActivityResult result, Context context, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, GraphType type) {
        if( result.getResultCode() != Activity.RESULT_OK || result.getData() == null)
            return;
        Uri uri = result.getData().getData();
        Saver.save(context, uri, new FileData(stack.getCurrent(), type));
    }
    public static void exportCommand(ActivityResult result, Context context, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, GraphType type) {
        if( result.getResultCode() != Activity.RESULT_OK || result.getData() == null)
            return;
        Uri uri = result.getData().getData();
        var visualizationOptional = Loader.<GraphVisualization<PropertySupportingGraph>>load(context, uri);

        visualizationOptional.ifPresent(data -> {
            if (data.type != type) {
                Toast.makeText(context, "Cannot load " + data.type + " graph, because graphStack contains " + type + " graphs", Toast.LENGTH_SHORT).show();
                return;
            }
            stack.push(data.visualization);
            Toast.makeText(context, "Import complete", Toast.LENGTH_SHORT).show();
        });
//        Rectangle oldRec = state.getRectangle();
//        Rectangle optimalRec = DrawManager.getOptimalRectangle(visualization.getVisualization(), visualization.getGraph(), 0.1, oldRec);
//        state.setRectangle(optimalRec);
    }
}
