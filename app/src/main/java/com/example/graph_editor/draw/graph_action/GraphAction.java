package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import com.example.graph_editor.point_mapping.PointMapper;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public interface GraphAction {
    boolean perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack);
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
//    class MoveObject implements GraphAction {
//        @Override
//        public boolean perform(GraphView view, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, GraphOnTouchMutation mutation) {
//            Vertex nearest = DrawManager.getNearestVertex(data.graph, data.rectangle, data.currentRelativePoint, 0.1, Collections.emptySet());
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    stack.backup();
//                    if (data.movedVertex == null && nearest != null)       // select a vertex
//                        data.movedVertex = nearest;
//                case MotionEvent.ACTION_MOVE:
//                    if (data.movedVertex != null) {
//                        data.movedVertex.setPoint(DrawManager.getAbsolute(data.rectangle, data.currentRelativePoint));
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    data.movedVertex = null;
//                    break;
//            }
//            return true;
//        }
//    }
//    class RemoveObject implements GraphAction {
//        @Override
//        public boolean perform(GraphView view, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, GraphOnTouchMutation mutation) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                stack.backup();
//            }
//
//            Vertex nearestVertex;
//            while (null != (nearestVertex = DrawManager.getNearestVertex(data.graph, data.rectangle, data.currentRelativePoint, 0.03, Collections.emptySet()))) {
//                data.graph.removeVertex(nearestVertex);
//            }
//            Edge nearestEdge;
//            while (null != (nearestEdge = DrawManager.getNearestEdge(data.graph, data.rectangle, data.currentRelativePoint, 0.03))) {
//                data.graph.removeEdge(nearestEdge);
//            }
//
//            return true;
//        }
//    }

//    class ZoomCanvas implements GraphAction {
//        @Override
//        public boolean perform(GraphView view, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, GraphOnTouchMutation mutation) {
//            int indexA = event.findPointerIndex(data.firstPointerId);
//            int indexB = event.findPointerIndex(data.secondPointerId);
//
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_MOVE:
//                    if (indexA == -1 || indexB == -1) data.zoomIsDeactivated = true;
//                    if (data.zoomIsDeactivated) break;
//
//                    Point firstRelativeEnd = view.getRelative(new Point(event.getX(indexA), event.getY(indexA)));
//                    Point secondRelativeEnd = view.getRelative(new Point(event.getX(indexB), event.getY(indexB)));
//
//                    Rectangle newRectangle = DrawManager.getZoomedRectangle(data.zoomInitialRectangle, data.zoomFirstAbsoluteStart, data.zoomSecondAbsoluteStart, firstRelativeEnd, secondRelativeEnd);
//                    stack.getCurrentState().setRectangle(newRectangle);
//
//                    break;
//                case MotionEvent.ACTION_POINTER_DOWN:
//                case MotionEvent.ACTION_POINTER_UP:
//                    data.zoomIsDeactivated = true;
//                    break;
//                case MotionEvent.ACTION_UP:
//                    stack.getCurrentState().setGraphAction(new MoveCanvas());
//                    break;
//            }
//            return true;
//        }
//    }
}
