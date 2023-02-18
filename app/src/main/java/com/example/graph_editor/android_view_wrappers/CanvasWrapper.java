package com.example.graph_editor.android_view_wrappers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import graph_editor.draw.point_mapping.CanvasDrawer;
import graph_editor.draw.point_mapping.ScreenPoint;

public class CanvasWrapper implements CanvasDrawer {
    private final Canvas canvas;
    private final Paint vertexPaint = new Paint();
    private final Paint edgePaint = new Paint();
    private final Paint shapePaint = new Paint();
    public CanvasWrapper(Canvas canvas) {
        this.canvas = canvas;
        vertexPaint.setStyle(Paint.Style.FILL);
        vertexPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(0.01f * canvas.getWidth());
        edgePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        shapePaint.setStrokeWidth(2);
    }

    @Override
    public void drawCircle(ScreenPoint screenPoint, float radius, int color) {
        setColor(vertexPaint, color);
        canvas.drawCircle(screenPoint.getX(), screenPoint.getY(), radius, vertexPaint);
    }

    @Override
    public void drawLine(ScreenPoint start, ScreenPoint stop, int color) {
        setColor(edgePaint, color);
        canvas.drawLine(start.getX(), start.getY(), stop.getX(), stop.getY(), edgePaint);
    }

    @Override
    public void drawPolygon(Iterable<ScreenPoint> iterable, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStrokeWidth(2);
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        setColor(shapePaint, color);
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
        path.close();
        canvas.drawPath(path, paint);
    }

    private static void setColor(Paint p, int color) {
        p.setARGB(color >> 24, (color >> 16) & 255, (color >> 8) & 255, color & 255);
    }
}
