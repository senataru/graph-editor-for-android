package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.util.Log;
import android.view.ScaleGestureDetector;

import com.example.graph_editor.draw.Frame;

public class GraphOnScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
    private final Frame frame;

    GraphOnScaleListener(Frame frame) {
        this.frame = frame;
    }

    // no need to remember modes
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
//        System.out.println("Budzi sie detektor");
        float scaleFactor = detector.getScaleFactor();
        frame.rescale(1/scaleFactor);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
}
