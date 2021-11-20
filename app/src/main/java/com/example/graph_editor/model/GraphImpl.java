package com.example.graph_editor.model;

import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.List;

public class GraphImpl implements Graph {
    GraphType type;
    List<Vertex> vertices = new ArrayList<>();
    VertexFactory vertexFactory;

    GraphImpl(GraphType type) {
        this.type = type;
        vertexFactory = new VertexFactory();
    }

    @Override
    public void addVertex() {
        vertices.add(vertexFactory.produce());
    }

    @Override
    public void addEdge(Vertex source, Vertex target) {
        source.addEdge(target);
    }

    @Override
    public GraphType getType() {
        return type;
    }

    @Override
    public List<Vertex> getVertices() {
        return vertices;
    }

    @Override
    public List<Edge> getEdges() {
        List<Edge> result = new ArrayList<>();
        for(Vertex vertex : vertices) {
            result.addAll(vertex.getEdges());
        }
        return result;
    }

    @Override
    public DrawManager getDrawManager() {
        return new DrawManager(this);
    }
}
