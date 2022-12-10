package com.example.graph_editor.model;

public interface Edge extends PropertyContainer {
    Vertex getSource();
    Vertex getTarget();
}
