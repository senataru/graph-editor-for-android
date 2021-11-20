package com.example.graph_editor.model;

import com.example.graph_editor.model.mathematics.Point;

import java.util.List;

public interface Graph {
    void addVertex();
    void addEdge(Vertex source, Vertex target);
    GraphType getType();
    List<Vertex> getVertices();
    List<Edge> getEdges();
    DrawManager getDrawManager();
}
