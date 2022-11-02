package com.example.graph_editor.draw;

import android.graphics.Canvas;

import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;

public class Utils {
    public static Point canvasPoint(Point absolutePoint, Rectangle rectangle, Canvas canvas) {
        return new Point(
                (absolutePoint.getX() - rectangle.getLeft()) * canvas.getWidth() / rectangle.getWidth(),
                (absolutePoint.getY() - rectangle.getTop()) * canvas.getHeight() / rectangle.getHeight()
        );
    }
}
