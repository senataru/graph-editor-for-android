package com.example.graph_editor.model.state;

import com.example.graph_editor.draw.graph_action.GraphActionObserver;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.mathematics.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.visual.GraphVisualization;

public class State {
    private GraphVisualization visualization;
    private Rectangle rectangle;
    private GraphAction graphAction;
    private boolean currentlyModified; //more accurately - isCurrentlyTouched, if true ignore button presses
    private final List<GraphActionObserver> observers = new ArrayList<>();

    public State(GraphVisualization visualization, Rectangle rectangle, GraphAction graphAction) {
        this.visualization = visualization;
        this.rectangle = rectangle;
        this.graphAction = graphAction;
    }

    public Point getCoordinates(Vertex v) { return visualization.getVertexPoint(v); }
    public Rectangle getRectangle() { return rectangle; }
    public GraphAction getGraphAction() { return graphAction; }
    public boolean isCurrentlyModified() { return currentlyModified; }

    public void setRectangle(Rectangle rectangle) { this.rectangle = rectangle; }
    public void setGraphAction(GraphAction graphAction) {
        this.graphAction = graphAction;
        tellObservers();
    }
    public void setCurrentlyModified(boolean value) { this.currentlyModified = value; }

    public synchronized void addObserver(GraphActionObserver observer) {
        observers.add(observer);
    }

    public synchronized void removeObserver(GraphActionObserver observer) {
        observers.remove(observer);
    }

    private synchronized void tellObservers() {
        for (GraphActionObserver o : observers) {
            o.update(graphAction);
        }
    }
}
