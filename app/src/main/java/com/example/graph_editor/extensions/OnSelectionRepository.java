package com.example.graph_editor.extensions;

import android.util.Pair;

import java.util.Collection;

public interface OnSelectionRepository<T> {
    int registerOption(String name, T onSelection);
    void deregisterOption(int id);
    Collection<Pair<String, T>> getRegisteredOptions();
}
