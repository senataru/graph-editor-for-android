package com.example.graph_editor.extensions;

import java.util.Optional;
import graph_editor.draw.IGraphDrawer;
import graph_editor.draw.point_mapping.CanvasDrawer;
import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.properties.PropertySupportingGraph;

public interface DrawerSource {
    Optional<IGraphDrawer<PropertySupportingGraph>> getDrawer(PointMapper mapper, CanvasDrawer canvasDrawer);
}