package com.example.graph_editor.extensions;

import com.example.graph_editor.draw.graph_action.GraphActionManager;
import java.util.HashMap;
import java.util.Map;

import graph_editor.draw.GraphDrawer;
import graph_editor.extensions.OnOptionSelection;
import graph_editor.extensions.Plugin;

public class PluginsProxyImpl implements Plugin.Proxy {
    private final GraphMenuManager graphMenuManager;
    private final CanvasManager canvasManager;
    private final GraphActionManager graphActionManager;

    public PluginsProxyImpl(
            GraphMenuManager graphMenuManager,
            CanvasManager canvasManager,
            GraphActionManager graphActionManager) {
        this.graphMenuManager = graphMenuManager;
        this.canvasManager = canvasManager;
        this.graphActionManager = graphActionManager;
    }

    private final Map<Plugin, Map<String, Integer>> obtainedOptions = new HashMap<>();

    @Override
    public boolean registerOption(Plugin plugin, String name, OnOptionSelection onOptionSelection) {
        Map<String, Integer> options = obtainedOptions.get(plugin);
        if (options != null && options.get(name) != null) {
            return false;
        }
        if (options == null) {
            options = new HashMap<>();
            obtainedOptions.put(plugin, options);
        }
        int id = graphMenuManager.registerOption(name, onOptionSelection);
        options.put(name, id);
        return true;
    }

    @Override
    public boolean deregisterOption(Plugin plugin, String name) {
        Map<String, Integer> options = obtainedOptions.get(plugin);
        if (options != null) {
            Integer id = options.remove(name);
            if (id != null) {
                graphMenuManager.deregisterOption(id);
                return true;
            }
        }
        return false;
    }

    public void releasePluginResources(Plugin plugin) {
        Map<String, Integer> options = obtainedOptions.remove(plugin);
        if (options != null) {
            options.forEach((name, id) -> graphMenuManager.deregisterOption(id));
        }
    }
}
