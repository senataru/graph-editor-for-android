package com.example.graph_editor.model;

public enum GraphType {
    DIRECTED("directed"), UNDIRECTED("undirected");

    private String type;

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
}
