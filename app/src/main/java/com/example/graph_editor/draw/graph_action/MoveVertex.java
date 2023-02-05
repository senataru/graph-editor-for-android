package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.example.graph_editor.point_mapping.PointMapper;
import com.example.graph_editor.point_mapping.ScreenPoint;

import java.util.Map;
import java.util.stream.Collectors;

import graph_editor.geometry.GeometryUtils;
import graph_editor.geometry.Point;
import graph_editor.graph.SimpleGraphBuilder;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class MoveVertex extends GraphDebuilder {
    private ScreenPoint sp1;
    private ScreenPoint sp2;
    @Override
    public boolean perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> sp1 = new ScreenPoint(event.getX(), event.getY());
            case MotionEvent.ACTION_MOVE -> sp2 = new ScreenPoint(event.getX(), event.getY());
            case MotionEvent.ACTION_UP -> {
                GraphVisualization<PropertySupportingGraph> visualization = execute(mapper, stack.getCurrent());
                if (visualization != null) { stack.push(visualization); }
            }
            default -> { }
        }
        return true;
    }

    @Override
    protected GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous) {
        Map<Point, Vertex> inverse = previous
                .getVisualization()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Point p1 = GeometryUtils.findClosestPoint(mapper.mapFromView(sp1), inverse.keySet());
        Point p2 = mapper.mapFromView(sp2);
        if (p1 == null) {
            return null;
        } else {
            PropertySupportingGraph graph = previous.getGraph();
            var builder = new SimpleGraphBuilder(graph.getVertices().size());
            BuilderVisualizer visualizer = new BuilderVisualizer();
            Vertex movedVertex = inverse.get(p1);
            var entries = previous
                    .getVisualization()
                    .entrySet()
                    .stream()
                    .filter(e -> !e.getKey().equals(movedVertex))
                    .collect(Collectors.toList());
            PropertyGraphBuilder propertyGraphBuilder = deBuild(graph, builder, visualizer, entries);
            visualizer.addCoordinates(movedVertex, p2);
            return visualizer.generateVisual(propertyGraphBuilder.build());
        }
    }
}
