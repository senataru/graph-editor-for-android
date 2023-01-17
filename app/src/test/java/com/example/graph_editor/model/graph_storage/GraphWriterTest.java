//package com.example.graph_editor.model.graph_storage;
//
//import static org.junit.Assert.assertEquals;
//
//import com.example.graph_editor.model.GraphType;
//
//import org.junit.Test;
//
//import java.util.List;
//
//import graph_editor.graph.Graph;
//
//public class GraphWriterTest {
//    @Test
//    public void testToVEDirected() {
//        Graph graph = new GraphFactory(GraphType.DIRECTED).produce();
//        graph.addVertex();
//        graph.addVertex();
//        graph.addVertex();
//        List<Vertex> l = graph.getVertices();
//        graph.addEdge(l.get(0), l.get(1));
//        graph.addEdge(l.get(2), l.get(1));
//
//        String graphString = GraphWriter.toVE(graph);
//
//        assertEquals("3 2\n0 1\n2 1\n", graphString);
//    }
//
//    @Test
//    public void testToVEUndirected() {
//        Graph graph = new GraphFactory(GraphType.UNDIRECTED).produce();
//        graph.addVertex();
//        graph.addVertex();
//        graph.addVertex();
//        List<Vertex> l = graph.getVertices();
//        graph.addEdge(l.get(0), l.get(1));
//        graph.addEdge(l.get(2), l.get(1));
//
//        String graphString = GraphWriter.toVE(graph);
//
//        assertEquals("3 2\n0 1\n1 2\n", graphString);
//    }
//
//    @Test
//    public void testToExact() {
//        Graph graph = new GraphFactory(GraphType.UNDIRECTED).produce();
//        graph.addVertex();
//        graph.addVertex();
//        graph.addVertex();
//        List<Vertex> l = graph.getVertices();
//        l.get(0).setPoint(new Point(0f, 0f));
//        l.get(1).setPoint(new Point(0f, 1f));
//        l.get(2).setPoint(new Point(1f, 1f));
//        graph.addEdge(l.get(0), l.get(1));
//        graph.addEdge(l.get(2), l.get(1));
//
//        String graphString = GraphWriter.toExact(graph);
//
//        assertEquals("UNDIRECTED\n3 2\n0 0.0 0.0\n1 0.0 1.0\n2 1.0 1.0\n0 1\n1 2\n", graphString);
//    }
//
//    @Test
//    public void shouldGetVertexPropertyString() {
//        String propertyName = "propertyName";
//        Graph graph = new GraphFactory(GraphType.UNDIRECTED).produce();
//        graph.addVertex();
//        graph.addVertex();
//        graph.addVertex();
//        List<Vertex> vertices = graph.getVertices();
//        graph.setVertexProperty(vertices.get(0), propertyName, "a");
//        graph.setVertexProperty(vertices.get(1), propertyName, "b");
//        graph.setVertexProperty(vertices.get(2), propertyName, "c");
//        graph.setVertexProperty(vertices.get(0), "otherProperty", "d");
//        String expectedPropertyString = propertyName + "\n"
//                + "3\n"
//                + "0 a\n"
//                + "1 b\n"
//                + "2 c\n";
//
//        String propertyString = GraphWriter.getVertexPropertyString(propertyName, graph);
//
//        assertEquals(propertyString, expectedPropertyString);
//    }
//
//    @Test
//    public void shouldGetEdgePropertyStringDirected() {
//        String propertyName = "propertyName";
//        Graph graph = new GraphFactory(GraphType.DIRECTED).produce();
//        //assuming indexes are based on order on vertex addition
//        Vertex v1 = graph.addVertex();
//        Vertex v2 = graph.addVertex();
//        Vertex v3 = graph.addVertex();
//        graph.addEdge(v1, v2);
//        graph.addEdge(v2, v3);
//        graph.addEdge(v3, v1);
//        graph.setEdgeProperty(graph.getEdge(v1, v2), propertyName, "a");
//        graph.setEdgeProperty(graph.getEdge(v2, v3), propertyName, "b");
//        graph.setEdgeProperty(graph.getEdge(v3, v1), propertyName, "c");
//        graph.setEdgeProperty(graph.getEdge(v3, v1), "otherProperty", "d");
//        String expectedPropertyString = propertyName + "\n"
//                + "3\n"
//                + "0 1 a\n"
//                + "1 2 b\n"
//                + "2 0 c\n";
//
//        String propertyString = GraphWriter.getEdgePropertyString(propertyName, graph);
//
//        assertEquals(propertyString, expectedPropertyString);
//    }
//
//    @Test
//    public void shouldGetEdgePropertyStringUndirected() {
//        String propertyName = "propertyName";
//        Graph graph = new GraphFactory(GraphType.UNDIRECTED).produce();
//        //assuming indexes are based on order on vertex addition
//        Vertex v1 = graph.addVertex();
//        Vertex v2 = graph.addVertex();
//        Vertex v3 = graph.addVertex();
//        graph.addEdge(v1, v2);
//        graph.addEdge(v2, v3);
//        graph.addEdge(v3, v1);
//        graph.setEdgeProperty(graph.getEdge(v1, v2), propertyName, "a");
//        graph.setEdgeProperty(graph.getEdge(v2, v3), propertyName, "b");
//        graph.setEdgeProperty(graph.getEdge(v3, v1), propertyName, "c");
//        graph.setEdgeProperty(graph.getEdge(v3, v1), "otherProperty", "d");
//        String expectedPropertyString = propertyName + "\n"
//                + "6\n"
//                + "0 1 a\n"
//                + "1 0 a\n"
//                + "1 2 b\n"
//                + "2 1 b\n"
//                + "2 0 c\n"
//                + "0 2 c\n";
//
//        String propertyString = GraphWriter.getEdgePropertyString(propertyName, graph);
//
//        assertEquals(propertyString, expectedPropertyString);
//    }
//}
