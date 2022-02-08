package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.draw.action_mode_type.ActionModeType;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.model.state.StateStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GraphOnTouchListener implements View.OnTouchListener {

    private final Context context;
    private final GraphView graphView;
    private final StateStack stateStack;

    OnTouchListenerData data;

    public GraphOnTouchListener(Context context, GraphView graphView, StateStack stateStack) {
        this.context = context;
        this.graphView = graphView;
        this.stateStack = stateStack;

        this.data = new OnTouchListenerData();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        State currentState = stateStack.getCurrentState();
        data.rectangle = currentState.getRectangle();
        data.graph = currentState.getGraph();

        data.relativePoint = graphView.getRelative(new Point(event.getX(), event.getY()));
        data.absolutePoint = DrawManager.getAbsolute(data.rectangle, data.relativePoint);
        boolean stylusMode = Settings.getStylus(context);

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    data.oldStylusActionMode = currentState.getActionModeType();
                    currentState.setCurrentModeType(ActionModeType.MOVE_CANVAS);
                }
            }
        }

        boolean result;
        switch (currentState.getActionModeType()) {
            case NEW_VERTEX:
                result = actionNewVertex(v, event);
                break;
            case NEW_EDGE:
                result = actionNewEdge(v, event);
                break;
            case MOVE_OBJECT:
                result = actionMoveObject(v, event);
                break;
            case REMOVE_OBJECT:
                result = actionRemoveObject(v, event);
                break;
            case MOVE_CANVAS:
                result = actionMoveCanvas(v, event);
                break;
            case ZOOM_CANVAS:
                result = actionZoomCanvas(v, event);
                break;
            default:
                result = false;
                break;
        }

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentState.setCurrentModeType(data.oldStylusActionMode);
                    data.oldStylusActionMode = null;
                }
            }
        }

        graphView.postInvalidate();
        return result;
    }

    private boolean actionNewVertex(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateStack.backup();
                data.newVertex = data.graph.addVertex();
                data.newVertex.setPoint(data.absolutePoint);
                break;
            case MotionEvent.ACTION_MOVE:
                data.newVertex.setPoint(data.absolutePoint);
                break;
            case MotionEvent.ACTION_UP:
                data.newVertex = null;
                break;
            default:
                break;
        }
        return true;
    }

    private boolean actionNewEdge(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateStack.backup();
                Vertex nearest = DrawManager.getNearestVertex(data.graph, data.rectangle, data.relativePoint, 0.1, Collections.emptySet());
                data.edgeFirst = data.graph.addVertex();
                if (nearest != null) {
                    data.edgeFirst.setPoint(nearest.getPoint());
                    data.edgeFirstSnap = nearest;
                    data.edgeSecondSnap = nearest;
                } else {
                    data.edgeFirst.setPoint(data.absolutePoint);
                    data.edgeFirstSnap = null;
                    data.edgeSecondSnap = null;
                }
                data.edgeSecond = data.graph.addVertex();
                data.edgeSecond.setPoint(data.edgeFirst.getPoint());
                data.graph.addEdge(data.edgeFirst, data.edgeSecond);
                return true;
            case MotionEvent.ACTION_MOVE:
                Set<Vertex> excluded = new HashSet<>();
                excluded.add(data.edgeFirst);
                excluded.add(data.edgeSecond);
                Vertex nearestViable = DrawManager.getNearestVertex(data.graph, data.rectangle, data.relativePoint, 0.1, excluded);

                if (nearestViable != null) {
                    data.edgeSecond.setPoint(nearestViable.getPoint());
                    data.edgeSecondSnap = nearestViable;
                } else {
                    data.edgeSecond.setPoint(data.absolutePoint);
                    data.edgeSecondSnap = null;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (data.edgeFirst == DrawManager.getNearestVertex(data.graph, data.rectangle, data.relativePoint, 0.05, new HashSet<>(Collections.singleton(data.edgeSecond)))) {
                    data.graph.removeVertex(data.edgeFirst);
                    data.graph.removeVertex(data.edgeSecond);
                } else {
                    // did first ver-tex snap?
                    if (data.edgeFirstSnap != null) {
                        data.graph.removeVertex(data.edgeFirst);
                        data.edgeFirst = data.edgeFirstSnap;
                        data.graph.addEdge(data.edgeFirst, data.edgeSecond);
                    }
                    // did second vertex snap?
                    if (data.edgeSecondSnap != null) {
                        data.graph.removeVertex(data.edgeSecond);
                        data.edgeSecond = data.edgeSecondSnap;
                        data.graph.addEdge(data.edgeFirst, data.edgeSecond);
                    }
                }
                data.edgeFirst = data.edgeFirstSnap = data.edgeSecond = data.edgeSecondSnap = null;
                return false;
        }
        return false;
    }

    private boolean actionMoveObject(View v, MotionEvent e) {
        Vertex nearest = DrawManager.getNearestVertex(data.graph, data.rectangle, data.relativePoint, 0.1, Collections.emptySet());

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateStack.backup();
                if (data.movedVertex == null && nearest != null)       // select a vertex
                    data.movedVertex = nearest;
            case MotionEvent.ACTION_MOVE:
                if (data.movedVertex != null) {
                    data.movedVertex.setPoint(DrawManager.getAbsolute(data.rectangle, data.relativePoint));
                }
                break;
            case MotionEvent.ACTION_UP:
                data.movedVertex = null;
                break;
        }
        return true;
    }

    private boolean actionRemoveObject(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            stateStack.backup();
        }

        Vertex nearestVertex;
        while (null != (nearestVertex = DrawManager.getNearestVertex(data.graph, data.rectangle, data.relativePoint, 0.03, Collections.emptySet()))) {
            data.graph.removeVertex(nearestVertex);
        }
        Edge nearestEdge;
        while (null != (nearestEdge = DrawManager.getNearestEdge(data.graph, data.rectangle, data.relativePoint, 0.03))) {
            data.graph.removeEdge(nearestEdge);
        }

        return true;
    }


    private boolean actionMoveCanvas(View v, MotionEvent e) {
        System.out.println("MOVE");
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                data.prevX = e.getX();
                data.prevY = e.getY();
                data.dx = 0;
                data.dy = 0;
                int indexA = (e.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                data.firstPointerId = e.getPointerId(indexA);
                data.prevAbsolute = DrawManager.getAbsolute(stateStack.getCurrentState().getRectangle(),
                        graphView.getRelative(new Point(e.getX(indexA), e.getY(indexA))));
                break;
            case MotionEvent.ACTION_MOVE:
                data.dx = e.getX() - data.prevX;
                data.dy = e.getY() - data.prevY;
                data.prevX = e.getX();
                data.prevY = e.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // initialize zoom variables
                data.initial = stateStack.getCurrentState().getRectangle().deepCopy();
                int indexB = (e.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                data.secondPointerId = e.getPointerId(indexB);
                data.secondAbsoluteStart = DrawManager.getAbsolute(stateStack.getCurrentState().getRectangle(),
                        graphView.getRelative(new Point(e.getX(indexB), e.getY(indexB))));
                data.firstAbsoluteStart = data.prevAbsolute;
                stateStack.getCurrentState().setCurrentModeType(ActionModeType.ZOOM_CANVAS);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        DrawManager.translate(data.rectangle, data.dx/v.getWidth(), data.dy/v.getWidth());
        return true;
    }

    private boolean actionZoomCanvas(View v, MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                int indexA = e.findPointerIndex(data.firstPointerId);
                int indexB = e.findPointerIndex(data.secondPointerId);

                if (indexA == -1 || indexB == -1) break;

                Point firstRelativeEnd = graphView.getRelative(new Point(e.getX(indexA), e.getY(indexA)));
                Point secondRelativeEnd = graphView.getRelative(new Point(e.getX(indexB), e.getY(indexB)));

                Rectangle newRectangle = DrawManager.getZoomedRectangle(data.initial, data.firstAbsoluteStart, data.secondAbsoluteStart, firstRelativeEnd, secondRelativeEnd);
                stateStack.getCurrentState().setRectangle(newRectangle);

            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_UP:
                stateStack.getCurrentState().setCurrentModeType(ActionModeType.MOVE_CANVAS);
                break;
        }

        return true;
    }
}
