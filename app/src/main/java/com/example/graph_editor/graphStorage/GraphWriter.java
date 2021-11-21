package com.example.graph_editor.graphStorage;

import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.GraphType;
import com.example.graph_editor.model.Vertex;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class GraphWriter {
    public static String toVE(Graph g) {
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
            s.append(Math.min(index1, index2)).append(" ").append(Math.max(index1, index2)).append("\n");
//            s.append(index1).append(" ").append(index2).append("\n");
        }

        return new String(s);
    }

    public static String toExact (Graph g) {
        StringBuilder s = new StringBuilder();

        List<Vertex> vertices = g.getVertices();
        List<Edge> edges = g.getEdges();



        s.append(g.getType().toString());
        s.append("\n");

        s.append(vertices.size());
        s.append(" ");
        s.append(edges.size());
        s.append("\n");

        for (Vertex v : vertices) {
            s.append(v.getIndex());
            s.append(" ");
            s.append(v.getPoint().getX());
            s.append(" ");
            s.append(v.getPoint().getY());
            s.append("\n");
        }

        for (Edge e : edges) {
            int index1 = e.getSource().getIndex();
            int index2 = e.getTarget().getIndex();
            s.append(Math.min(index1, index2)).append(" ").append(Math.max(index1, index2)).append("\n");
        }

        return new String(s);

    }
}
