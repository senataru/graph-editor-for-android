package com.example.graph_editor.extensions;

import android.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OnSelectionRepositoryImpl<T> implements OnSelectionRepository<T> {
    private int id = 0;
    private final Map<Integer, Pair<String, T>> registeredOptions = new HashMap<>();
    @Override
    public int registerOption(String name, T handler) {
        registeredOptions.put(id, Pair.create(name, handler));
        return id++;
    }

    @Override
    public void deregisterOption(int id) {
        registeredOptions.remove(id);
    }
    @Override
    public Collection<Pair<String, T>> getRegisteredOptions() {
        return registeredOptions.values();
    }
}
