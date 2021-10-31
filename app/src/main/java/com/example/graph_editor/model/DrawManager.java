package com.example.graph_editor.model;

import com.example.graph_editor.draw.Frame;
import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.List;

public class DrawManager {
    Point leftTop;
    double width;
    double height;
    List<Vertex> vertices;

    public DrawManager(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void setFrame(Frame frame) {
        //uses the Frame as if it was a struct, doesn't remember the reference
        //maybe error when too close
        Point leftTop = frame.getLeftTop();
        Point rightBot = frame.getRightBot();
        this.leftTop = leftTop;
        this.width = rightBot.getX() - leftTop.getX();
        this.height = rightBot.getY() - leftTop.getY();
        updateCurrentPoints();
    }

    private void updateCurrentPoints() {
        for(Vertex vertex : vertices) {
            vertex.setRelativePoint(scale(vertex.getAbsolutePoint()));
        }
    }

    private Point scale(Point point) {
        double x = (point.getX() - leftTop.getX())/width;
        double y = (point.getY() - leftTop.getY())/ height;
        return new Point(x, y);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        List<Edge> result = new ArrayList<>();
        for(Vertex vertex : vertices) {
            result.addAll(vertex.getEdges());
        }
        return result;
    }
}
