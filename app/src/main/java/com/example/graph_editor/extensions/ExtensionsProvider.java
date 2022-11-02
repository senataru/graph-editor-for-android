package com.example.graph_editor.extensions;

import com.example.graph_editor.model.extensions.Extension;
import com.example.graph_editor.model.extensions.ExtensionInvoker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ExtensionsProvider {
    //TODO change to nonstatic(?), as well as implementation !
    public static List<Extension> provideExtensions() {
        return extensions;
    }


    private static final String file = "res/raw/matching.js";
    private static final InputStream in = ExtensionsProvider.class.getClassLoader().getResourceAsStream(file);
    private static final ExtensionInvoker invoker = RhinoJSInvoker.createInstance(
            //TODO change
//                            "file:///android_asset/js/example.ts",
//                            "src\\main\\assets\\js\\example.ts",
//                            "src\\main\\js\\example.ts",
            new InputStreamReader(in)
    );

    private static List<Extension> extensions =  List.of(
            new Extension(
                    "Enable Me",
                    invoker,
                    new ScriptProxy(
                            invoker,
                            new GraphMenuManagerImpl(),
                            new CanvasManagerImpl(),
                            new GraphActionManagerImpl()
                    )
            )
    );
}
