package com.example.graph_editor.model;

import java.util.Map;
import java.util.function.Supplier;

import graph_editor.graph.Graph;
import graph_editor.graph_generators.GraphGenerator;
import graph_editor.graph_generators.GraphGeneratorBipartiteClique;
import graph_editor.graph_generators.GraphGeneratorClique;
import graph_editor.graph_generators.GraphGeneratorFullBinaryTree;
import graph_editor.graph_generators.GraphGeneratorGrid;
import graph_editor.graph_generators.GraphGeneratorKingGrid;
import graph_editor.graph_generators.GraphGeneratorUndirectedCycle;

public class GraphGeneratorsUndirected {
    private static final Map<String, Supplier<GraphGenerator<? extends Graph>>> generatorsMap = Map.of(
            "cycle", GraphGeneratorUndirectedCycle::new,
            "clique", GraphGeneratorClique::new,
            "bipartite clique", GraphGeneratorBipartiteClique::new,
            "full binary tree", GraphGeneratorFullBinaryTree::new,
            "grid", GraphGeneratorGrid::new,
            "king grid", GraphGeneratorKingGrid::new
            );

    public static Map<String, Supplier<GraphGenerator<? extends Graph>>> getGeneratorsMap() {
        return generatorsMap;
    }
}
