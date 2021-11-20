package com.example.graph_editor.model;

import android.util.Pair;

import com.example.graph_editor.draw.Frame;
import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.List;

public class DrawManager {
    private Point leftTop;
    private double width;
    private double height;
    private final Graph g;

    public DrawManager(Graph g) {
        this.g = g;
    }

    public void updateFrame(Frame frame) {
        //uses the Frame as if it was a struct, doesn't remember the reference
        //maybe error when too close
        Pair<Point, Point> points = frame.getPoints();
        Point leftTop = points.first;
        Point rightBot = points.second;
        this.leftTop = leftTop;
        this.width = rightBot.getX() - leftTop.getX();
        this.height = rightBot.getY() - leftTop.getY();
        updateCurrentPoints();
    }

    private void updateCurrentPoints() {
        for(Vertex vertex : g.getVertices()) {
            vertex.setRelativePoint(scale(vertex.getAbsolutePoint()));
        }
    }

    private Point scale(Point point) {
        double x = (point.getX() - leftTop.getX())/width;
        double y = (point.getY() - leftTop.getY())/height;
        return new Point(x, y);
    }

    public List<Vertex> getVertices() { return g.getVertices(); }

    public List<Edge> getEdges() { return g.getEdges(); }
}
