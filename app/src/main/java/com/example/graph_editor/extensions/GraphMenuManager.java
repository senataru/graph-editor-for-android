package com.example.graph_editor.extensions;

import android.view.View;

import com.example.graph_editor.model.state.StateStack;

import graph_editor.graph.Graph;

public interface GraphMenuManager {
    interface MenuOptionHandler {
        void handle(StateStack stack , Graph graph, View view);
    }
    int registerOption(String name, MenuOptionHandler onOptionSelection);
    void deregisterOption(int id);
}
