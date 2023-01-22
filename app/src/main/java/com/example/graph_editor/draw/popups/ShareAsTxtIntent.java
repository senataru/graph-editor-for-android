package com.example.graph_editor.draw.popups;

import android.content.Context;
import android.content.Intent;

import graph_editor.graph.Graph;

public class ShareAsTxtIntent {
    private final Context context;
    private final Graph graph;

    public ShareAsTxtIntent(Context context, Graph graph) {
        this.context = context;
        this.graph = graph;
    }

    public void show() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);  // for some reason this action won't display custom titles
        sendIntent.putExtra(Intent.EXTRA_TEXT, GraphWriter.toExact(graph));
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Export your graph");
        context.startActivity(shareIntent);
    }
}
