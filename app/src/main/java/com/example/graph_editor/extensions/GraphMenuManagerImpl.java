package com.example.graph_editor.extensions;

import android.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import graph_editor.extensions.OnOptionSelection;

public class GraphMenuManagerImpl implements GraphMenuManager {
    //TODO change
    private static final GraphMenuManagerImpl instance = new GraphMenuManagerImpl();
    //TODO change
    public static GraphMenuManager getInstance() {
        return instance;
    }

    private int id = 0;
    private final Map<Integer, Pair<String, OnOptionSelection>> registeredOptions = new HashMap<>();
    @Override
    public int registerOption(String name, OnOptionSelection handler) {
        registeredOptions.put(id, Pair.create(name, handler));
        return id++;
    }

    @Override
    public void deregisterOption(int id) {
        registeredOptions.remove(id);
    }
    @Override
    public Collection<Pair<String, OnOptionSelection>> getRegisteredOptions() {
        return registeredOptions.values();
    }
}
