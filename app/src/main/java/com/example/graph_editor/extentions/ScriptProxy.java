package com.example.graph_editor.extentions;

import com.example.graph_editor.model.extensions.CanvasManager;
import com.example.graph_editor.model.extensions.GraphMenuManager;
import com.example.graph_editor.model.extensions.ExtensionInvoker;

public class ScriptProxy implements ExtensionInvoker.ExtensionProxy {
    private final ExtensionInvoker invoker;
    private final GraphMenuManager graphMenuManager;
    private final CanvasManager canvasManager;
    public ScriptProxy(
            ExtensionInvoker invoker,
            GraphMenuManager graphMenuManager, CanvasManager canvasManager) {
        this.invoker = invoker;
        this.graphMenuManager = graphMenuManager;
        this.canvasManager = canvasManager;
    }

    @Override
    public int registerGraphMenuOption(String optionName, String functionCalledOnOptionSelected) {
        return graphMenuManager.registerOption(
                optionName,
                arg -> invoker.callFunction(functionCalledOnOptionSelected, arg)
        );
    }

    @Override
    public void deregisterGraphMenuOption(int id) {
        graphMenuManager.deregisterOption(id);
    }

    @Override
    public void customizeVertexDrawingBehaviour(String vertexDrawer) {
        canvasManager.setVertexDrawer((point, rectangle, canvas) -> {
            invoker.callFunction(vertexDrawer, point, rectangle, canvas);
        });
    }

    @Override
    public void restoreDefaultVertexDrawingBehaviour() {
        canvasManager.setVertexDrawer(null);
    }

    @Override
    public void customizeEdgeDrawingBehaviour(String edgeDrawer) {
        canvasManager.setEdgeDrawer((p1, p2, rectangle, canvas) -> {
            invoker.callFunction(edgeDrawer, p1, p2, rectangle, canvas);
        });
    }

    @Override
    public void restoreDefaultEdgeDrawingBehaviour() {
        canvasManager.setEdgeDrawer(null);
    }
}
