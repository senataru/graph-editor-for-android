package com.example.graph_editor.extensions;

import android.graphics.Canvas;

import com.example.graph_editor.point_mapping.PointMapper;

import java.util.Collection;
import java.util.Optional;

import graph_editor.graph.Edge;
import graph_editor.graph.Vertex;

public interface CanvasManager {
    interface VertexDrawer {
        void drawVertex(Vertex vertex, PointMapper mapper, Canvas canvas);
    }
    interface EdgeDrawer {
        void drawEdge(Edge edge, PointMapper mapper, Canvas canvas);
    }
    interface ExtendedElementsDrawer {
         void drawElements(PointMapper mapper, Canvas canvas);
    }
    Optional<VertexDrawer> getVertexDrawer();
    void setVertexDrawer(VertexDrawer drawer);
    Optional<EdgeDrawer> getEdgeDrawer();
    void setEdgeDrawer(EdgeDrawer drawer);
    void addExtendedDrawer(ExtendedElementsDrawer drawer);
    void removeExtendedDrawer(ExtendedElementsDrawer drawer);
    Collection<ExtendedElementsDrawer> getExtendedDrawers();
}
