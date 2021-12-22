package com.example.graph_editor.model;

import java.util.ArrayList;


//pointer always points towards what should be drawn right now
public class UndoRedoStackImpl implements UndoRedoStack {
    private final ArrayList<String> stack;
    private int pointer;

    public UndoRedoStackImpl(String initialGraph) {
        stack = new ArrayList<>();
        stack.add(initialGraph);
        pointer = 0;
    }

    @Override
    public String undo() {
        pointer = Math.max(pointer-1, 0);
        return stack.get(pointer);
    }

    @Override
    public String redo() {
        pointer = Math.min(pointer+1, stack.size()-1);
        return stack.get(pointer);
    }

    @Override
    public void put(String graph) {
        stack.subList(pointer + 1, stack.size()).clear();
        stack.add(graph);
        pointer += 1;
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
    public String get() {
        return stack.get(pointer);
    }
}
