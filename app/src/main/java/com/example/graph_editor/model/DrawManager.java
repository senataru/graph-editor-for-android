package com.example.graph_editor.model;

import android.util.Pair;

import com.example.graph_editor.draw.Frame;
import com.example.graph_editor.model.mathematics.Geometry;
import com.example.graph_editor.model.mathematics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


// !! needs initialising in form of setting frame at least once
public class DrawManager {
    private Point leftTop;
    private double width;
    private double height;
    private final Graph graph;
    private boolean initialised = false;

    public DrawManager(Graph graph) {
        this.graph = graph;
    }

    public boolean isInitialised() { return initialised; }

    public Graph getGraph() {
        return graph;
    }

    public void updateFrame(Frame frame) {
        //uses the Frame as if it was a struct, doesn't remember the reference
        Pair<Point, Point> points = frame.getPoints();
        Point leftTop = points.first;
        Point rightBot = points.second;
        this.leftTop = leftTop;
        this.width = rightBot.getX() - leftTop.getX();
        this.height = rightBot.getY() - leftTop.getY();
        if(new Point(width, height).equals(Point.ZERO)) {
            throw new RuntimeException("Frame size equals 0 or is negative");
        }
        initialised = true;
    }

    public Point getRelative(Point point) {
        double x = (point.getX() - leftTop.getX())/width;
        double y = (point.getY() - leftTop.getY())/height;
        return new Point(x, y);
    }

    public Point getAbsolute(Point point) {
        double x = leftTop.getX() + point.getX() * width;
        double y = leftTop.getY() + point.getY() * height;
        return new Point(x, y);
    }
    public double getRelativeDistanceFrom(Point relativePoint, Vertex vertex) {
        return Geometry.distance(relativePoint, getRelative(vertex.getPoint()));
    }
    public double getRelativeDistanceFrom(Point relativePoint, Edge edge) {
        return Geometry.distanceFromSegment(relativePoint,
                getRelative(edge.getSource().getPoint()), getRelative(edge.getTarget().getPoint()));
    }

    //returns null if there are no vertices
    public Vertex getNearestVertex(Point relativePoint, double delta, Set<Vertex> excluded) {
        Point point = getAbsolute(relativePoint);
        double nearest = Double.MAX_VALUE;
        Vertex result = null;
        for(Vertex vertex : graph.getVertices()) {
            if (excluded.contains(vertex))
                continue;
            double distance = Geometry.distance(point, vertex.getPoint());
            if( distance < nearest){
                result = vertex;
                nearest = distance;
            }
        }

        if (result != null && getRelativeDistanceFrom(relativePoint, result) > delta)
            return null;
        return result;
    }

    //returns null if there are no edges
    public Edge getNearestEdge(Point relativePoint, double delta) {
        Point point = getAbsolute(relativePoint);
        double nearest = Double.MAX_VALUE;
        Edge result = null;
        for(Edge edge : graph.getEdges()) {
            double distance = Geometry.distanceFromSegment(point,
                    edge.getSource().getPoint(), edge.getTarget().getPoint());
            if( distance < nearest){
                result = edge;
                nearest = distance;
            }
        }
        if (result != null && getRelativeDistanceFrom(relativePoint, result) > delta)
            return null;

        return result;
    }

    public Frame getOptimalAndUpdateFrame(double paddingPercent, Frame frame) {
        updateFrame(frame);

        List<Vertex> vertices = graph.getVertices();
        if(vertices.isEmpty()){
            return new Frame(new Point(0,0), new Point(1, 1));
        }
        Point extremeLeftTop = new Point(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        Point extremeRightBot = new Point(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        for(Vertex vertex : graph.getVertices()) {
            Point point = vertex.getPoint();
            if(point.getX()<extremeLeftTop.getX())
                extremeLeftTop = new Point(point.getX(), extremeLeftTop.getY());
            if(point.getY()>extremeLeftTop.getY())
                extremeLeftTop = new Point(extremeLeftTop.getX(), point.getY());
            if(point.getX()>extremeRightBot.getX())
                extremeRightBot = new Point(point.getX(), extremeRightBot.getY());
            if(point.getY()<extremeRightBot.getY())
                extremeRightBot = new Point(extremeRightBot.getX(), point.getY());
        }

        double extremeWidth = extremeRightBot.getX() - extremeLeftTop.getX();
        double extremeHeight = extremeLeftTop.getY() - extremeRightBot.getY() ;
        double scale = Math.max(extremeWidth/width, extremeHeight/height);
        Point resultLeftTop = new Point(extremeLeftTop.getX()-paddingPercent*width*scale,
                extremeLeftTop.getY()+paddingPercent*height*scale);
        Point resultRightBot = new Point(resultLeftTop.getX()+width*scale*(1+2*paddingPercent),
                resultLeftTop.getY()-height*scale*(1+2*paddingPercent));

        if(vertices.size() <= 1) {
            resultLeftTop = new Point(resultLeftTop.getX()-1, resultLeftTop.getY()+1);
            resultRightBot = new Point(resultRightBot.getX()+1, resultRightBot.getY()-1);
        }

        Frame result = new Frame(resultLeftTop, resultRightBot);
        updateFrame(result);
        return result;
    }

    public List<Vertex> getVertices() { return graph.getVertices(); }

    public List<Edge> getEdges() { return graph.getEdges(); }
}
