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

public class NewEdge extends GraphDebuilder {
    private ScreenPoint sp1;
    private ScreenPoint sp2;
    @Override
    public GraphVisualization<PropertySupportingGraph> perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> sp1 = new ScreenPoint(event.getX(), event.getY());
            case MotionEvent.ACTION_MOVE -> {
                sp2 = new ScreenPoint(event.getX(), event.getY());
                var visualization = execute(mapper, stack.getCurrent());
                return visualization != null ? visualization : stack.getCurrent();
            }
            case MotionEvent.ACTION_UP -> {
                GraphVisualization<PropertySupportingGraph> visualization = execute(mapper, stack.getCurrent());
                if (visualization != null) { stack.push(visualization); }
            }
            default -> { }
        }
        return stack.getCurrent();
    }

    @Override
    protected GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous) {
        Map<Point, Vertex> inverse = previous
                .getVisualization()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Point p1 = GeometryUtils.findClosestPoint(mapper.mapFromView(sp1), inverse.keySet());
        Point p2 = GeometryUtils.findClosestPoint(mapper.mapFromView(sp2), inverse.keySet());
        if (p1 == null || p2 == null || p1.equals(p2)) {
            return null;
        } else {
            PropertySupportingGraph graph = previous.getGraph();
            var builder = new SimpleGraphBuilder(graph.getVertices().size());
            builder.addEdge(inverse.get(p1).getIndex(), inverse.get(p2).getIndex());
            BuilderVisualizer visualizer = new BuilderVisualizer();
            PropertyGraphBuilder propertyGraphBuilder = deBuild(graph, builder, visualizer, previous.getVisualization().entrySet());
            return visualizer.generateVisual(propertyGraphBuilder.build());
        }
    }
}
