package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public interface GraphAction {
    GraphVisualization<PropertySupportingGraph> perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack);
}
