package com.example.graph_editor.model;

public class GraphFactory {
    Graph produce() {
        return new GraphImpl(GraphType.DIRECTED);
    }
}
