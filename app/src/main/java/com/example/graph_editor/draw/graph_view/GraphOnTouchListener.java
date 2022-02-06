package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.draw.action_mode_type.ActionModeType;
import com.example.graph_editor.model.mathematics.Frame;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.model.state.StateStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GraphOnTouchListener implements View.OnTouchListener {

    private final Context context;
    private final GraphView graphView;
    private final StateStack stateStack;
    private final ScaleGestureDetector scaleDetector;

    // global variables for easier management

    private Graph graph;
    private Frame frame;
    private Point relativePoint;
    private Point absolutePoint;
    private Vertex highlighted;
    private ActionModeType oldStylusActionMode;

    public GraphOnTouchListener(Context context, GraphView graphView, StateStack stateStack, ScaleGestureDetector scaleDetector) {
        this.context = context;
        this.graphView = graphView;
        this.stateStack = stateStack;
        this.scaleDetector = scaleDetector;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        State currentState = stateStack.getCurrentState();
        frame = currentState.getFrame();
        graph = currentState.getGraph();

        relativePoint = graphView.getRelative(new Point(event.getX(), event.getY()));
        absolutePoint = DrawManager.getAbsolute(frame.getRectangle(), relativePoint);
        highlighted = graphView.highlighted;
        boolean stylusMode = Settings.getStylus(context);

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    oldStylusActionMode = currentState.getActionModeType();
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
//        scaleDetector.onTouchEvent(event);

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentState.setCurrentModeType(oldStylusActionMode);
                    oldStylusActionMode = null;
                }
            }
        }

        graphView.highlighted = highlighted;
        graphView.postInvalidate();
        return result;
    }

    private boolean actionNewVertex(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateStack.backup();
                highlighted = graph.addVertex();
                highlighted.setPoint(absolutePoint);
                break;
            case MotionEvent.ACTION_MOVE:
                highlighted.setPoint(absolutePoint);
                break;
            case MotionEvent.ACTION_UP:
                highlighted = null;
                break;
            default:
                break;
        }
        return true;
    }

    private Vertex edgeFirst = null;
    private Vertex edgeFirstSnap = null;
    private Vertex edgeSecond = null;
    private Vertex edgeSecondSnap = null;
    private boolean actionNewEdge(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateStack.backup();
                Vertex nearest = DrawManager.getNearestVertex(graph, frame.getRectangle(), relativePoint, 0.1, Collections.emptySet());
                edgeFirst = graph.addVertex();
                if (nearest != null) {
                    edgeFirst.setPoint(nearest.getPoint());
                    edgeFirstSnap = nearest;
                    edgeSecondSnap = nearest;
                } else {
                    edgeFirst.setPoint(absolutePoint);
                    edgeFirstSnap = null;
                    edgeSecondSnap = null;
                }
                edgeSecond = graph.addVertex();
                edgeSecond.setPoint(edgeFirst.getPoint());
                graph.addEdge(edgeFirst, edgeSecond);
                return true;
            case MotionEvent.ACTION_MOVE:
                Set<Vertex> excluded = new HashSet<>();
                excluded.add(edgeFirst);
                excluded.add(edgeSecond);
                Vertex nearestViable = DrawManager.getNearestVertex(graph, frame.getRectangle(), relativePoint, 0.1, excluded);

                if (nearestViable != null) {
                    edgeSecond.setPoint(nearestViable.getPoint());
                    edgeSecondSnap = nearestViable;
                } else {
                    edgeSecond.setPoint(absolutePoint);
                    edgeSecondSnap = null;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (edgeFirst == DrawManager.getNearestVertex(graph, frame.getRectangle(), relativePoint, 0.05, new HashSet<>(Collections.singleton(edgeSecond)))) {
                    graph.removeVertex(edgeFirst);
                    graph.removeVertex(edgeSecond);
                } else {
                    // did first ver-tex snap?
                    if (edgeFirstSnap != null) {
                        graph.removeVertex(edgeFirst);
                        edgeFirst = edgeFirstSnap;
                        graph.addEdge(edgeFirst, edgeSecond);
                    }
                    // did second vertex snap?
                    if (edgeSecondSnap != null) {
                        graph.removeVertex(edgeSecond);
                        edgeSecond = edgeSecondSnap;
                        graph.addEdge(edgeFirst, edgeSecond);
                    }
                }
                edgeFirst = edgeFirstSnap = edgeSecond = edgeSecondSnap = null;
                return false;
        }
        return false;
    }

    private boolean actionMoveObject(View v, MotionEvent e) {
        Vertex nearest = DrawManager.getNearestVertex(graph, frame.getRectangle(), relativePoint, 0.1, Collections.emptySet());

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateStack.backup();
                if (highlighted == null && nearest != null)       // select a vertex
                    highlighted = nearest;
                else if (highlighted == nearest && highlighted != null) // move current vertex slightly
                    highlighted.setPoint(DrawManager.getAbsolute(frame.getRectangle(), relativePoint));
                else if (nearest != null)     // select different vertex
                    highlighted = nearest;
                else if (highlighted != null)       // move selected vertex
                    highlighted.setPoint(DrawManager.getAbsolute(frame.getRectangle(), relativePoint));
                break;
            case MotionEvent.ACTION_MOVE:
                if (highlighted != null) {
                    highlighted.setPoint(DrawManager.getAbsolute(frame.getRectangle(), relativePoint));
                }
                break;
            case MotionEvent.ACTION_UP:
                highlighted = null;
                break;
        }
        return true;
    }

    private boolean actionRemoveObject(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            stateStack.backup();
        }

        Vertex nearestVertex;
        while (null != (nearestVertex = DrawManager.getNearestVertex(graph, frame.getRectangle(), relativePoint, 0.03, Collections.emptySet()))) {
            graph.removeVertex(nearestVertex);
        }
        Edge nearestEdge;
        while (null != (nearestEdge = DrawManager.getNearestEdge(graph, frame.getRectangle(), relativePoint, 0.03))) {
            graph.removeEdge(nearestEdge);
        }

        return true;
    }

    private float prevX = 0f;
    private float prevY = 0f;
    private float dx = 0f;
    private float dy = 0f;
    private boolean actionMoveCanvas(View v, MotionEvent e) {
        System.out.println("MOVE");
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                stateStack.backup();   //only graph changes are backed up
                prevX = e.getX();
                prevY = e.getY();
                dx = 0;
                dy = 0;
                int indexA = (e.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                firstPointerId = e.getPointerId(indexA);
                prevAbsolute = DrawManager.getAbsolute(stateStack.getCurrentState().getFrame().getRectangle(),
                        graphView.getRelative(new Point(e.getX(indexA), e.getY(indexA))));
                break;
            case MotionEvent.ACTION_MOVE:
                dx = e.getX() - prevX;
                dy = e.getY() - prevY;
                prevX = e.getX();
                prevY = e.getY();
//                prevAbsolute = absolutePoint;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // initialize zoom variables
                initial = stateStack.getCurrentState().getFrame().deepCopy();
                int indexB = (e.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                secondPointerId = e.getPointerId(indexB);
                secondAbsoluteStart = DrawManager.getAbsolute(stateStack.getCurrentState().getFrame().getRectangle(),
                        graphView.getRelative(new Point(e.getX(indexB), e.getY(indexB))));
                firstAbsoluteStart = prevAbsolute;
//                firstRelativeEnd = firstAbsoluteStart;
//                secondRelativeEnd = secondAbsoluteStart;
                stateStack.getCurrentState().setCurrentModeType(ActionModeType.ZOOM_CANVAS);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        frame.translate(dx/v.getWidth(), dy/v.getWidth());
        return true;
    }

    private Point prevAbsolute = null;
    private Frame initial;
    private int firstPointerId;
    private int secondPointerId;
    private Point firstAbsoluteStart;
    private Point secondAbsoluteStart;
    private Point firstRelativeEnd;
    private Point secondRelativeEnd;
    private boolean actionZoomCanvas(View v, MotionEvent e) {
        int id = e.getPointerId(e.getActionIndex());
        System.out.println("ZOOM");
        System.out.println("A" + firstPointerId);
        System.out.println("B" + secondPointerId);
        System.out.println(id);
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                int indexA = e.findPointerIndex(firstPointerId);
                int indexB = e.findPointerIndex(secondPointerId);

                if (indexA == -1 || indexB == -1) break;

                firstRelativeEnd = graphView.getRelative(new Point(e.getX(indexA), e.getY(indexA)));
                secondRelativeEnd = graphView.getRelative(new Point(e.getX(indexB), e.getY(indexB)));

//                if (firstRelativeEnd == null || secondRelativeEnd == null) break;

//                StringBuilder s = new StringBuilder();
                Frame newFrame = DrawManager.getZoomedRectangle(initial, firstAbsoluteStart, secondAbsoluteStart, firstRelativeEnd, secondRelativeEnd);
//                s.append("ELO\n");
//                s.append(firstAbsoluteStart.toString() + "\n\n");
//                s.append(secondAbsoluteStart.toString() + "\n\n");
//                s.append(firstRelativeEnd.toString() + "\n\n");
//                s.append(secondRelativeEnd.toString() + "\n\n");
//                s.append("old\n");
//                s.append(frame.getRectangle().getLeftTop() + "\n\n");
//                s.append(frame.getRectangle().getRightBot() + "\n\n");
//                s.append(frame.getScale() + "\n\n");
//                s.append("new\n");
//                s.append(newFrame.getRectangle().getLeftTop() + "\n\n");
//                s.append(newFrame.getRectangle().getRightBot() + "\n\n");
//                s.append(newFrame.getScale() + "\n\n");
//                System.out.println(new String(s));
                stateStack.getCurrentState().setFrame(newFrame);

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
