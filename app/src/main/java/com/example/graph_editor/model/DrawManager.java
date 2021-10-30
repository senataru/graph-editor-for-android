package com.example.graph_editor.model;

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

    public void setFrame(Point leftTop, Point rightBot) {
        //maybe error when too close
        this.leftTop = leftTop;
        this.width = rightBot.getX() - leftTop.getX();
        this.height = rightBot.getY() - leftTop.getY();
        updateCurrentPoints();
    }

    private void updateCurrentPoints() {
        for(Vertex vertex : vertices) {
            vertex.setCurrentPoint(scale(vertex.getPoint()));
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
