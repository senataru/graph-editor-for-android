package com.example.graph_editor.model.state;

import com.example.graph_editor.draw.graph_action.GraphActionObserver;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.mathematics.Rectangle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import graph_editor.geometry.Point;

public class State {
    private GraphAction graphAction;
    private boolean currentlyModified; //more accurately - isCurrentlyTouched, if true ignore button presses
    private final List<GraphActionObserver> observers = new ArrayList<>();

    public State(GraphAction graphAction) {
        this.graphAction = graphAction;
    }
    public GraphAction getGraphAction() { return graphAction; }
    public boolean isCurrentlyModified() { return currentlyModified; }

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
