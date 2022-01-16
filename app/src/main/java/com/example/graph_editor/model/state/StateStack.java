package com.example.graph_editor.model.state;

public interface StateStack {
    State undo();
    State redo();
    void backup(); // backup puts a copy of current state BEHIND it
    boolean isUndoPossible();
    boolean isRedoPossible();
    State getCurrentState();    // returns a modifiable copy
    void invalidateView();
}
