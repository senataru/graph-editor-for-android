package com.example.graph_editor.draw.graph_view;

import android.view.MotionEvent;
import android.view.View;

public interface GraphOnTouchListener extends View.OnTouchListener {
    @Override
    boolean onTouch(View v, MotionEvent event);
}
