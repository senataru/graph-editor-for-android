package com.example.graph_editor.extensions;

import android.view.View;

import graph_editor.graph.Graph;
import graph_editor.graph.GraphStack;

public interface GraphMenuManager {
    interface MenuOptionHandler {
        void handle(GraphStack stack , Graph graph, View view);
    }
    int registerOption(String name, MenuOptionHandler onOptionSelection);
    void deregisterOption(int id);
}
