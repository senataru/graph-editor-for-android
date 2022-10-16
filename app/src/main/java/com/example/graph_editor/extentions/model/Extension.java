package com.example.graph_editor.extentions.model;

public class Extension {
    private final String name;
    private final String scriptFileName;
    private boolean enabled;

    public Extension(String name, String scriptFileName, boolean enabled) {
        this.name = name;
        this.scriptFileName = scriptFileName;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean value) {
        enabled = value;
    }
    public String getName() { return name; }
}
