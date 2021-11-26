package com.example.graph_editor.draw.graph_view;

import android.view.MotionEvent;
import android.view.View;

import com.example.graph_editor.draw.ActionModeType;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;

import java.util.Collections;
import java.util.HashSet;

public class GraphOnTouchListenerImpl implements GraphOnTouchListener {

    private final DrawManager manager;
    private final GraphView graphView;

    // global variables for easier management
    private final Graph graph;

    private Point relativePoint;
    private Point absolutePoint;
    private Vertex highlighted;
    private Vertex nearest;
    private Vertex newVertex;
    private Vertex nearestNotNew;

    public GraphOnTouchListenerImpl(GraphView graphView, DrawManager manager) {
        this.graphView = graphView;
        this.manager = manager;

        this.graph = manager.getGraph();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        relativePoint = graphView.getRelative(new Point(event.getX(), event.getY()));
        absolutePoint = manager.getAbsolute(relativePoint);
        highlighted = graphView.highlighted;
        nearest = manager.getNearestVertex(relativePoint, 0.1, Collections.emptySet());
        nearestNotNew = manager.getNearestVertex(relativePoint, 0.1, new HashSet<>(Collections.singleton(newVertex)));
        ActionModeType currentType = ActionModeType.getCurrentModeType();

        boolean result;

        switch (currentType) {
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
            default:
                result = false;
                break;
        }

        manager.updateFrame(graphView.frame);       //TODO: manager update method without frame
        graphView.highlighted = highlighted;
        graphView.postInvalidate();
        return result;
    }

    private boolean actionNewVertex(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
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

    Vertex edgeFirst = null;
    private boolean actionNewEdge(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (edgeFirst == null) {
                    if (nearest != null) {
                        edgeFirst = nearest;
                    } else {
                        edgeFirst = graph.addVertex();
                        edgeFirst.setPoint(absolutePoint);
                    }
                    highlighted = edgeFirst;
                } else {
                    if (edgeFirst == nearest) {
                        edgeFirst = null;
                        highlighted = null;
                    } else {
                        newVertex = graph.addVertex();
                        newVertex.setPoint(absolutePoint);
                        highlighted = newVertex;
                        manager.getGraph().addEdge(edgeFirst, newVertex);
                        if (nearest != null) {
                            newVertex.setPoint(nearest.getPoint());
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (edgeFirst != null) {
                    if (newVertex != null) {
                        if (nearestNotNew != null) {
                            newVertex.setPoint(nearestNotNew.getPoint());
                        } else {
                            newVertex.setPoint(absolutePoint);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (nearestNotNew == edgeFirst) {
                    edgeFirst = null;
                }
                if (edgeFirst != null && newVertex != null) {
                    if (nearestNotNew != null) {
                        graph.removeVertex(newVertex);
                        graph.addEdge(edgeFirst, nearestNotNew);
                    }
                    edgeFirst = null;
                    newVertex = null;
                }
                highlighted = edgeFirst;
                break;
        }
        return false;
    }

    private boolean actionMoveObject(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (highlighted == null && nearest != null)       // select a vertex
                    highlighted = nearest;
                else if (highlighted == nearest && highlighted != null) // move current vertex slightly
                    highlighted.setPoint(manager.getAbsolute(relativePoint));
                else if (nearest != null)     // select different vertex
                    highlighted = nearest;
                else if (highlighted != null)       // move selected vertex
                    highlighted.setPoint(manager.getAbsolute(relativePoint));
                break;
            case MotionEvent.ACTION_MOVE:
                if (highlighted != null) {
                    highlighted.setPoint(manager.getAbsolute(relativePoint));
                }
                break;
            case MotionEvent.ACTION_UP:
                highlighted = null;
                break;
        }
        return true;
    }

    private boolean actionRemoveObject(View v, MotionEvent e) {
        Edge nearestEdge = manager.getNearestEdge(relativePoint, 0.03);
        Vertex nearestVertex = manager.getNearestVertex(relativePoint, 0.03, Collections.emptySet());

        if (nearestVertex != null)
            graph.removeVertex(nearestVertex);
        if (nearestEdge != null)
            graph.removeEdge(nearestEdge);

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            default:
                break;
        }
        return true;
    }
}
