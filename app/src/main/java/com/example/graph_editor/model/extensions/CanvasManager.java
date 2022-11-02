package com.example.graph_editor.model.extensions;

import android.graphics.Canvas;

import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;

import java.util.Collection;
import java.util.Optional;

public interface CanvasManager {
    interface VertexDrawer {
        void drawVertex(int id, Point p, Rectangle rectangle, Canvas canvas);
    }
    interface EdgeDrawer {
        void drawEdge(int id1, int id2, Point p1, Point p2, Rectangle rectangle, Canvas canvas);
    }
    interface ExtendedElementsDrawer {
         void drawElements(Rectangle rectangle, Canvas canvas);
    }
    Optional<VertexDrawer> getVertexDrawer();
    void setVertexDrawer(VertexDrawer drawer);
    Optional<EdgeDrawer> getEdgeDrawer();
    void setEdgeDrawer(EdgeDrawer drawer);
    void addExtendedDrawer(ExtendedElementsDrawer drawer);
    void removeExtendedDrawer(ExtendedElementsDrawer drawer);
    Collection<ExtendedElementsDrawer> getExtendedDrawers();
}
