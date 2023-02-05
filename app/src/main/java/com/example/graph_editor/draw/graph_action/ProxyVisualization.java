package com.example.graph_editor.draw.graph_action;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import graph_editor.geometry.Point;
import graph_editor.graph.Edge;
import graph_editor.graph.ExtendedGraphElement;
import graph_editor.graph.GraphElement;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public abstract class ProxyVisualization implements GraphVisualization<PropertySupportingGraph> {
    protected final GraphVisualization<PropertySupportingGraph> visualization;
    public ProxyVisualization(GraphVisualization<PropertySupportingGraph> visualization) {
        this.visualization = visualization;
    }
    @Override
    public Point getVertexPoint(Vertex vertex) {
        return visualization.getVertexPoint(vertex);
    }

    @Override
    public PropertySupportingGraph getGraph() {
        return visualization.getGraph();
    }

    @Override
    public Map<Vertex, Point> getVisualization() {
        return visualization.getVisualization();
    }
}

class GhostEdgeVisualization extends ProxyVisualization {
    private final Edge e;
    public GhostEdgeVisualization(GraphVisualization<PropertySupportingGraph> visualization, Edge additional) {
        super(visualization);
        this.e = additional;
    }
    @Override
    public PropertySupportingGraph getGraph() {
        return new PropertySupportingGraph() {
            final PropertySupportingGraph graph = visualization.getGraph();
            @Override
            public Collection<ExtendedGraphElement> getExtendedElements() {
                return graph.getExtendedElements();
            }
            @Override
            public List<Edge> getEdges() {
                return Stream.concat(graph.getEdges().stream(), Stream.of(e)).collect(Collectors.toList());
            }
            @Override
            public List<Vertex> getVertices() {
                return graph.getVertices();
            }
            @Override
            public Iterable<String> getPropertiesNames() {
                return graph.getPropertiesNames();
            }
            @Override
            public Iterable<GraphElement> getElementsWithProperty(String s) {
                return graph.getElementsWithProperty(s);
            }
            @Override
            public String getPropertyValue(String s, GraphElement graphElement) {
                return graph.getPropertyValue(s, graphElement);
            }
        };
    }
}
