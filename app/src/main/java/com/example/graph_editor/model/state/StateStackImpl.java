package com.example.graph_editor.model.state;

import com.example.graph_editor.model.Graph;

import java.util.ArrayList;
import java.util.List;


//pointer always points towards what should be drawn right now
public class StateStackImpl implements StateStack {
    private final List<Graph> stack;
    private final State currentState;
    private final Runnable invalidateFunction;

    private int pointer;

    public StateStackImpl(Runnable invalidateFunction, State initialState) {
        stack = new ArrayList<>();
        this.invalidateFunction = invalidateFunction;
        stack.add(initialState.getGraph());
        currentState = initialState;
        pointer = 0;
        invalidateView();
    }

    @Override
    public State undo() {
        pointer = Math.max(pointer-1, 0);
        currentState.setGraph(stack.get(pointer));
        invalidateView();
        return currentState;
    }

    @Override
    public State redo() {
        pointer = Math.min(pointer+1, stack.size()-1);
        currentState.setGraph(stack.get(pointer));
        invalidateView();
        return currentState;
    }

//    private void put(Graph g) {
//        stack.subList(pointer + 1, stack.size()).clear();
//        stack.add(g);
//        pointer += 1;
//    }

    @Override
    public void backup() {
        Graph g = currentState.getGraph();
        if (pointer >= 1) {
            stack.subList(pointer, stack.size()).clear();
            stack.add(g.deepCopy());
            stack.add(g);
            pointer += 1;
        } else {
            stack.clear();
            stack.add(currentState.getGraph().deepCopy());
            stack.add(currentState.getGraph());
            pointer = 1;
        }
        invalidateView();
    }

    @Override
    public boolean isUndoPossible() {
        return pointer != 0;
    }

    @Override
    public boolean isRedoPossible() {
        return pointer != stack.size()-1;
    }

    @Override
    public State getCurrentState() {
        return currentState;
    }

    @Override
    public void invalidateView() {
        invalidateFunction.run();
    }
}
