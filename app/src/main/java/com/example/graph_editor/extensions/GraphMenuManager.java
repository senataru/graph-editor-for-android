package com.example.graph_editor.extensions;

import android.util.Pair;
import android.view.View;

import java.util.Collection;

import graph_editor.extensions.OnOptionSelection;
import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public interface GraphMenuManager {
    int registerOption(String name, OnOptionSelection onOptionSelection);
    void deregisterOption(int id);
    Collection<Pair<String, OnOptionSelection>> getRegisteredOptions();
}
