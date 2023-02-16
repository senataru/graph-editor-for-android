package com.example.graph_editor.extensions;

import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

import dalvik.system.DexClassLoader;
import graph_editor.extensions.Plugin;
import graph_editor.extensions.PluginConfigDto;

public class PluginLoader {
    public static Plugin loadPlugin(File pluginDirectory, PluginConfigDto dto)  {
        try {
            System.out.println(new File(pluginDirectory, dto.getJarName()).getAbsolutePath());
            ClassLoader cl = new DexClassLoader(new File(pluginDirectory, dto.getJarName()).getAbsolutePath(), null, null, PluginLoader.class.getClassLoader());
            Class<?> pluginClass = Class.forName(dto.getPluginClassName(), true, cl);
            return (Plugin) pluginClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}