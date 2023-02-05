package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.example.graph_editor.point_mapping.PointMapper;

import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class RotateCanvas implements GraphAction {
    private float downY;
    @Override
    public GraphVisualization<PropertySupportingGraph> perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                downY = event.getY();
            }
            case MotionEvent.ACTION_MOVE -> {
                float diffY = downY - event.getY();
                downY = event.getY();
                mapper.rotate(diffY, event.getX());
            }
            default -> { }
        }
        return stack.getCurrent();
    }
}
