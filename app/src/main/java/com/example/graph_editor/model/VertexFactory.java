package com.example.graph_editor.model;

public class VertexFactory {
    int nextName = 1;
    Vertex produce() {
        return new VertexImpl(String.valueOf(nextName++));
    }
}
