package com.example.graph_editor.model.graph_storage;

import com.example.graph_editor.model.GraphType;

import java.util.ArrayList;
import java.util.List;

public class GraphScanner {
    //
    public static Graph fromExact(String s) throws InvalidGraphStringException {
        Graph g;
        String[] t = s.split("\n");

        if (t[0].equals("DIRECTED")) {
            g = new GraphFactory(GraphType.DIRECTED).produce();
        } else if (t[0].equals("UNDIRECTED")) {
            g = new GraphFactory(GraphType.UNDIRECTED).produce();
        } else {
            throw new InvalidGraphStringException();
        }

        try {
            int v = Integer.parseInt(t[1].split(" ")[0]);
            int e = Integer.parseInt(t[1].split(" ")[1]);

            for (int i = 0; i < v; i++) {
                g.addVertex();
            }
            List<Vertex> vertices = g.getVertices();


            for (int i = 2; i < 2 + v; i++) {
                String[] split = t[i].split(" ");
                int index = Integer.parseInt(split[0]);
                double x = Double.parseDouble(split[1]);
                double y = Double.parseDouble(split[2]);
                Point point = new Point(x, y);
                g.getVertices().get(index).setPoint(point);
            }

            for (int i = 2 + v; i < 2 + v + e; i++) {
                String[] split = t[i].split(" ");
                int a = Integer.parseInt(split[0]);
                int b = Integer.parseInt(split[1]);

                g.addEdge(vertices.get(a), vertices.get(b));
            }
        } catch (NumberFormatException e) {
            throw new InvalidGraphStringException();
        }

        return g;
    }

    public static List<Graph> fromExactList(String s) throws InvalidGraphStringException {
        List<Graph> result = new ArrayList<>();
        String[] t = s.split("#");
        for (String g : t) {
            result.add(fromExact(g));
        }
        return result;
    }

    public static Graph addVertexProperty(Graph graph, String s) throws InvalidGraphStringException {
        String[] text = s.split("\n");

        String propertyName = text[0];
        int count = Integer.parseInt(text[1]);

        try {
            for (int i = 2; i < 2 + count; i++) {
                String[] split = text[i].split(" ");
                int index = Integer.parseInt(split[0]);
                String value = split[1];
                graph.setVertexProperty(graph.getVertices().get(index), propertyName, value);
            }
        } catch (RuntimeException e) {
            throw new InvalidGraphStringException();
        }
        return graph;
    }

    public static Graph addEdgeProperty(Graph graph, String s) throws InvalidGraphStringException {
        String[] text = s.split("\n");

        String propertyName = text[0];
        int count = Integer.parseInt(text[1]);

        try {
            List<Vertex> vertices = graph.getVertices();
            for (int i = 2; i < 2 + count; i++) {
                String[] split = text[i].split(" ");
                int sourceIndex = Integer.parseInt(split[0]);
                int targetIndex = Integer.parseInt(split[1]);
                Vertex sourceVertex = vertices.get(sourceIndex);
                Vertex targetVertex = vertices.get(targetIndex);
                String value = split[2];
                graph.setEdgeProperty(graph.getEdge(sourceVertex, targetVertex), propertyName, value);
            }
        } catch (RuntimeException e) {
            throw new InvalidGraphStringException();
        }
        return graph;
    }
}
