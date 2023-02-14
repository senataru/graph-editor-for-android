package com.example.graph_editor.draw.graph_view;

import android.view.MotionEvent;
import android.view.View;

import com.example.graph_editor.draw.DrawActivity;
import com.example.graph_editor.draw.NavigationButtonCollection;
import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.draw.graph_action.MoveCanvas;

import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class GraphOnTouchListener implements GraphView.OnTouchListener {
    private final DrawActivity activity;
    private final GraphView graphView;
    private final VersionStack<GraphVisualization<PropertySupportingGraph>> stack;
    private final NavigationButtonCollection buttonCollection;
    private final PointMapper mapper;

    public GraphOnTouchListener(DrawActivity activity, GraphView graphView, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, NavigationButtonCollection buttonCollection, PointMapper mapper) {
        this.activity = activity;
        this.graphView = graphView;
        this.stack = stack;
        this.buttonCollection = buttonCollection;
        this.mapper = mapper;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        boolean stylusMode = Settings.getStylus(activity);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            activity.setLocked(true);
        }

        GraphAction action = buttonCollection.getGraphAction();

        if (stylusMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    action = new MoveCanvas();
                }
            }
        }

        GraphVisualization<PropertySupportingGraph> visualization = action.perform(mapper, event, stack);
        graphView.notifyChange(visualization);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            activity.setLocked(false);
        }
        return true;
    }
}
