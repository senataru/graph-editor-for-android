package com.example.graph_editor.point_mapping;

import graph_editor.geometry.Point;

public interface PointMapper {
    ScreenPoint mapIntoView(Point point);
    Point mapFromView(ScreenPoint screenPoint);
    Point getOffset();
    void setOffset(Point offset);
    void zoomBy(float heightPixels);
    void rotate(float heightPixels, float screenX);
}
