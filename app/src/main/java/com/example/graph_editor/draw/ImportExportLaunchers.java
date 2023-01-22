package com.example.graph_editor.draw;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;

import com.example.graph_editor.file_serialization.Loader;
import com.example.graph_editor.file_serialization.Saver;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;

import java.io.Serializable;

import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class ImportExportLaunchers {
    public static void importCommand(ActivityResult result, Context context, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, State state) {
        if( result.getResultCode() != Activity.RESULT_OK || result.getData() == null)
            return;
        Uri uri = result.getData().getData();
        Saver.save(context, uri, (Serializable) stack.getCurrent());
    }
    public static void exportCommand(ActivityResult result, Context context, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, State state) {
        if( result.getResultCode() != Activity.RESULT_OK || result.getData() == null)
            return;
        Uri uri = result.getData().getData();
        var visualization = Loader.<GraphVisualization<PropertySupportingGraph>>load(context, uri);

        stack.push(visualization);
        Rectangle oldRec = state.getRectangle();
        Rectangle optimalRec = DrawManager.getOptimalRectangle(visualization.getVisualization(), visualization.getGraph(), 0.1, oldRec);
        state.setRectangle(optimalRec);

        Toast.makeText(context, "Import complete", Toast.LENGTH_SHORT).show();
    }
}
