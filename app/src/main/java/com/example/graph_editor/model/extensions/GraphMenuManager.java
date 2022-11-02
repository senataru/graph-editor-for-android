package com.example.graph_editor.model.extensions;

import android.content.Context;
import android.view.View;

import com.example.graph_editor.model.Graph;

import java.util.function.Consumer;

public interface GraphMenuManager {
    interface MenuOptionHandler {
        void handle(Graph graph, View view);
    }
    int registerOption(String name, MenuOptionHandler onOptionSelection);
    void deregisterOption(int id);
}
