package com.example.graph_editor.draw.graph_view;

import com.example.graph_editor.draw.action_mode_type.ActionModeType;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;

public class OnTouchListenerData {
    // globals
    public Graph graph;
    public Rectangle rectangle;
    public Point relativePoint;
    public Point absolutePoint;
    public ActionModeType oldStylusActionMode;

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
    public float prevX = 0f;
    public float prevY = 0f;
    public float dx = 0f;
    public float dy = 0f;

    // zoom canvas
    public Point prevAbsolute;
    public Rectangle initial;
    public int firstPointerId;
    public int secondPointerId;
    public Point firstAbsoluteStart;
    public Point secondAbsoluteStart;
}
