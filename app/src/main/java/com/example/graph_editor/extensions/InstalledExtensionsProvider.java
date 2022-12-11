package com.example.graph_editor.extensions;

import com.example.graph_editor.model.extensions.CanvasManager;
import com.example.graph_editor.model.extensions.Extension;
import com.example.graph_editor.model.extensions.ExtensionInvoker;
import com.example.graph_editor.model.extensions.ExtensionsRepository;
import com.example.graph_editor.model.extensions.GraphActionManager;
import com.example.graph_editor.model.extensions.GraphMenuManager;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstalledExtensionsProvider implements ExtensionsRepository {
    private static final GraphMenuManager graphMenuManager = new GraphMenuManagerImpl();
    private static final CanvasManager canvasManager = new CanvasManagerImpl();
    private static final GraphActionManager graphActionManager = new GraphActionManagerImpl();

    private final File root;
    private final List<Extension> extensions = new ArrayList<>();
    private final Map<String, Extension> extensionsMap = new HashMap<>();

    private InstalledExtensionsProvider(File root) {
        this.root = root;
    }

    public static InstalledExtensionsProvider getInstance(File root) {
        InstalledExtensionsProvider instance = new InstalledExtensionsProvider(root);
        instance.loadRepository();
        return instance;
    }

    @Override
    public List<Extension> getExtensions() {
        return extensions;
    }

    @Override
    public boolean isPresent(String extensionName) {
        return extensionsMap.containsKey(extensionName);
    }

    @Override
    public boolean add(String extensionsName) {
        return loadExtension(new File(root, extensionsName));
    }


    @Override
    public boolean remove(String extensionsName) {
        if(isPresent(extensionsName)) {
            extensions.removeIf(e -> e.getName().equals(extensionsName));
            extensionsMap.remove(extensionsName);
            return true;
        }
        return false;
    }

    private boolean loadExtension(File extensionsDirectory) {
        try {
            File script = new File(extensionsDirectory, "main_script.js");
            ExtensionInvoker invoker = RhinoJSInvoker.createInstance(new FileReader(script));
            Extension e = new Extension(
                    extensionsDirectory.getName(),
                    invoker,
                    new ScriptProxy(
                            invoker,
                            graphMenuManager,
                            canvasManager,
                            graphActionManager
                    ));
            extensions.add(e);
            extensionsMap.put(extensionsDirectory.getName(), e);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadRepository() {
        File[] pluginsDirectories = root.listFiles();
        if (pluginsDirectories == null) {
            throw new RuntimeException("root directory does not exist");
        }
        for (File pluginDirectory : pluginsDirectories) {
            loadExtension(pluginDirectory);
        }
    }
}
