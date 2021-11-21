package com.example.graph_editor.model.mathematics;

public class Point {
    public static Point ZERO = new Point(0, 0);
    final double x, y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double length() { return Geometry.distance(ZERO, this); }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return Geometry.close(other.getX(), getX()) && Geometry.close(other.getY(), getY());
    }
}
