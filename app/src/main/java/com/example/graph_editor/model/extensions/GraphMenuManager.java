package com.example.graph_editor.model.extensions;

import android.content.Context;

import java.util.function.Consumer;

public interface GraphMenuManager {
    interface OnSelection extends Consumer<Context> { }
    int registerOption(String name, OnSelection onOptionSelection);
    void deregisterOption(int id);
}
