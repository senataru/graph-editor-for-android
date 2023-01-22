package com.example.graph_editor.draw.popups;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class ShareAsIntent {
    private final Context context;
    private final GraphVisualization<PropertySupportingGraph> visualization;

    public ShareAsIntent(Context context, GraphVisualization<PropertySupportingGraph> visualization) {
        this.context = context;
        this.visualization = visualization;
    }

    public void show() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);  // for some reason this action won't display custom titles
        sendIntent.putExtra(Intent.EXTRA_TEXT, (Serializable) visualization);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Export your graph");
        context.startActivity(shareIntent);
    }
}
