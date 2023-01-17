package com.example.graph_editor.model.state;

import com.example.graph_editor.draw.graph_action.GraphActionObserver;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.mathematics.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Graph graph;
    private Rectangle rectangle;
    private GraphAction graphAction;
    private boolean currentlyModified; //more accurately - isCurrentlyTouched, if true ignore button presses
    private final List<GraphActionObserver> observers = new ArrayList<>();

    public State(Graph graph, Rectangle rectangle, GraphAction graphAction) {
        this.graph = graph;
        this.rectangle = rectangle;
        this.graphAction = graphAction;
    }

    public Graph getGraph() { return graph; }
    public Rectangle getRectangle() { return rectangle; }
    public GraphAction getGraphAction() { return graphAction; }
    public boolean isCurrentlyModified() { return currentlyModified; }

    public void setGraph(Graph graph) { this.graph = graph; }
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
