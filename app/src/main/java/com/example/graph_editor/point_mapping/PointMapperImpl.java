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

    //TODO implement
    @Override
    public ScreenPoint mapIntoView(Point point) {
        return null;
    }

    @Override
    public Point mapFromView(ScreenPoint screenPoint) {
        return null;
    }
}
