package com.example.graph_editor.extentions.model;

public class Extension {
    private final String name;
    private final JSInvoker invoker;
    private boolean enabled = false;

    public Extension(String name, JSInvoker invoker) {
        this.name = name;
        this.invoker = invoker;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        invoker.callJSFunction(enabled ? "activate" : "deactivate");
    }
    public String getName() { return name; }
}
