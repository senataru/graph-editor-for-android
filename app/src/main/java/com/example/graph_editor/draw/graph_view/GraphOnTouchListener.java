package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.draw.action_mode_type.ActionModeType;
import com.example.graph_editor.draw.popups.MessagePopup;
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

    private Integer currentlyManagedTool = null;

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

//        if (event.getAction() == MotionEvent.ACTION_DOWN && currentlyManagedTool == null) {
//            currentlyManagedTool = event.getToolType(0);
//            currentState.setCurrentlyModified(true);
//        }
//
//        if (event.getToolType(0) != currentlyManagedTool) return true;


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

//        StringBuilder s = new StringBuilder();
////        s.append("managed tool: ");
////        s.append(currentlyManagedTool);
//        s.append("\nstylus mode: ");
//        s.append(stylusMode);
//        s.append("\nactual tool: ");
//        s.append(event.getToolType(0));
//        s.append("\nactionMode: ");
//        s.append(currentState.getActionModeType());
//        if (Settings.getButtons(context))
//            new MessagePopup(context, new String(s)).show();


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
            default:
                result = false;
                break;
        }
        scaleDetector.onTouchEvent(event);

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentState.setCurrentModeType(oldStylusActionMode);
                    oldStylusActionMode = null;
                }
            }
        }

//        if (event.getAction() == MotionEvent.ACTION_UP && currentlyManagedTool == event.getToolType(0)) {
//            currentlyManagedTool = null;
//            currentState.setCurrentlyModified(false);
//        }

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

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }
    private float prevX = 0f;
    private float prevY = 0f;
    private float dx = 0f;
    private float dy = 0f;
    private Mode mode = Mode.NONE;
    private boolean actionMoveCanvas(View v, MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                stateStack.backup();   //only graph changes are backed up
                mode = Mode.DRAG;
                prevX = e.getX();
                prevY = e.getY();
                dx = 0;
                dy = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                dx = e.getX() - prevX;
                dy = e.getY() - prevY;
                prevX = e.getX();
                prevY = e.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = Mode.ZOOM;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_UP:
                mode = Mode.NONE;
                break;
        }
        if (mode == Mode.DRAG)
            frame.translate(dx/v.getWidth(), dy/v.getWidth());
        return true;
    }
}
