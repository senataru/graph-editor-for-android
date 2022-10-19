package com.example.graph_editor.extentions.model;

import android.util.Pair;

import java.util.List;

public interface GraphMenuManager {
    int registerOption(String name, Runnable onOptionSelection);
    void deregisterOption(int id);
}
