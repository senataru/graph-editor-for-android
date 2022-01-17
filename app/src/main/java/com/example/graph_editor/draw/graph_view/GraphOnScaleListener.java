package com.example.graph_editor.draw.graph_view;

import android.view.ScaleGestureDetector;

import com.example.graph_editor.draw.action_mode_type.ActionModeType;
import com.example.graph_editor.model.state.StateStack;

public class GraphOnScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
    private final StateStack stateStack;

    GraphOnScaleListener(StateStack stack) {
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
        if (stateStack.getCurrentState().getActionModeType() == ActionModeType.MOVE_CANVAS) {
            stateStack.getCurrentState().setCurrentModeType(ActionModeType.ZOOM_CANVAS);
//            stateStack.backup();     // unnecessary - every scale begins with move, and move is already backed up
            return true;
        }
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        stateStack.getCurrentState().setCurrentModeType(ActionModeType.MOVE_CANVAS);
    }
}
