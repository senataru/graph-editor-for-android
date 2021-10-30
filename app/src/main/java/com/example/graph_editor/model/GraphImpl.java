package com.example.graph_editor.model;

import java.util.ArrayList;
import java.util.List;

public class GraphImpl implements Graph {
    GraphType type;
    List<Vertex> vertexes = new ArrayList<>();
    VertexFactory vertexFactory;

    GraphImpl(GraphType type) {
        this.type = type;
        vertexFactory = new VertexFactory();
    }

    @Override
    public void addVertex() {
        vertexes.add(vertexFactory.produce());
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
    public List<Vertex> getVertexes() {
        return vertexes;
    }
}
