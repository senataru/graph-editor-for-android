package com.example.graph_editor.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

import graph_editor.extensions.Extension;
import graph_editor.extensions.ExtensionsRepository;
import graph_editor.extensions.Plugin;
import graph_editor.extensions.PluginConfigDto;

public class InstalledExtensionsProvider implements ExtensionsRepository {
    private final Plugin.Proxy proxy;
    private final File pluginsDirectory;
    private final List<Extension> extensions = new ArrayList<>();
    private final Map<String, Extension> extensionsMap = new HashMap<>();
    private InstalledExtensionsProvider(
            Plugin.Proxy proxy,
            File pluginsDirectory
    ) {
        this.proxy = proxy;
        this.pluginsDirectory = pluginsDirectory;
    }

    public static InstalledExtensionsProvider newInstance(
            Plugin.Proxy proxy,
            File pluginsDirectory
    ) {
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
        ObjectMapper mapper = new ObjectMapper();
        Extension ext;
        try {
            PluginConfigDto dto = mapper.readValue(new File(extensionDirectory, "config.json"), PluginConfigDto.class);
            Plugin plugin = PluginLoader.loadPlugin(extensionDirectory, dto);
            ext = new Extension(
                    extensionDirectory.getName(),
                    plugin,
                    proxy
            );
            extensions.add(ext);
            extensionsMap.put(extensionDirectory.getName(), ext);
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
