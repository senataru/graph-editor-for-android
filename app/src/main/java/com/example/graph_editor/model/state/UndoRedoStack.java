package com.example.graph_editor.model.state;

public interface UndoRedoStack {
    State undo();
    State redo();
    void put(State elem);
    void backup(); // backup puts a copy of current state BEHIND it
    boolean isUndoPossible();
    boolean isRedoPossible();
    State getCurrentState();
    void invalidateView();
}
