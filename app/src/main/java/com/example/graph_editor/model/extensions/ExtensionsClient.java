package com.example.graph_editor.model.extensions;

import java.io.File;
import java.util.List;

public interface ExtensionsClient {
    List<String> getExtensionsList() throws Exception;
    void downloadExtension(File root, String name) throws Exception;
    void connect() throws Exception;
    void disconnect() throws Exception;
}
