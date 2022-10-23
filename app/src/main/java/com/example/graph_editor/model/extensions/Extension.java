package com.example.graph_editor.model.extensions;

public class Extension {
    private final String name;
    private final ExtensionInvoker invoker;
    private final ExtensionInvoker.ExtensionProxy extensionProxy;
    private boolean enabled = false;

    public Extension(String name, ExtensionInvoker invoker, ExtensionInvoker.ExtensionProxy extensionProxy) {
        this.name = name;
        this.invoker = invoker;
        this.extensionProxy = extensionProxy;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        invoker.callFunction(enabled ? "activate" : "deactivate", extensionProxy);
    }
    public String getName() { return name; }
}
