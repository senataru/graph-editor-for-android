package com.example.graph_editor.model;

import com.example.graph_editor.model.mathematics.Point;

public interface Vertex {
    void addEdge(Vertex target);
    void removeEdge(Edge edge);
    void setPoint(Point point);
    Point getPoint();
}
