package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.draw.graph_action.MoveCanvas;
import com.example.graph_editor.draw.graph_action.NewVertex;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.point_mapping.PointMapper;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class GraphOnTouchListener implements View.OnTouchListener {
    private final Context context;
    private final GraphView graphView;
    private final VersionStack<GraphVisualization<PropertySupportingGraph>> stack;
    private final State state;
    private final PointMapper mapper;

    GraphOnTouchListenerData data;

    public GraphOnTouchListener(Context context, GraphView graphView, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, State state, PointMapper mapper) {
        this.context = context;
        this.graphView = graphView;
        this.stack = stack;
        this.state = state;
        this.mapper = mapper;

        this.data = new GraphOnTouchListenerData();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        boolean stylusMode = Settings.getStylus(context);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            state.setCurrentlyModified(true);
        }

        GraphAction oldAction= null;
        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    oldAction = state.getGraphAction();
                    state.setGraphAction(new MoveCanvas());
                }
            }
        }

        boolean result = state.getGraphAction().perform(mapper, event, stack);

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    state.setGraphAction(oldAction);
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
