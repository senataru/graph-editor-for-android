package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;
import androidx.annotation.NonNull;

import com.example.graph_editor.point_mapping.PointMapper;
import com.example.graph_editor.point_mapping.ScreenPoint;

import graph_editor.graph.SimpleGraphBuilder;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class NewVertex extends GraphDebuilder {
    private ScreenPoint sp;
    @Override
    public boolean perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> sp = new ScreenPoint(event.getX(), event.getY());
            case MotionEvent.ACTION_UP -> {
                GraphVisualization<PropertySupportingGraph> visualization = execute(mapper, stack.getCurrent());
                stack.push(visualization);
            }
            default -> { }
        }
        return true;
    }

    @Override
    protected GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous) {
        mapper.mapFromView(sp);
        PropertySupportingGraph graph = previous.getGraph();
        var builder = new SimpleGraphBuilder(graph.getVertices().size());
        Vertex addedVertex = builder.addVertex();

        BuilderVisualizer visualizer = new BuilderVisualizer();
        visualizer.addCoordinates(addedVertex, mapper.mapFromView(sp));
        PropertyGraphBuilder propertyGraphBuilder = deBuild(graph, builder, visualizer, previous.getVisualization().entrySet());
        return visualizer.generateVisual(propertyGraphBuilder.build());
    }
}
