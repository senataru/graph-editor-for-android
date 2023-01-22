package com.example.graph_editor.draw.popups;

import android.content.Context;
import android.content.Intent;

import graph_editor.graph.Graph;
import graph_editor.visual.GraphVisualization;

public class ShareAsTxtIntent {
    private final Context context;
    private final GraphVisualization visualization;

    public ShareAsTxtIntent(Context context, GraphVisualization visualization) {
        this.context = context;
        this.visualization = visualization;
    }

    public void show() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);  // for some reason this action won't display custom titles
        sendIntent.putExtra(Intent.EXTRA_TEXT, GraphWriter.toExact(visualization));
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Export your graph");
        context.startActivity(shareIntent);
    }
}
