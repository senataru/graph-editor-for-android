package com.example.graph_editor.draw.graph_action;


import android.view.MotionEvent;

import androidx.annotation.NonNull;

import graph_editor.graph.VersionStack;
import graph_editor.point_mapping.PointMapper;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

//TODO implement
class MoveCanvas extends GraphOnTouchMutation {
    @Override
    public boolean perform(PointMapper mapper, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack) {
        return false;
    }

    @Override
    protected GraphVisualization<PropertySupportingGraph> execute(PointMapper mapper, GraphVisualization<PropertySupportingGraph> previous) {
        return null;
    }
//        @Override
//        public boolean perform(GraphView view, @NonNull MotionEvent event, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, GraphOnTouchMutation mutation) {
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_DOWN:
//                    data.movePreviousX = event.getX();
//                    data.movePreviousY = event.getY();
//                    data.moveDeltaX = 0f;
//                    data.moveDeltaY = 0f;
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    data.moveDeltaX = event.getX() - data.movePreviousX;
//                    data.moveDeltaY = event.getY() - data.movePreviousY;
//                    data.movePreviousX = event.getX();
//                    data.movePreviousY = event.getY();
//                    break;
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    // going into zoom mode, initialize zoom variables
//                    int indexB = event.getActionIndex();
//                    data.zoomInitialRectangle = stack.getCurrentState().getRectangle().deepCopy();
//                    data.secondPointerId = event.getPointerId(indexB);
//                    data.zoomFirstAbsoluteStart = data.previousAbsolutePoint;
//                    data.zoomSecondAbsoluteStart = DrawManager.getAbsolute(stack.getCurrentState().getRectangle(),
//                            view.getRelative(new Point(event.getX(indexB), event.getY(indexB))));
//                    data.zoomIsDeactivated = false;
//                    stack.getCurrentState().setGraphAction(new ZoomCanvas());
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//            DrawManager.translate(data.rectangle, data.moveDeltaX/view.getWidth(), data.moveDeltaY/view.getWidth());
//            return true;
//        }
}
