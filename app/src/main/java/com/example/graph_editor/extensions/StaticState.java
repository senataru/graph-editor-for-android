package com.example.graph_editor.extensions;

import android.content.Context;

import com.example.graph_editor.extensions.InstalledExtensionsProvider;
import com.example.graph_editor.extensions.PluginsProxyImpl;
import com.example.graph_editor.fs.FSDirectories;
import com.example.graph_editor.model.GraphType;

import java.io.File;

import graph_editor.extensions.ExtensionsRepository;

//TODO refactor needed, but these are static because of lack of presentation layer since the very beginning of the app
public class StaticState {
    private static ExtensionsRepository repository;
    public static ExtensionsRepository getExtensionsRepositoryInstance(Context context) {
        if (repository == null) {
            repository = InstalledExtensionsProvider.newInstance(
                    new PluginsProxyImpl(
                            GraphType.DIRECTED.getStackCaptureRepository(),
                            GraphType.UNDIRECTED.getStackCaptureRepository(),
                            GraphType.DIRECTED.getPropertyReaderRepository(),
                            GraphType.UNDIRECTED.getPropertyReaderRepository()
                    ),
                    new File(context.getFilesDir(), FSDirectories.pluginsDirectory)
            );
        }
        return repository;
    }
}
