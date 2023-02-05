package com.example.graph_editor.draw.graph_action;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.example.graph_editor.point_mapping.PointMapper;
import com.example.graph_editor.point_mapping.ScreenPoint;

import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class ZoomCanvas implements GraphAction {
    private float downY;
    @Override
    public GraphVisualization<PropertySupportingGraph> perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                downY = event.getY();
            }
            case MotionEvent.ACTION_MOVE -> {
                float diffY = downY - event.getY();
                downY = event.getY();
                mapper.zoomBy(diffY);
            }
            default -> { }
        }
        return stack.getCurrent();
    }
}
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
