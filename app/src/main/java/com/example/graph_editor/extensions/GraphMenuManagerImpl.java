package com.example.graph_editor.extensions;

import android.util.Pair;

import com.example.graph_editor.model.extensions.GraphMenuManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GraphMenuManagerImpl implements GraphMenuManager {
    //TODO change to nonstatic
    private static final Map<Integer, Pair<String, MenuOptionHandler>> registeredOptions = new HashMap<>();
    public static Collection<Pair<String, MenuOptionHandler>> getRegisteredOptions() {
        return registeredOptions.values();
    }

    private static int id = 0;

    @Override
    public int registerOption(String name, MenuOptionHandler handler) {
        registeredOptions.put(id, Pair.create(name, handler));
        return id++;
    }

    //TODO allow only access to ids extension registered previously?
    @Override
    public void deregisterOption(int id) {
        registeredOptions.remove(id);
    }
}
