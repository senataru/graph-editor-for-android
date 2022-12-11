package com.example.graph_editor.extensions;

import android.content.Context;

import com.example.graph_editor.client.Client;
import com.example.graph_editor.model.extensions.CanvasManager;
import com.example.graph_editor.model.extensions.Extension;
import com.example.graph_editor.model.extensions.ExtensionInvoker;
import com.example.graph_editor.model.extensions.GraphActionManager;
import com.example.graph_editor.model.extensions.GraphMenuManager;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExtensionsProvider {
    private static final GraphMenuManager graphMenuManager = new GraphMenuManagerImpl();
    private static final CanvasManager canvasManager = new CanvasManagerImpl();
    private static final GraphActionManager graphActionManager = new GraphActionManagerImpl();

    public static List<Extension> provideExtensions(Context context) {
        List<Extension> extensions = new ArrayList<>();
        Thread t = new Thread(() -> downloadAllExtensions(context));
        t.start();
        try {
            t.join();
            File[] pluginsDirectories = context.getFilesDir().listFiles();
            for (File f : pluginsDirectories) {
                File script = f.listFiles()[0]; //TODO change!!
                ExtensionInvoker invoker = RhinoJSInvoker.createInstance(new FileReader(script));
                extensions.add(
                        new Extension(
                                f.getName(),
                                invoker,
                                new ScriptProxy(
                                        invoker,
                                        graphMenuManager,
                                        canvasManager,
                                        graphActionManager
                                )
                        )
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        extensions.forEach(e -> System.out.println(e.getName()));
        return extensions;
    }
    private static void downloadAllExtensions(Context context) {
        try {
            Client client = new Client("192.168.1.38");
            for (String pluginName : client.getList()) {
                client.getPlugin(context.getFilesDir(), pluginName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
