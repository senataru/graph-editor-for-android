package com.example.graph_editor.extensions;

import android.view.View;

import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public interface GraphMenuManager {
    interface MenuOptionHandler {
        void handle(VersionStack<GraphVisualization<PropertySupportingGraph>> stack , Graph graph, View view);
    }
    int registerOption(String name, MenuOptionHandler onOptionSelection);
    void deregisterOption(int id);
}
