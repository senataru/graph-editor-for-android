package com.example.graph_editor.model;

public interface UndoRedoStack {
    String undo();
    String redo();
    void put(String graph);
    boolean isUndoPossible();
    boolean isRedoPossible();
    String get();
}
