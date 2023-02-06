package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.example.graph_editor.point_mapping.PointMapper;
import com.example.graph_editor.point_mapping.ScreenPoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import graph_editor.geometry.GeometryUtils;
import graph_editor.geometry.Point;
import graph_editor.graph.Edge;
import graph_editor.graph.SimpleGraphBuilder;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class RemoveElements extends GraphOnTouchMutation {
    private static final double eraserSize = 0.05;
    Set<Vertex> removedVertices = new HashSet<>();
    Set<Edge> removedEdges = new HashSet<>();
    @Override
    public GraphVisualization<PropertySupportingGraph> perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                Point eraser = mapper.mapFromView(new ScreenPoint(event.getX(), event.getY()));
                for (Vertex v : stack.getCurrent().getGraph().getVertices()) {
                    if (GeometryUtils.distance(stack.getCurrent().getVertexPoint(v), eraser) < eraserSize / mapper.getZoom()) {
                        System.out.println(stack.getCurrent().getVertexPoint(v).getX() + " " + eraser.getX());
                        removedVertices.add(v);
                        removedEdges.addAll(v.getEdges());
                        stack.getCurrent().getGraph().getVertices().forEach(adj -> adj.getEdges().forEach(e -> {
                            if (e.getTarget().equals(v)) { removedEdges.add(e); }
                        }));
                    }
                }
                return execute(mapper, stack.getCurrent());
            }
            case MotionEvent.ACTION_UP -> {
                GraphVisualization<PropertySupportingGraph> visualization = execute(mapper, stack.getCurrent());
                stack.push(visualization);
                removedVertices = new HashSet<>();
                removedEdges = new HashSet<>();
            }
        }
        return stack.getCurrent();
    }
    @Override
    protected GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous) {
        PropertySupportingGraph graph = previous.getGraph();
        var builder = new SimpleGraphBuilder(0);
        Map<Vertex, Vertex> correspondence = new HashMap<>();
        for (Vertex v : previous.getGraph().getVertices()) {
            if (!removedVertices.contains(v)) {
                correspondence.put(v, builder.addVertex());
            }
        }
        correspondence.forEach((v, addedVertex) -> {
            v.getEdges().forEach(e -> {
                if(!removedEdges.contains(e) && !removedVertices.contains(e.getTarget())) {
                    builder.addEdge(addedVertex.getIndex(), correspondence.get(e.getTarget()).getIndex());
                }
            });
        });
        BuilderVisualizer visualizer = new BuilderVisualizer();
        var propertyGraphBuilder = new PropertyGraphBuilder(builder.build());
        graph.getExtendedElements().forEach(propertyGraphBuilder::addExtendedElement);
        graph.getPropertiesNames().forEach(propertyName -> {
            propertyGraphBuilder.registerProperty(propertyName);
            StreamSupport.stream(graph.getElementsWithProperty(propertyName).spliterator(), false)
                            .filter(ge -> !removedVertices.contains(ge) && !removedEdges.contains(ge))
                                    .forEach(graphElement -> propertyGraphBuilder.addElementProperty(
                                        graphElement,
                                        propertyName,
                                        graph.getPropertyValue(propertyName, graphElement)
                                    ));
        });
        previous.getVisualization().forEach((v, p) -> {
            if (!removedVertices.contains(v)) {
                visualizer.addCoordinates(correspondence.get(v), p);
            }
        });
        return visualizer.generateVisual(propertyGraphBuilder.build());
    }
}
