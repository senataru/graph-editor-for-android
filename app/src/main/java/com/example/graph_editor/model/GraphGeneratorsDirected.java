package com.example.graph_editor.model;

import java.util.Map;
import java.util.function.Supplier;

import graph_editor.graph.Graph;
import graph_editor.graph_generators.GraphGenerator;
import graph_editor.graph_generators.GraphGeneratorDirectedCycle;

public class GraphGeneratorsDirected {
    private static final Map<String, Supplier<GraphGenerator<? extends Graph>>> generatorsMap = Map.of(
            "cycle", GraphGeneratorDirectedCycle::new
            );

    public static Map<String, Supplier<GraphGenerator<? extends Graph>>> getGeneratorsMap() {
        return generatorsMap;
    }
}
