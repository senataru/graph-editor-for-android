package com.example.graph_editor.point_mapping;

import android.view.View;

import graph_editor.geometry.Point;

public class PointMapperImpl implements PointMapper {
    private final View view;
    private Point offset;
    private double zoom;
    private double rotation;

    public PointMapperImpl(View view, Point offset) {
        this.view = view;
        this.offset = offset;
        this.zoom = 1.0;
    }

    @Override
    public ScreenPoint mapIntoView(Point point) {
        double x = (point.getX() - offset.getX()) * (view.getWidth() * zoom) + view.getWidth() / 2.0;
        double y = (point.getY() - offset.getY()) * (view.getHeight() * zoom) + view.getHeight() / 2.0;

        return rotatePoint(new ScreenPoint((float) x, (float) y), rotation);
    }

    @Override
    public Point mapFromView(ScreenPoint screenPoint) {
        var rotated = rotatePoint(screenPoint, -rotation);
        double x = offset.getX() + (rotated.getX() - view.getWidth() / 2.0) / (view.getWidth() * zoom);
        double y = offset.getY() + (rotated.getY() - view.getHeight() / 2.0) / (view.getHeight() * zoom);

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
    public void zoomBy(float heightPixels) {
        zoom *= Math.exp(heightPixels / view.getHeight());
    }
    @Override
    public void rotate(float heightPixels, float screenX) {
        rotation += (screenX <= view.getWidth() / 2.0 ? 1 : -1) * Math.PI * heightPixels / view.getHeight();
        if (rotation > 2 * Math.PI) {
            rotation -= 2 * Math.PI;
        } else if (rotation < 0.0) {
            rotation += 2 * Math.PI;
        }
    }

    private ScreenPoint rotatePoint(ScreenPoint point, double angle) {
        float dx = point.getX() - view.getWidth() / 2.0f;
        float dy = point.getY() - view.getHeight() / 2.0f;
        return new ScreenPoint(
                (float) (dx * Math.cos(angle) - dy * Math.sin(angle)) + view.getWidth() / 2.0f,
                (float) (dx * Math.sin(angle) + dy * Math.cos(angle)) + view.getHeight() / 2.0f
        );
    }
}
