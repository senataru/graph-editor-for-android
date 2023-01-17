package com.example.graph_editor.extensions;

import android.util.Pair;

import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.extensions.GraphActionManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GraphActionManagerImpl implements GraphActionManager {
    //TODO change to nonstatic
    private static final Map<Integer, Pair<String, GraphAction>> registeredActions = new HashMap<>();
    private int id;

    public static Collection<Pair<String, GraphAction>> getRegisteredActions() {
        return registeredActions.values();
    }
    @Override
    public int registerAction(String imageButtonPath, GraphAction onActionSelection) {
        registeredActions.put(id, Pair.create(imageButtonPath, onActionSelection));
        return id++;
    }

    @Override
    public void deregisterAction(int id) {
        registeredActions.remove(id);
    }
}
