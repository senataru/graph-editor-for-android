package com.example.graph_editor.draw.graph_view;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.graphics.Path;

import com.example.graph_editor.model.GraphType;

import java.util.List;
import java.util.Map;

import graph_editor.draw.IGraphDrawer;
import graph_editor.draw.point_mapping.CanvasDrawer;
import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.draw.point_mapping.ScreenPoint;
import graph_editor.geometry.Point;
import graph_editor.graph.Edge;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class DefaultDrawer implements IGraphDrawer<PropertySupportingGraph> {
    private static final float defaultVertexSize = 20.0f;
    private static final int defaultVertexColor = 0x000000ff;
    private static final int defaultEdgeColor = 0x7f000000;
    private static final int defaultArrowColor = 0x00ff0000;
    private static final int defaultArrowRadius = 50;
    private static final double defaultArrowAngle = PI / 6;
    private final PointMapper mapper;
    private final CanvasDrawer drawer;
    private final GraphType type;

    public DefaultDrawer(PointMapper mapper, CanvasDrawer drawer, GraphType type) {
        this.mapper = mapper;
        this.drawer = drawer;
        this.type = type;
    }

    @Override
    public void drawGraph(GraphVisualization<PropertySupportingGraph> graphVisualization) {
        PropertySupportingGraph graph = graphVisualization.getGraph();
        Map<Vertex, Point> coordinates = graphVisualization.getVisualization();
        for (Vertex v : graph.getVertices()) {
            ScreenPoint sp = mapper.mapIntoView(coordinates.get(v));
            drawer.drawCircle(sp, defaultVertexSize, defaultVertexColor);
        }
        for (Edge e : graph.getEdges()) {
            ScreenPoint sp1 = mapper.mapIntoView(coordinates.get(e.getSource()));
            ScreenPoint sp2 = mapper.mapIntoView(coordinates.get(e.getTarget()));
            drawer.drawLine(sp1, sp2, defaultEdgeColor);
            if (type == GraphType.DIRECTED) {
                float lineAngleRad = (float) (atan2(sp2.getY() - sp1.getY(), sp2.getX() - sp1.getX()));
                List<ScreenPoint> vertices = List.of(
                        sp2,
                        new ScreenPoint(
                                (float)(sp2.getX() - defaultArrowRadius * cos(lineAngleRad - (defaultArrowAngle / 2.0))),
                                (float)(sp2.getY() - defaultArrowRadius * sin(lineAngleRad - (defaultArrowAngle / 2.0)))
                        ),
                        new ScreenPoint(
                                (float)(sp2.getX() - defaultArrowRadius * cos(lineAngleRad + (defaultArrowAngle / 2.0))),
                                (float)(sp2.getY() - defaultArrowRadius * sin(lineAngleRad + (defaultArrowAngle / 2.0)))
                        )
                );

                drawer.drawPolygon(vertices, defaultArrowColor);
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
            }
        }
    }
}
