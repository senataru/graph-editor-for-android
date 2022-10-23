package com.example.graph_editor.model.extensions;

import android.content.Context;

import com.example.graph_editor.draw.action_mode_type.GraphAction;

import java.util.function.Consumer;

public interface GraphActionManager {
    int registerOption(String imageButtonPath, GraphAction onActionSelection);
    void deregisterOption(int id);
}
