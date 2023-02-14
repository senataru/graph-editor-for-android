package com.example.graph_editor.model;

import java.util.Map;
import java.util.function.IntFunction;

import graph_editor.graph.DirectedGraph;
import graph_editor.graph.GenericGraphBuilder;
import graph_editor.graph.Graph;
import graph_editor.graph.UndirectedGraph;

public enum GraphType {
    DIRECTED("directed"),
    UNDIRECTED("undirected");

    static final Map<GraphType, IntFunction<GenericGraphBuilder<? extends Graph>>> graphBuilderFactoryMap =
            Map.of(DIRECTED, DirectedGraph.Builder::new, UNDIRECTED, UndirectedGraph.Builder::new);
    private final String type;

    GraphType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static GraphType getFromString(String type) {
        if (type.equals("directed")) {
            return DIRECTED;
        } else if (type.equals("undirected")) {
            return UNDIRECTED;
        } else {
            throw new IllegalArgumentException("Unknown graph type: " + type);
        }
    }

    public IntFunction<GenericGraphBuilder<? extends Graph>> getGraphBuilderFactory() {
        return graphBuilderFactoryMap.get(this);
    }
}
