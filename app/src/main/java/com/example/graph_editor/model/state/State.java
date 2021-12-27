package com.example.graph_editor.model.state;

import com.example.graph_editor.draw.Frame;
import com.example.graph_editor.model.Graph;

public class State {
    private Graph graph;
    private Frame frame;

    public State(Graph graph, Frame frame) {
        this.graph = graph;
        this.frame = frame;
    }

    public Graph getGraph() { return graph; }
    public Frame getFrame() { return frame; }

    public void setGraph(Graph graph) { this.graph = graph; }
    public void setFrame(Frame frame) { this.frame = frame; }

    public State deepCopy() {
        return new State(graph.deepCopy(), frame.deepCopy());
    }
}
