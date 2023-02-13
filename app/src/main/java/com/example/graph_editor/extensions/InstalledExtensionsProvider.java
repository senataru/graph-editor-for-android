package com.example.graph_editor.extensions;

import java.io.File;
import java.util.*;

import graph_editor.extensions.Extension;
import graph_editor.extensions.ExtensionsRepository;
import graph_editor.extensions.Plugin;

public class InstalledExtensionsProvider implements ExtensionsRepository {
    private final Plugin.Proxy proxy;
    private final File pluginsDirectory;
    private final List<Extension> extensions = new ArrayList<>();
    private final Map<String, Extension> extensionsMap = new HashMap<>();

    private InstalledExtensionsProvider(Plugin.Proxy proxy, File pluginsDirectory) {
        this.proxy = proxy;
        this.pluginsDirectory = pluginsDirectory;
    }

    public static InstalledExtensionsProvider getInstance(Plugin.Proxy proxy, File pluginsDirectory) {
        pluginsDirectory.mkdirs();
        InstalledExtensionsProvider instance = new InstalledExtensionsProvider(proxy, pluginsDirectory);
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
        return loadExtension(new File(pluginsDirectory, extensionsName));
    }


    // I don't know if it is used somewhere else, but I use this changed function in remove extension call
    @Override
    public boolean remove(String extensionsName) {
        if(isPresent(extensionsName)) {
            extensions.removeIf(e -> e.getName().equals(extensionsName));
            extensionsMap.remove(extensionsName);
            File[] pluginsDirectories = pluginsDirectory.listFiles();
            if(pluginsDirectories!=null) {
                for (File plugin: pluginsDirectories) {
                    if(plugin.getName().equals(extensionsName)){
                        deleteDirectory(plugin);
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void deleteDirectory(File file){
        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                deleteDirectory(temp);
            }
        }

        file.delete();
    }

    private boolean loadExtension(File extensionDirectory) {
        try {
            Extension e = new Extension(
                    extensionDirectory.getName(),
                    PluginLoader.loadPlugin(extensionDirectory),
                    proxy
            );
            extensions.add(e);
            extensionsMap.put(extensionDirectory.getName(), e);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadRepository() {
        File[] pluginsDirectories = pluginsDirectory.listFiles();
        if (pluginsDirectories == null) {
            throw new RuntimeException("directory does not exist");
        }
        for (File pluginDirectory : pluginsDirectories) {
            loadExtension(pluginDirectory);
        }
    }
}
