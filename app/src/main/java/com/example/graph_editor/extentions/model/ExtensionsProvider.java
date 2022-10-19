package com.example.graph_editor.extentions.model;

import com.example.graph_editor.extentions.RhinoJSInvoker;

import java.util.List;

public class ExtensionsProvider {
    //TODO change to nonstatic(?), as well as implementation !
    public static List<Extension> provideExtensions() {
        return extensions;
    }

    private static List<Extension> extensions =  List.of(
//                new Extension("UIPlugin", null),
//                new Extension("PushRelabelPlugin", null),
            new Extension(
                        "Enable Me",
                        RhinoJSInvoker.createInstance(
//                                "file:///android_asset/js/example.ts",
//                                "src\\main\\assets\\js\\example.ts",
//                                "src\\main\\js\\example.ts",
                                //TODO change
                                null,
                                new GraphMenuManagerImpl()
                                )
                                        )
                                        );
}
