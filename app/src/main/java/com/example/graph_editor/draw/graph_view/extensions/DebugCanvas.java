package com.example.graph_editor.draw.graph_view.extensions;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DebugCanvas extends Canvas {
    private Canvas canvas;

    public static DebugCanvas INSTANCE = new DebugCanvas();
    public Canvas own(Canvas canvas) {
        this.canvas = canvas;
        return this;
    }
    @Override
    public int getWidth() {
        return canvas.getWidth();
    }
    @Override
    public int getHeight() {
        return canvas.getHeight();
    }
    @Override
    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        System.out.println(cx + " " + cy + " " + radius);
        System.out.println(paint.getColor());
        System.out.println(paint.getAlpha());
        System.out.println(paint.getStyle());
        System.out.println(paint.getFlags());
        canvas.drawCircle(cx, cy, radius, paint);
    }
    @Override
    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        System.out.println(startX + " " + startY + " " + stopX + " " + stopY);
        System.out.println(paint.getColor());
        System.out.println(paint.getAlpha());
        System.out.println(paint.getStyle());
        System.out.println(paint.getFlags());
        System.out.println(paint.getStrokeWidth());
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    public static void printD(double e) {
        System.out.println(e);
    }
    public static void printF(float e) {
        System.out.println(e);
    }
}
