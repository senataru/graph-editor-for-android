package com.example.graph_editor.model.state;

import com.example.graph_editor.draw.graph_action.GraphActionObserver;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.mathematics.Rectangle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graph_editor.geometry.Point;
import graph_editor.graph.Vertex;
import graph_editor.visual.GraphVisualization;

public class State {
    private Rectangle rectangle;
    private GraphAction graphAction;
    private boolean currentlyModified; //more accurately - isCurrentlyTouched, if true ignore button presses
    private final List<GraphActionObserver> observers = new ArrayList<>();

    public State(Rectangle rectangle, GraphAction graphAction) {
        this.rectangle = rectangle;
        this.graphAction = graphAction;
    }

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

    public static final long serialVersionUID = 2L;
    void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeLong(serialVersionUID);

        oos.writeDouble(rectangle.getLeftTop().getX());
        oos.writeDouble(rectangle.getLeftTop().getY());
        oos.writeDouble(rectangle.getRightBot().getX());
        oos.writeDouble(rectangle.getRightBot().getY());
    }

    void readObject(ObjectInputStream ois) throws IOException {
        long serialUID = ois.readLong();
        if (serialUID != serialVersionUID) {
            throw new IOException("Incorrect serialization version: " + serialUID + ", expected " + serialVersionUID);
        }

        var tl = new Point(ois.readDouble(), ois.readDouble());
        var rb = new Point(ois.readDouble(), ois.readDouble());
        rectangle = new Rectangle(tl, rb);
    }
}
