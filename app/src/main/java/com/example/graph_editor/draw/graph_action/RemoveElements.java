package com.example.graph_editor.draw.graph_action;

import static graph_editor.properties.GraphDebuilder.copyProperties;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.StreamSupport;

import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.draw.point_mapping.ScreenPoint;
import graph_editor.geometry.GeometryUtils;
import graph_editor.geometry.Point;
import graph_editor.graph.Edge;
import graph_editor.graph.GenericGraphBuilder;
import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.properties.GraphDebuilder;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class RemoveElements extends GraphOnTouchMutation {
    private static final double eraserSize = 0.05;
    private Set<Vertex> removedVertices = new HashSet<>();
    private Set<Edge> removedEdges = new HashSet<>();
    private final IntFunction<GenericGraphBuilder<? extends Graph>> graphBuilderFactory;

    public RemoveElements(IntFunction<GenericGraphBuilder<? extends Graph>> graphBuilderFactory) {
        this.graphBuilderFactory = graphBuilderFactory;
    }

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
                            if (e.getOther(adj).equals(v)) { removedEdges.add(e); }
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
        Map<Vertex, Vertex> vCorrespondence = new HashMap<>();
        Map<Edge, Edge> eCorrespondence = new HashMap<>();
        var graphBuilder = graphBuilderFactory.apply(0);
        for (Vertex v : previous.getGraph().getVertices()) {
            if (!removedVertices.contains(v)) {
                vCorrespondence.put(v, graphBuilder.addVertex());
            }
        }
        removedVertices.forEach(v -> System.out.println(v.getIndex()));
        System.out.println("hee");
        vCorrespondence.forEach((v, addedVertex) -> {
            v.getEdges().forEach(e -> {
                if(!removedEdges.contains(e) && !removedVertices.contains(e.getOther(v))) {
                    Optional<Edge> addedEdge = graphBuilder.addEdge(
                            addedVertex.getIndex(), vCorrespondence.get(e.getOther(v)).getIndex()
                    );
                    addedEdge.ifPresent(edge -> eCorrespondence.put(e, edge));
                }
            });
        });
        PropertyGraphBuilder propertyGraphBuilder = new PropertyGraphBuilder(graphBuilder);
        BuilderVisualizer visualizer = new BuilderVisualizer();
        graph.getExtendedElements().forEach(propertyGraphBuilder::addExtendedElement);
        copyProperties(graph, propertyGraphBuilder, vCorrespondence, eCorrespondence);
        Map<Vertex, Point> coordinates = previous.getVisualization();
        vCorrespondence.forEach((v, builderV) -> {
            visualizer.addCoordinates(builderV, coordinates.get(v));
        });
        return visualizer.generateVisual(propertyGraphBuilder.build());
    }
}
