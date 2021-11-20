package com.example.graph_editor.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.List;

public class VertexImpl implements Vertex {
    int id;
    int index = -1;
    String name;
    List<Edge> edges = new ArrayList<>();
    Point absolutePoint;
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
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void addEdge(Vertex target) {
        edges.add(edgeFactory.produce(target));
    }

    @Override
    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void removeEdge(Vertex target) {
        edges.removeIf(edge -> edge.getTarget() == target);
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
}
