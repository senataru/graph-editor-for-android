package com.example.graph_editor.draw.graph_view;

import android.view.ScaleGestureDetector;

import com.example.graph_editor.draw.ActionModeType;
import com.example.graph_editor.model.state.UndoRedoStack;

public class GraphOnScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
    private final UndoRedoStack stateStack;

    GraphOnScaleListener(UndoRedoStack stack) {
        this.stateStack = stack;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();

        stateStack.getCurrentState().getFrame().rescale(1/scaleFactor);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (ActionModeType.getCurrentModeType() == ActionModeType.MOVE_CANVAS) {
            ActionModeType.setCurrentModeType(ActionModeType.ZOOM_CANVAS);
//            stateStack.backup();     // unnecessary - every scale begins with move, and move is already backed up
            return true;
        }
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        ActionModeType.setCurrentModeType(ActionModeType.MOVE_CANVAS);

    }
}
