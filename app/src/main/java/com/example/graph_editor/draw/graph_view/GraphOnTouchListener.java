package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.state.State;

import graph_editor.geometry.Point;

public class GraphOnTouchListener implements View.OnTouchListener {
    private final Context context;
    private final GraphView graphView;
    private final StateStack stateStack;

    GraphOnTouchListenerData data;

    public GraphOnTouchListener(Context context, GraphView graphView, StateStack stateStack) {
        this.context = context;
        this.graphView = graphView;
        this.stateStack = stateStack;

        this.data = new GraphOnTouchListenerData();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        State currentState = stateStack.getCurrentState();
        data.rectangle = currentState.getRectangle();
        data.graph = currentState.getGraph();

        data.currentRelativePoint = graphView.getRelative(new Point(event.getX(), event.getY()));
        data.currentAbsolutePoint = DrawManager.getAbsolute(data.rectangle, data.currentRelativePoint);

//        if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
//            Toast.makeText(context, "Multitouch detected", Toast.LENGTH_SHORT).show();
//        }

        boolean stylusMode = Settings.getStylus(context);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            data.firstPointerId = event.getPointerId(event.getActionIndex());
            currentState.setCurrentlyModified(true);
        }


        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    data.stylusActionMode = currentState.getGraphAction();
                    currentState.setGraphAction(new GraphAction.MoveCanvas());
                }
            }
        }

        boolean result = currentState.getGraphAction().perform(v, event, stateStack, data, graphView);

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentState.setGraphAction(data.stylusActionMode);
                    data.stylusActionMode = null;
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            currentState.setCurrentlyModified(false);
        }

        data.previousAbsolutePoint = data.currentAbsolutePoint;

        graphView.postInvalidate();
        return result;
    }
}
