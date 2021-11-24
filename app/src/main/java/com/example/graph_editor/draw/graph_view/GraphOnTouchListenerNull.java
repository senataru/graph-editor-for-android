package com.example.graph_editor.draw.graph_view;

import android.view.MotionEvent;
import android.view.View;

public class GraphOnTouchListenerNull implements GraphOnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return false;
    }
}
