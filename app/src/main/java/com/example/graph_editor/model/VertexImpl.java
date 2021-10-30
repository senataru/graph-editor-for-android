package com.example.graph_editor.model;

import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.List;

public class VertexImpl implements Vertex {
    int id;
    String name;
    List<Edge> edges = new ArrayList<>();
    Point point;
    Point currentPoint;
    EdgeFactory edgeFactory;

    public VertexImpl(int id, String name) {
        this.id = id;
        this.name = name;
        edgeFactory = new EdgeFactory(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addEdge(Vertex target) {
        edges.add(edgeFactory.produce(target));
    }

    @Override
    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }

    @Override
    public Point getCurrentPoint() {
        return currentPoint;
    }
}
