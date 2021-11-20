package com.example.graph_editor.draw;

import com.example.graph_editor.model.mathematics.Point;

public class Frame {
    private Point leftTop;
    private Point rightBot;
    private double scale;

    public Frame(Point leftTop, Point rightBot) {
        this.leftTop = leftTop;
        this.rightBot = rightBot;
        this.scale = 1;
    }

    public Point getLeftTop() { return leftTop; }
    public Point getRightBot() { return rightBot; }

//    public void scale(double x_scale, double y_scale) {
//        double dx = rightBot.getX() - leftTop.getX();
//        double dy = rightBot.getY() - leftTop.getY();
//
//        double ddx = dx * (x_scale-1)/2;
//        double ddy = dy * (y_scale-1)/2;
//
//        leftTop = new Point(leftTop.getX() - ddx, leftTop.getY() - ddy);
//        rightBot = new Point(rightBot.getX() + ddx, rightBot.getY() + ddy);
//
//        System.out.println(leftTop.getX());
//        System.out.println(leftTop.getY());
//        System.out.println(rightBot.getX());
//        System.out.println(rightBot.getY());
//    }

    public void setScale(double s) {
        double rescale = s/this.scale;

        double dx = rightBot.getX() - leftTop.getX();
        double dy = rightBot.getY() - leftTop.getY();

        double ddx = dx * (rescale-1)/2;
        double ddy = dy * (rescale-1)/2;

        leftTop = new Point(leftTop.getX() - ddx, leftTop.getY() - ddy);
        rightBot = new Point(rightBot.getX() + ddx, rightBot.getY() + ddy);

        this.scale = s;
    }

    public void translate(double dx, double dy) {
        double ddx = (rightBot.getX() - leftTop.getX()) * dx;
        double ddy = (rightBot.getY() - leftTop.getY()) * dy;

        leftTop = new Point(leftTop.getX() + ddx, leftTop.getY() + ddy);
        rightBot = new Point(rightBot.getX() + ddx, rightBot.getY() + ddy);
    }
}
