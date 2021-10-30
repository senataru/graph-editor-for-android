package com.example.graph_editor.model;

import java.util.List;

public interface Graph {
    void addVertex();
    void addEdge(Vertex source, Vertex target);
    GraphType getType();
    List<Vertex> getVertexes();
}
