package com.example.graph_editor.extensions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class CanvasManagerImpl implements CanvasManager {
    private static VertexDrawer vertexDrawer;
    private static EdgeDrawer edgeDrawer;
    private final Collection<ExtendedElementsDrawer> drawers = new HashSet<>();
    @Override
    public Optional<VertexDrawer> getVertexDrawer() {
        return Optional.ofNullable(vertexDrawer);
    }

    @Override
    public Optional<EdgeDrawer> getEdgeDrawer() {
        return Optional.ofNullable(edgeDrawer);
    }

    @Override
    public void setVertexDrawer(VertexDrawer drawer) {
        vertexDrawer = drawer;
    }

    @Override
    public void setEdgeDrawer(EdgeDrawer drawer) {
        edgeDrawer = drawer;
    }
    @Override
    public void addExtendedDrawer(ExtendedElementsDrawer drawer) {
        drawers.add(drawer);
    }
    @Override
    public void removeExtendedDrawer(ExtendedElementsDrawer drawer) {
        drawers.remove(drawer);
    }

    @Override
    public Collection<ExtendedElementsDrawer> getExtendedDrawers() {
        return drawers;
    }
}
