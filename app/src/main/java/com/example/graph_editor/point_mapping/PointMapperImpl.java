package com.example.graph_editor.point_mapping;

import android.view.View;

import com.example.graph_editor.model.mathematics.Rectangle;

import graph_editor.geometry.Point;
import graph_editor.point_mapping.PointMapper;
import graph_editor.point_mapping.ScreenPoint;

public class PointMapperImpl implements PointMapper {
    private final View view;
    private final Rectangle rectangle;

    public PointMapperImpl(View view, Rectangle rectangle) {
        this.view = view;
        this.rectangle = rectangle;
    }

    @Override
    public ScreenPoint mapIntoView(Point point) {
        double x = (point.getX() - rectangle.getLeft()) * view.getWidth() / rectangle.getWidth();
        double y = (point.getY() - rectangle.getTop()) * view.getHeight() / rectangle.getHeight();

        return new ScreenPoint((float) x, (float) y);
    }

    @Override
    public Point mapFromView(ScreenPoint screenPoint) {
        double x = rectangle.getLeft() + screenPoint.getX() * rectangle.getWidth() / view.getWidth();
        double y = rectangle.getTop() + screenPoint.getY() * rectangle.getHeight() / view.getHeight();

        return new Point(x,y);
    }
}
