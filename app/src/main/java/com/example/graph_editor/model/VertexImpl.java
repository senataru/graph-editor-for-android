package com.example.graph_editor.model;

import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VertexImpl implements Vertex {
    int index = -1;
    List<Edge> edges = new ArrayList<>();
    Point absolutePoint;
    EdgeFactory edgeFactory;
    Map<String, String> properties = new HashMap<>();

    public VertexImpl() {
        edgeFactory = new EdgeFactory(this);
        this.absolutePoint = new Point(0, 0);
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void addEdge(Vertex target) {
        Edge e = edgeFactory.produce(target);
        edges.add(e);
    }

    @Override
    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    @Override
    public void removeEdge(Vertex target) {
        List<Edge> toRemove = new ArrayList<>();
        for (Edge e : edges)
            if (e.getTarget() == target)
                toRemove.add(e);
        edges.removeAll(toRemove);
    }

    @Override
    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public void setPoint(Point point) {
        this.absolutePoint = point;
    }

    @Override
    public Point getPoint() {
        return absolutePoint;
    }

    @Override
    public String getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public void setProperty(String name, String value) {
        Objects.requireNonNull(name, "Property name can not be null");
        Objects.requireNonNull(value, "Property value can not be null");
        properties.put(name, value);
    }

    @Override
    public void removeProperty(String name) {
        Objects.requireNonNull(name, "Property name can not be null");
        properties.remove(name);
    }
}
