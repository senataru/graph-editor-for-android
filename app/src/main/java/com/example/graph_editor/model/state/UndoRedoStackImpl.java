package com.example.graph_editor.model.state;

import com.example.graph_editor.draw.Frame;
import com.example.graph_editor.model.Graph;

import java.util.ArrayList;


//pointer always points towards what should be drawn right now
public class UndoRedoStackImpl implements UndoRedoStack {
    private final ArrayList<State> stack;
    private final Runnable invalidateFunction;

    private int pointer;

    public UndoRedoStackImpl(Runnable invalidateFunction) {
        stack = new ArrayList<>();
        this.invalidateFunction = invalidateFunction;
        pointer = -1;
    }

    @Override
    public State undo() {
        pointer = Math.max(pointer-1, 0);
        invalidateFunction.run();
        return stack.get(pointer);
    }

    @Override
    public State redo() {
        pointer = Math.min(pointer+1, stack.size()-1);
        invalidateFunction.run();
        return stack.get(pointer);
    }

    @Override
    public void put(State elem) {
        stack.subList(pointer + 1, stack.size()).clear();
        stack.add(elem);
        pointer += 1;
        invalidateFunction.run();
    }

    @Override
    public void backup() {
        State current = getCurrentState();
        if (pointer >= 1) {
            undo();
            put(current.deepCopy());
            put(current);
        } else {
            stack.clear();
            stack.add(current.deepCopy());
            stack.add(current);
            pointer = 1;
        }
        invalidateFunction.run();
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
        return stack.get(pointer);
    }

    @Override
    public void invalidateView() {
        invalidateFunction.run();
    }
}
