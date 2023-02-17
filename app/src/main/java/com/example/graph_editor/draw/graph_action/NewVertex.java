package com.example.graph_editor.draw.graph_action;

import static graph_editor.properties.GraphDebuilder.deBuild;

import android.view.MotionEvent;
import androidx.annotation.NonNull;

import java.util.function.IntFunction;

import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.draw.point_mapping.ScreenPoint;
import graph_editor.graph.GenericGraphBuilder;
import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class NewVertex extends GraphOnTouchMutation {
    private ScreenPoint sp;
    private final IntFunction<GenericGraphBuilder<? extends Graph>> graphBuilderFactory;

    public NewVertex(IntFunction<GenericGraphBuilder<? extends Graph>> graphBuilderFactory) {
        this.graphBuilderFactory = graphBuilderFactory;
    }

    @Override
    public GraphVisualization<PropertySupportingGraph> perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                sp = new ScreenPoint(event.getX(), event.getY());
                return execute(mapper, stack.getCurrent());
            }
            case MotionEvent.ACTION_UP -> {
                GraphVisualization<PropertySupportingGraph> visualization = execute(mapper, stack.getCurrent());
                stack.push(visualization);
            }
            default -> { }
        }
        return stack.getCurrent();
    }

    @Override
    protected GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous) {
        mapper.mapFromView(sp);
        PropertySupportingGraph graph = previous.getGraph();
        var builder = graphBuilderFactory.apply(0);
        Vertex addedVertex = builder.addVertex();

        BuilderVisualizer visualizer = new BuilderVisualizer();
        visualizer.addCoordinates(addedVertex, mapper.mapFromView(sp));
        PropertyGraphBuilder propertyGraphBuilder = deBuild(graph, builder, visualizer, previous.getVisualization().entrySet());
        return visualizer.generateVisual(propertyGraphBuilder.build());
    }
}
