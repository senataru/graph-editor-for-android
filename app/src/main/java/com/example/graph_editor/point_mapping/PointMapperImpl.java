package com.example.graph_editor.point_mapping;

import android.view.View;

import graph_editor.geometry.Point;

public class PointMapperImpl implements PointMapper {
    private final View view;
    private Point offset;
    private double zoom;

    public PointMapperImpl(View view, Point offset) {
        this.view = view;
        this.offset = offset;
        this.zoom = 1.0;
    }

    @Override
    public ScreenPoint mapIntoView(Point point) {
        double x = (point.getX() - offset.getX()) * view.getWidth() * zoom;
        double y = (point.getY() - offset.getY()) * view.getHeight() * zoom;

        return new ScreenPoint((float) x, (float) y);
    }

    @Override
    public Point mapFromView(ScreenPoint screenPoint) {
        double x = offset.getX() + screenPoint.getX() / (view.getWidth() * zoom);
        double y = offset.getY() + screenPoint.getY() / (view.getHeight() * zoom);

        return new Point(x,y);
    }

    @Override
    public Point getOffset() {
        return offset;
    }

    @Override
    public void setOffset(Point offset) {
        this.offset = offset;
    }

    @Override
    public void zoomBy(double factor) {
        zoom *= factor;
    }
}
