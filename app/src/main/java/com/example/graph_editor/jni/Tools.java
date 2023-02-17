package com.example.graph_editor.jni;

public class Tools {
    public native double[] arrange(int n, int m, double[] x, double[] y, int[] tab_edge_source, int[] tab_edge_target);
    public native double[] arrangePlanar(int n, int m, double[] x, double[] y, int[] tab_edge_source, int[] tab_edge_target);
    public native double[] makePlanar(int n, int m, double[] x, double[] y, int[] tab_edge_source, int[] tab_edge_target);
}