package com.example.graph_editor.model;

import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.List;

public class VertexImpl implements Vertex {
    int id;
    String name;
    List<Edge> edges = new ArrayList<>();
    Point absolutePoint;
    Point relativePoint;
    EdgeFactory edgeFactory;

    public VertexImpl(int id, String name) {
        this.id = id;
        this.name = name;
        edgeFactory = new EdgeFactory(this);
        this.absolutePoint = new Point(0, 0);
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
    public void setAbsolutePoint(Point point) {
        this.absolutePoint = point;
    }

    @Override
    public Point getAbsolutePoint() {
        return absolutePoint;
    }

    @Override
    public void setRelativePoint(Point currentPoint) {
        this.relativePoint = currentPoint;
    }

    @Override
    public Point getRelativePoint() {
        return relativePoint;
    }
}
