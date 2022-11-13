package com.example.graph_editor.model.graph_storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.GraphFactory;
import com.example.graph_editor.model.GraphType;
import com.example.graph_editor.model.Vertex;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphScannerTest {
    @Test
    public void testFromExact() throws InvalidGraphStringException {
        Graph graph = GraphScanner.fromExact("UNDIRECTED\n3 2\n0 0.0 0.0\n1 0.0 1.0\n2 1.0 1.0\n0 1\n1 2\n");

        List<Vertex> v = graph.getVertices();
        List<Edge> e = graph.getEdges();

        assertEquals(3, v.size());
        assertEquals(0f, v.get(0).getPoint().getX(), 0.0001);
        assertEquals(0f, v.get(0).getPoint().getY(), 0.0001);
        assertEquals(0f, v.get(1).getPoint().getX(), 0.0001);
        assertEquals(1f, v.get(1).getPoint().getY(), 0.0001);
        assertEquals(1f, v.get(2).getPoint().getX(), 0.0001);
        assertEquals(1f, v.get(2).getPoint().getY(), 0.0001);
        assertEquals(2, e.size());
        assertEquals(0, e.get(0).getSource().getIndex());
        assertEquals(1, e.get(0).getTarget().getIndex());
        assertEquals(1, e.get(1).getSource().getIndex());
        assertEquals(2, e.get(1).getTarget().getIndex());
    }

    @Test
    public void shouldAddVertexProperty() throws InvalidGraphStringException {
        String propertyName = "propertyName";
        String propertyString = propertyName + "\n"
                + "3\n"
                + "2 c\n"
                + "0 a\n"
                + "1 b\n";
        Graph graph = new GraphFactory(GraphType.UNDIRECTED).produce();
        graph.addVertex();
        graph.addVertex();
        graph.addVertex();
        graph.addVertex();

        Graph result = GraphScanner.addVertexProperty(graph, propertyString);

        List<Vertex> propertyVertices = result.getVerticesWithProperty(propertyName);
        List<Vertex> vertices = result.getVertices();
        assertEquals(propertyVertices.size(), 3);
        assertEquals(vertices.get(0).getProperty(propertyName), "a");
        assertEquals(vertices.get(1).getProperty(propertyName), "b");
        assertEquals(vertices.get(2).getProperty(propertyName), "c");
        assertTrue(result.getVertexPropertyNames().contains(propertyName));
        assertEquals(result.getVertexPropertyNames().size(), 1);
    }
}
