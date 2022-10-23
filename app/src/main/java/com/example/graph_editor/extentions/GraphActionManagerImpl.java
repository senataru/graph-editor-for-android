package com.example.graph_editor.extentions;

import android.util.Pair;

import com.example.graph_editor.draw.action_mode_type.GraphAction;
import com.example.graph_editor.model.extensions.GraphActionManager;
import com.example.graph_editor.model.extensions.GraphMenuManager;

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
    public int registerOption(String imageButtonPath, GraphAction onActionSelection) {
        registeredActions.put(id, Pair.create(imageButtonPath, onActionSelection));
        return id++;
    }

    @Override
    public void deregisterOption(int id) {
        registeredActions.remove(id);
    }
}
