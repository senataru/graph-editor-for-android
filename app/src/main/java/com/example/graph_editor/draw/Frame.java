package com.example.graph_editor.draw;

import com.example.graph_editor.model.mathematics.Point;

public class Frame {
    private Point leftTop;
    private Point rightBot;

    public Frame(Point leftTop, Point rightBot) {
        this.leftTop = leftTop;
        this.rightBot = rightBot;
    }

    public Point getLeftTop() { return leftTop; }
    public Point getRightBot() { return rightBot; }

}
