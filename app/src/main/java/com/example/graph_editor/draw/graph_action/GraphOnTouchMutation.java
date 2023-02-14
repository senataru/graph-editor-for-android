package com.example.graph_editor.draw.graph_action;

import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

abstract class GraphOnTouchMutation implements GraphAction {
    protected abstract GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous);
}
