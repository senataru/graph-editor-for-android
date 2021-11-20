package com.example.graph_editor.draw;

import android.util.Pair;

import com.example.graph_editor.model.mathematics.Point;

public class Frame {
    private final Point originalLeftTop;
    private final Point originalRightBot;
    private Point leftTop;
    private Point rightBot;
    private double scale;
    private double dx;
    private double dy;

    public Frame(Point leftTop, Point rightBot) {
        this.originalLeftTop = leftTop;
        this.originalRightBot = rightBot;
        this.leftTop = leftTop;
        this.rightBot = rightBot;
        this.scale = 1;
    }

    public Pair<Point, Point> getPoints() {
        return new Pair<>(leftTop, rightBot);
    }

    public void setScale(double s) {
        double rescale = s/this.scale;

        double dx = rightBot.getX() - leftTop.getX();
        double dy = rightBot.getY() - leftTop.getY();

        double ddx = dx * (rescale-1)/2;
        double ddy = dy * (rescale-1)/2;

        leftTop = new Point(leftTop.getX() - ddx, leftTop.getY() - ddy);
        rightBot = new Point(rightBot.getX() + ddx, rightBot.getY() + ddy);

        this.scale = (leftTop.getX()-rightBot.getX())/(originalLeftTop.getX()-originalRightBot.getX());
    }

    public void translate(double dxNew, double dyNew) {
        double ddx = (this.dx - dxNew) * scale;
        double ddy = (this.dy - dyNew) * scale;

        leftTop = new Point(leftTop.getX() + ddx, leftTop.getY() + ddy);
        rightBot = new Point(rightBot.getX() + ddx, rightBot.getY() + ddy);
        this.dx = dxNew;
        this.dy = dyNew;
    }

    public double getScale() { return scale; }
}
