package com.example.graph_editor.draw.graph_action;

import java.util.Collection;
import java.util.Map;

import graph_editor.geometry.Point;
import graph_editor.graph.GraphBuilder;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualizationBuilder;

public abstract class GraphDebuilder extends GraphOnTouchMutation {
    protected PropertyGraphBuilder deBuild(
            PropertySupportingGraph graph,
            GraphBuilder builder,
            GraphVisualizationBuilder visualizer,
            Collection<Map.Entry<Vertex, Point>> coordinates
    ) {
        graph.getEdges().forEach(edge -> builder.addEdge(edge.getSource().getIndex(), edge.getTarget().getIndex()));

        var propertyGraphBuilder = new PropertyGraphBuilder(builder.build());
        graph.getExtendedElements().forEach(propertyGraphBuilder::addExtendedElement);
        graph.getPropertiesNames().forEach(propertyName -> {
            propertyGraphBuilder.registerProperty(propertyName);
            graph.getElementsWithProperty(propertyName)
                    .forEach(graphElement ->
                            propertyGraphBuilder.addElementProperty(
                                    graphElement,
                                    propertyName,
                                    graph.getPropertyValue(propertyName, graphElement)
                            )
                    );
        });
        coordinates.forEach(e -> visualizer.addCoordinates(e.getKey(), e.getValue()));
        return propertyGraphBuilder;
    }
}
