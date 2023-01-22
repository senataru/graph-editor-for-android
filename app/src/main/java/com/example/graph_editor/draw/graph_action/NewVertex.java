package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;
import androidx.annotation.NonNull;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.point_mapping.PointMapper;
import graph_editor.point_mapping.ScreenPoint;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class NewVertex extends GraphOnTouchMutation {
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
        //TODO this code will probably be redundant in other actions, so move it somewhere else?
        //TODO maybe add remove vertex in builder?
        mapper.mapFromView(sp);
        PropertySupportingGraph graph = previous.getGraph();
        var builder = new PropertyGraphBuilder(graph.getVertices().size());
        graph.getEdges().forEach(edge -> builder.addEdge(edge.getSource().getIndex(), edge.getTarget().getIndex()));
        graph.getExtendedElements().forEach(builder::addExtendedElement);
        graph.getPropertiesNames().forEach(propertyName -> {
            builder.registerProperty(propertyName);
            graph.getElementsWithProperty(propertyName)
                    .forEach(graphElement ->
                            builder.addElementProperty(
                                    graphElement,
                                    propertyName,
                                    graph.getPropertyValue(propertyName, graphElement)
                            )
                    );
        });
        BuilderVisualizer visualizer = new BuilderVisualizer();
        previous.getVisualization().forEach(visualizer::addCoordinates);


        //real code
        Vertex addedVertex = builder.addVertex();
        visualizer.addCoordinates(addedVertex, mapper.mapFromView(sp));
        return visualizer.generateVisual(builder.build());
    }
}
