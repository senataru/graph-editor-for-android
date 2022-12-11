package com.example.graph_editor.model;

public interface PropertyContainer {
    String getProperty(String name);
    void setProperty(String name, String value);
    void removeProperty(String name);
}
