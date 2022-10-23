package com.example.graph_editor.model.extensions;

public interface ExtensionInvoker {
    interface ExtensionProxy {
        int registerGraphMenuOption(String optionName, String functionCalledOnOptionSelected);
        void deregisterGraphMenuOption(int id);
        void customizeVertexDrawingBehaviour(String vertexDrawer);
        void restoreDefaultVertexDrawingBehaviour();
        void customizeEdgeDrawingBehaviour(String edgeDrawer);
        void restoreDefaultEdgeDrawingBehaviour();
        int registerGraphAction(String imageButtonPath, String functionCalled);
        void deregisterGraphAction(int id);
    }
    void callFunction(String functionName, Object... args);
}
