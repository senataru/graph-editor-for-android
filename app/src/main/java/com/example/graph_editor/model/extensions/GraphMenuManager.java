package com.example.graph_editor.model.extensions;

import android.content.Context;
import android.view.View;

import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.state.StateStack;

import java.util.function.Consumer;

public interface GraphMenuManager {
    interface MenuOptionHandler {
        void handle(StateStack stack , Graph graph, View view);
    }
    int registerOption(String name, MenuOptionHandler onOptionSelection);
    void deregisterOption(int id);
}
