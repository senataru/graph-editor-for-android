package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.state.State;

import graph_editor.geometry.Point;
import graph_editor.graph.VersionStack;
import graph_editor.visual.GraphVisualization;

public class GraphOnTouchListener implements View.OnTouchListener {
    private final Context context;
    private final GraphView graphView;
    private final VersionStack<GraphVisualization> stack;
    private final State state;

    GraphOnTouchListenerData data;

    public GraphOnTouchListener(Context context, GraphView graphView, VersionStack<GraphVisualization> stack, State state) {
        this.context = context;
        this.graphView = graphView;
        this.stack = stack;
        this.state = state;

        this.data = new GraphOnTouchListenerData();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        data.rectangle = state.getRectangle();
//        data.graph = graphStack.getCurrentGraph();

        data.currentRelativePoint = graphView.getRelative(new Point(event.getX(), event.getY()));
        data.currentAbsolutePoint = DrawManager.getAbsolute(data.rectangle, data.currentRelativePoint);

//        if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
//            Toast.makeText(context, "Multitouch detected", Toast.LENGTH_SHORT).show();
//        }

        boolean stylusMode = Settings.getStylus(context);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            data.firstPointerId = event.getPointerId(event.getActionIndex());
            state.setCurrentlyModified(true);
        }


        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    data.stylusActionMode = state.getGraphAction();
                    state.setGraphAction(new GraphAction.MoveCanvas());
                }
            }
        }

        boolean result = state.getGraphAction().perform(graphView, event, stack, data);

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    state.setGraphAction(data.stylusActionMode);
                    data.stylusActionMode = null;
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            state.setCurrentlyModified(false);
        }

        data.previousAbsolutePoint = data.currentAbsolutePoint;

        graphView.postInvalidate();
        return result;
    }
}
