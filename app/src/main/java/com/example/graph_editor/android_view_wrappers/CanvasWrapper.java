package com.example.graph_editor.android_view_wrappers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import graph_editor.draw.point_mapping.CanvasDrawer;
import graph_editor.draw.point_mapping.ScreenPoint;

public class CanvasWrapper implements CanvasDrawer {
    private final Canvas canvas;

    public CanvasWrapper(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void drawCircle(ScreenPoint screenPoint, float radius, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(screenPoint.getX(), screenPoint.getY(), radius, paint);
    }

    @Override
    public void drawLine(ScreenPoint start, ScreenPoint stop, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawLine(start.getX(), start.getY(), stop.getX(), stop.getY(), paint);
    }

    @Override
    public void drawPolygon(Iterable<ScreenPoint> iterable, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        ScreenPoint first = null;
        for (ScreenPoint p : iterable) {
            if (first == null) {
                first = p;
                path.moveTo(p.getX(), p.getY());
            } else {
                path.lineTo(p.getX(), p.getY());
            }
        }
        if (first != null) {
            path.lineTo(first.getX(), first.getY());
        }
        canvas.drawPath(path, paint);
    }
}
