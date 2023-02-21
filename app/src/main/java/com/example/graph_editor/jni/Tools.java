package com.example.graph_editor.jni;

@SuppressWarnings("unussed")
public class Tools {
    @SuppressWarnings("unused")
    public native double[] arrange(int n, int m, double[] x, double[] y, int[] tab_edge_source, int[] tab_edge_target);

    @SuppressWarnings("unused")
    public native double[] arrangePlanar(int n, int m, double[] x, double[] y, int[] tab_edge_source, int[] tab_edge_target);

    @SuppressWarnings("unused")
    public native double[] makePlanar(int n, int m, double[] x, double[] y, int[] tab_edge_source, int[] tab_edge_target);
}
// This is the clone of Core's Tools class. It is needed to create the similar library as in core, but for Android