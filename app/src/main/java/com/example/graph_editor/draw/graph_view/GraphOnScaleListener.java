package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.util.Log;
import android.view.ScaleGestureDetector;

import com.example.graph_editor.draw.ActionModeType;
import com.example.graph_editor.draw.Frame;

public class GraphOnScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
    private final Frame frame;

    GraphOnScaleListener(Frame frame) {
        this.frame = frame;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        frame.rescale(1/scaleFactor);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (ActionModeType.getCurrentModeType() == ActionModeType.MOVE_CANVAS) {
            ActionModeType.setCurrentModeType(ActionModeType.ZOOM_CANVAS);
            return true;
        }
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        ActionModeType.setCurrentModeType(ActionModeType.MOVE_CANVAS);
    }
}
