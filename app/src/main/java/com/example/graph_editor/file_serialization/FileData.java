package com.example.graph_editor.file_serialization;

import com.example.graph_editor.model.GraphType;

import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class FileData {
    public final GraphVisualization<PropertySupportingGraph> visualization;
    public final GraphType type;

    public FileData(GraphVisualization<PropertySupportingGraph> visualization, GraphType type) {
        this.visualization = visualization;
        this.type = type;
    }
}