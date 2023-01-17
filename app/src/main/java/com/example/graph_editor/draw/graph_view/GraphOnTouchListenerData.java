package com.example.graph_editor.draw.graph_view;

import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.mathematics.Rectangle;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.Vertex;
import graph_editor.properties.PropertyGraphBuilder;

public class GraphOnTouchListenerData {
    // globals
    public PropertyGraphBuilder builder;
    public Rectangle rectangle;
    public Point currentRelativePoint;
    public Point currentAbsolutePoint;

    public GraphAction stylusActionMode;

    // new vertex
    public Vertex newVertex;

    // move object
    public Vertex movedVertex;

    // new edge
    public Vertex edgeFirst = null;
    public Vertex edgeFirstSnap = null;
    public Vertex edgeSecond = null;
    public Vertex edgeSecondSnap = null;

    // move canvas
    public float movePreviousX = 0f;
    public float movePreviousY = 0f;
    public float moveDeltaX = 0f;
    public float moveDeltaY = 0f;

    // zoom canvas
    public Point previousAbsolutePoint = null;
    public int firstPointerId = -1;
    public int secondPointerId = -1;

    public Rectangle zoomInitialRectangle = null;
    public Point zoomFirstAbsoluteStart = null;
    public Point zoomSecondAbsoluteStart = null;
    public boolean zoomIsDeactivated = false;
}
