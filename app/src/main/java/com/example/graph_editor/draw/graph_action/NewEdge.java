package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.example.graph_editor.point_mapping.PointMapper;
import com.example.graph_editor.point_mapping.ScreenPoint;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import graph_editor.geometry.GeometryUtils;
import graph_editor.geometry.Point;
import graph_editor.graph.SimpleGraphBuilder;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class NewEdge extends GraphExpansion {
    private ScreenPoint sp1;
    private ScreenPoint sp2;
    @Override
    public boolean perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> sp1 = new ScreenPoint(event.getX(), event.getY());
            case MotionEvent.ACTION_MOVE -> sp2 = new ScreenPoint(event.getX(), event.getY());
            case MotionEvent.ACTION_UP -> {
                GraphVisualization<PropertySupportingGraph> visualization = execute(mapper, stack.getCurrent());
                if (visualization != null) { stack.push(visualization); }
            }
            default -> { }
        }
        return true;
    }

    @Override
    protected GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous) {
        Map<Point, Vertex> inverse = previous
                .getVisualization()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Point p1 = GeometryUtils.findClosestPoint(mapper.mapFromView(sp1), inverse.keySet());
        Point p2 = GeometryUtils.findClosestPoint(mapper.mapFromView(sp2), inverse.keySet());
        if (p1 == null || p2 == null || p1 == p2) {
            return null;
        } else {
            PropertySupportingGraph graph = previous.getGraph();
            var builder = new SimpleGraphBuilder(graph.getVertices().size());
            builder.addEdge(inverse.get(p1).getIndex(), inverse.get(p2).getIndex());
            BuilderVisualizer visualizer = new BuilderVisualizer();
            PropertyGraphBuilder propertyGraphBuilder = deBuild(graph, builder, visualizer, previous.getVisualization());
            return visualizer.generateVisual(propertyGraphBuilder.build());
        }
    }
}
//    class NewEdge implements GraphAction {
//        @Override
//        public boolean perform(GraphView view, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, GraphOnTouchMutation mutation) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    stack.backup();
//                    Vertex nearest = DrawManager.getNearestVertex(data.graph, data.rectangle, data.currentRelativePoint, 0.1, Collections.emptySet());
//                    data.edgeFirst = data.graph.addVertex();
//                    if (nearest != null) {
//                        data.edgeFirst.setPoint(nearest.getPoint());
//                        data.edgeFirstSnap = nearest;
//                        data.edgeSecondSnap = nearest;
//                    } else {
//                        data.edgeFirst.setPoint(data.currentAbsolutePoint);
//                        data.edgeFirstSnap = null;
//                        data.edgeSecondSnap = null;
//                    }
//                    data.edgeSecond = data.graph.addVertex();
//                    data.edgeSecond.setPoint(data.edgeFirst.getPoint());
//                    data.graph.addEdge(data.edgeFirst, data.edgeSecond);
//                    return true;
//                case MotionEvent.ACTION_MOVE:
//                    Set<Vertex> excluded = new HashSet<>();
//                    excluded.add(data.edgeFirst);
//                    excluded.add(data.edgeSecond);
//                    Vertex nearestViable = DrawManager.getNearestVertex(data.graph, data.rectangle, data.currentRelativePoint, 0.1, excluded);
//
//                    if (nearestViable != null) {
//                        data.edgeSecond.setPoint(nearestViable.getPoint());
//                        data.edgeSecondSnap = nearestViable;
//                    } else {
//                        data.edgeSecond.setPoint(data.currentAbsolutePoint);
//                        data.edgeSecondSnap = null;
//                    }
//                    return true;
//                case MotionEvent.ACTION_UP:
//                    if (data.edgeFirst == DrawManager.getNearestVertex(data.graph, data.rectangle, data.currentRelativePoint, 0.05, new HashSet<>(Collections.singleton(data.edgeSecond)))) {
//                        data.graph.removeVertex(data.edgeFirst);
//                        data.graph.removeVertex(data.edgeSecond);
//                    } else {
//                        // did first vertex snap?
//                        if (data.edgeFirstSnap != null) {
//                            data.graph.removeVertex(data.edgeFirst);
//                            data.edgeFirst = data.edgeFirstSnap;
//                            data.graph.addEdge(data.edgeFirst, data.edgeSecond);
//                        }
//                        // did second vertex snap?
//                        if (data.edgeSecondSnap != null) {
//                            data.graph.removeVertex(data.edgeSecond);
//                            data.edgeSecond = data.edgeSecondSnap;
//                            data.graph.addEdge(data.edgeFirst, data.edgeSecond);
//                        }
//                    }
//                    data.edgeFirst = data.edgeFirstSnap = data.edgeSecond = data.edgeSecondSnap = null;
//                    return false;
//            }
//            return false;
//        }
//    }