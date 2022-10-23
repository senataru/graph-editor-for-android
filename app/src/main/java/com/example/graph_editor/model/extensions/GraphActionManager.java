package com.example.graph_editor.model.extensions;

import com.example.graph_editor.draw.action_mode_type.GraphAction;

public interface GraphActionManager {
    int registerAction(String imageButtonPath, GraphAction onActionSelection);
    void deregisterAction(int id);
}
