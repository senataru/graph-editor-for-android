package com.example.graph_editor.extentions;

import android.util.Pair;

import com.example.graph_editor.model.extensions.GraphMenuManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GraphMenuManagerImpl implements GraphMenuManager {
    //TODO change to nonstatic
    private static final Map<Integer, Pair<String, OnSelection>> registeredOptions = new HashMap<>();
    public static Collection<Pair<String, OnSelection>> getRegisteredOptions() {
        return registeredOptions.values();
    }
    //TODO change?
    private static int id = 0;

    @Override
    public int registerOption(String name, OnSelection onOptionSelection) {
        registeredOptions.put(id, Pair.create(name, onOptionSelection));
        return id++;
    }

    //TODO allow only access to ids extension registered previously?
    @Override
    public void deregisterOption(int id) {
        registeredOptions.remove(id);
    }
}
