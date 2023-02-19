package com.example.graph_editor.draw.graph_action;


import android.view.MotionEvent;

import androidx.annotation.NonNull;

import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.draw.point_mapping.ScreenPoint;
import graph_editor.geometry.Point;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class MoveCanvas implements GraphAction {
    private ScreenPoint downPoint;
    private Point offset;
    @Override
    public GraphVisualization<PropertySupportingGraph> perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                downPoint = new ScreenPoint(event.getX(), event.getY());
                offset = mapper.getOffset();
            }
            case MotionEvent.ACTION_MOVE -> {
                if (downPoint == null) {
                    downPoint = new ScreenPoint(event.getX(), event.getY());
                    offset = mapper.getOffset();
                } else {
                    ScreenPoint downOnScreen = mapper.mapIntoView(offset);
                    float diffX = downOnScreen.getX() - event.getX();
                    float diffY = downOnScreen.getY() - event.getY();
                    ScreenPoint currentOffset = new ScreenPoint(downPoint.getX() + diffX, downPoint.getY() + diffY);
                    mapper.setOffset(mapper.mapFromView(currentOffset));
                }
            }
            default -> { }
        }
        return stack.getCurrent();
    }
}
