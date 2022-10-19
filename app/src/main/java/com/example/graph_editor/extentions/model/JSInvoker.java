package com.example.graph_editor.extentions.model;

public interface JSInvoker {
    interface CoreApplicationContext {
        int registerGraphMenuOption(String optionName, String functionCalledOnOptionSelected);
        void deregisterGraphMenuOption(int id);
        void print(String message);
    }
    void callJSFunction(String functionName);
}
