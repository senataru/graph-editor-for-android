package com.example.graph_editor.model.mathematics;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Geometry {
    public static boolean close(double a, double b) {
        double NEARLY_ZERO = 0.000001;
        return a-b < NEARLY_ZERO && b-a < NEARLY_ZERO;
    }
    public static double distance(Point a, Point b) {
        double xDiff = a.x-b.x;
        double yDiff = a.y-b.y;
        return Math.sqrt( xDiff*xDiff + yDiff*yDiff );
    }

    // distance from point x to segment ab
    public static double distanceFromSegment(Point x, Point a, Point b) {
        if(a.equals(b))
            return distance(x, b);
        Point ax = new Point(x.x-a.x, x.y-a.y);
        Point ab = new Point(b.x-a.x, b.y-a.y);
        double projection = (ab.x*ax.x + ab.y*ax.y)/ab.length();
        double distance = min(max(projection, 0), ab.length());
        return distance(x, getOnSegment(a, b, distance));
    }

    // point in distance 'distance' from start to target
    public static Point getOnSegment(Point start, Point target, double distance) {
        if(start.equals(target))
            throw new RuntimeException("Frame size equals 0 or is negative");
        double part = distance / distance(start, target);
        return new Point(start.x+(target.x-start.x)*part, start.y+(target.y-start.y)*part);
    }
}
