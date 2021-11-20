package com.example.graph_editor.model;

import com.example.graph_editor.model.mathematics.Point;

import java.util.List;

public interface Vertex {
    void addEdge(Vertex target);
    void removeEdge(Edge edge);
    List<Edge> getEdges();
    void setPoint(Point point);
    Point getPoint();
}
