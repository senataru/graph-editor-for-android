package com.example.graph_editor.model;

import com.example.graph_editor.model.graph_storage.GraphScanner;
import com.example.graph_editor.model.graph_storage.GraphWriter;
import com.example.graph_editor.model.graph_storage.InvalidGraphStringException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GraphImpl implements Graph {
    private final GraphType type;
    private final List<Vertex> vertices = new ArrayList<>();
    private final Map<String, List<Vertex>> verticesByProperty = new HashMap<>();

    public GraphImpl(GraphType type) {
        this.type = type;
    }

    @Override
    public Vertex addVertex() {
        Vertex v = new VertexImpl();
        vertices.add(v);
        v.setIndex(vertices.size()-1);
        return v;
    }

    @Override
    public void addEdge(Vertex source, Vertex target) {
        if (source == target) return;
        for (Edge e : source.getEdges())
            if (e.getTarget() == target) return;

        source.addEdge(target);
        if (type == GraphType.UNDIRECTED) {
            target.addEdge(source);
        }
    }

    @Override
    public void removeVertex(Vertex vertex) {
        for(Vertex other : vertices) {
            other.removeEdge(vertex);
        }
        vertices.remove(vertex);
        for(int i=0; i<vertices.size(); i++)
            vertices.get(i).setIndex(i);
    }

    @Override
    public void removeEdge(Edge edge) {
        edge.getSource().removeEdge(edge);
        if(type == GraphType.UNDIRECTED)
            edge.getTarget().removeEdge(edge.getSource());
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
            if(type == GraphType.DIRECTED) {
                result.addAll(vertex.getEdges());
            } else {
                for(Edge edge : vertex.getEdges()) {
                    if(edge.getSource().getIndex() < edge.getTarget().getIndex())
                        result.add(edge);
                }
            }
        }
        return result;
    }

    @Override
    public Graph deepCopy() {
        Graph result = null;
        try {
            result = GraphScanner.fromExact(GraphWriter.toExact(this));
            copyVertexProperties(result);
        } catch (InvalidGraphStringException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void copyVertexProperties(Graph target) {
        List<Vertex> targetVertices = target.getVertices();
        for (Map.Entry<String, List<Vertex>> entry : verticesByProperty.entrySet()) {
            String propertyName = entry.getKey();
            for (Vertex vertex : entry.getValue()) {
                target.setProperty(targetVertices.get(vertex.getIndex()), propertyName,
                        vertex.getProperty(propertyName));
            }
        }
    }

    @Override
    public void setProperty(Vertex vertex, String name, String value) {
        Objects.requireNonNull(name, "Property name can not be null");
        Objects.requireNonNull(value, "Property value can not be null");
        if (!verticesByProperty.containsKey(name)) {
            verticesByProperty.put(name, new ArrayList<>());
        }
        verticesByProperty.get(name).add(vertex);
        vertex.setProperty(name, value);
    }

    @Override
    public void removeProperty(String name) {
        List<Vertex> propertyVertices = verticesByProperty.get(name);
        if (propertyVertices == null) {
            return;
//            throw new IllegalArgumentException("No vertices with property " + name + " have been found");
        }
        for (Vertex vertex : propertyVertices) {
            vertex.removeProperty(name);
        }
        verticesByProperty.remove(name);
    }

    @Override
    public List<Vertex> getVerticesWithProperty(String name) {
        return verticesByProperty.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public List<String> getVertexPropertyNames() {
        return new ArrayList<>(verticesByProperty.keySet());
    }
}
