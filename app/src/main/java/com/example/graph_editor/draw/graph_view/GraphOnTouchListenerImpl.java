package com.example.graph_editor.draw.graph_view;

import android.view.MotionEvent;
import android.view.View;

import com.example.graph_editor.draw.ActionModeType;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;

public class GraphOnTouchListenerImpl implements GraphOnTouchListener {

    private final DrawManager manager;
    private final GraphView graphView;

    // global variables for easier management
    private final Graph graph;

    private Point relativePoint;
    private Point absolutePoint;
    private Vertex highlighted;
    private Vertex selected;
    private Vertex newVertex;
    private ActionModeType currentType;

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
        selected = manager.getNearestVertex(relativePoint, 0.1);
        newVertex = null;
        currentType = ActionModeType.getCurrentModeType();

        boolean result;

        switch (currentType) {
            case NEW_VERTEX:
                result = actionNewVertex(v, event);
                break;
            case NEW_EDGE:
                result = actionNewEdge(v, event);
                break;
            case MOVE_OBJECT:
                result = actionMoveObject(v, event, true);
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

    private boolean actionNewEdge(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (highlighted == null) {
                if (selected == null) {
                    newVertex = graph.addVertex();
                    newVertex.setPoint(absolutePoint);
                    highlighted = newVertex;
                    return true;
                } else {
                    highlighted = selected;
                }
            } else {
                if (highlighted == selected) {
                    highlighted = null;
                } else {
                    boolean createdNew = selected == null;
                    if (selected == null) {
                        newVertex = graph.addVertex();
                        newVertex.setPoint(absolutePoint);
                        selected = newVertex;
                    }
                    manager.getGraph().addEdge(highlighted, selected);
                    highlighted = selected;
                    return createdNew;
                }
            }
        } else {
            return actionMoveObject(v, e, false);
        }
        return false;
    }

    private boolean actionMoveObject(View v, MotionEvent e, boolean dehighlight) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (highlighted == null && selected != null)       // select a vertex
                    highlighted = selected;
                else if (highlighted == selected && highlighted != null) // move current vertex slightly
                    highlighted.setPoint(manager.getAbsolute(relativePoint));
                else if (selected != null)     // select different vertex
                    highlighted = selected;
                else if (highlighted != null)       // move selected vertex
                    highlighted.setPoint(manager.getAbsolute(relativePoint));
                break;
            case MotionEvent.ACTION_MOVE:
                if (highlighted != null) {
                    highlighted.setPoint(manager.getAbsolute(relativePoint));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dehighlight)
                    highlighted = null;
                break;
        }
        return true;
    }
}
