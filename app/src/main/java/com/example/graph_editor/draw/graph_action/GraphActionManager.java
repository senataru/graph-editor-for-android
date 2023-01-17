package com.example.graph_editor.draw.graph_action;

public interface GraphActionManager {
    int registerAction(String imageButtonPath, GraphAction onActionSelection);
    void deregisterAction(int id);
}
