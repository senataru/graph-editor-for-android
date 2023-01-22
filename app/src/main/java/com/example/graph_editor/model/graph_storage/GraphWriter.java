package com.example.graph_editor.model.graph_storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph_editor.graph.Edge;
import graph_editor.graph.Graph;
import graph_editor.graph.Vertex;

//do not use: disabled functionality, TODO retrieve ASAP, remove all '/' marks
class GraphWriter {
    private static String toVE(Graph g) {
        StringBuilder s = new StringBuilder();

        List<Vertex> vertices = g.getVertices();
        List<Edge> edges = g.getEdges();

        s.append(vertices.size());
        s.append(" ");
        s.append(edges.size());
        s.append("\n");

        for (Edge e : edges) {
            int index1 = e.getSource().getIndex();
            int index2 = e.getTarget().getIndex();
            s.append(index1).append(" ").append(index2).append("\n");
        }

        return new String(s);
    }

    private static String toExact (Graph g) {
        String ve = toVE(g);
        String[] veSplit = ve.split("\n");

        StringBuilder s = new StringBuilder();
//        s.append(g.getType().toString()).append("\n");
        s.append(veSplit[0]).append("\n");

        List<Vertex> vertices = g.getVertices();

        for (Vertex v : vertices) {
            s.append(v.getIndex());
            s.append(" ");
//            s.append((float)v.getPoint().getX());
            s.append(" ");
//            s.append((float)v.getPoint().getY());
            s.append("\n");
        }

        for (int i = 1; i< veSplit.length; i++) {
            s.append(veSplit[i]).append("\n");
        }

        return new String(s);
    }

    private static String toExactList(List<Graph> list){
        StringBuilder result = new StringBuilder();
        for (Graph g : list) {
            result.append(toExact(g));
            result.append("#");
        }
        return result.substring(0, result.length()-1);
    }

    private static Map<String, String> getAllVertexPropertyStrings(Graph graph) {
        Map<String, String> propertyStrings = new HashMap<>();
//        for (String propertyName : graph.getVertexPropertyNames()) {
//            propertyStrings.put(propertyName, getVertexPropertyString(propertyName, graph));
//        }
        return propertyStrings;
    }

    static String getVertexPropertyString(String propertyName, Graph graph) {
        List<Vertex> vertices = graph.getVerticesWithProperty(propertyName);
        if (vertices == null) {
            throw new IllegalArgumentException(
                    "No vertices with property " + propertyName + " have been found");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(propertyName);
        builder.append("\n");
        builder.append(vertices.size());
        builder.append("\n");

        for (Vertex vertex : vertices) {
            String value = vertex.getProperty(propertyName);
            builder.append(vertex.getIndex());
            builder.append(" ");
            builder.append(value);
            builder.append("\n");
        }
        return builder.toString();
    }

    public static Map<String, String> getAllEdgePropertyStrings(Graph graph) {
        Map<String, String> propertyStrings = new HashMap<>();
        for (String propertyName : graph.getEdgePropertyNames()) {
            propertyStrings.put(propertyName, getEdgePropertyString(propertyName, graph));
        }
        return propertyStrings;
    }

    static String getEdgePropertyString(String propertyName, Graph graph) {
        List<Edge> edges = graph.getEdgesWithProperty(propertyName);
        if (edges == null) {
            throw new IllegalArgumentException(
                    "No edges with property " + propertyName + " have been found");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(propertyName);
        builder.append("\n");
        builder.append(edges.size());
        builder.append("\n");

        for (Edge edge : edges) {
            String value = edge.getProperty(propertyName);
            builder.append(edge.getSource().getIndex());
            builder.append(" ");
            builder.append(edge.getTarget().getIndex());
            builder.append(" ");
            builder.append(value);
            builder.append("\n");
        }
        return builder.toString();
    }
}
