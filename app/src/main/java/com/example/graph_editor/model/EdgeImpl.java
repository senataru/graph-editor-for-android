package com.example.graph_editor.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EdgeImpl implements Edge {
    private final Vertex source;
    private final Vertex target;
    private final Map<String, String> properties = new HashMap<>();

    public EdgeImpl(Vertex source, Vertex target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public Vertex getSource() {
        return source;
    }

    @Override
    public Vertex getTarget() {
        return target;
    }

    @Override
    public String getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public void setProperty(String name, String value) {
        Objects.requireNonNull(name, "Property name can not be null");
        Objects.requireNonNull(value, "Property value can not be null");
        properties.put(name, value);
    }

    @Override
    public void removeProperty(String name) {
        Objects.requireNonNull(name, "Property name can not be null");
        properties.remove(name);
    }
}
