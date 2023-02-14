package com.example.graph_editor;

import android.view.View;

import graph_editor.draw.point_mapping.ScreenSection;

public class ViewWrapper implements ScreenSection {
    private final View view;

    public ViewWrapper(View view) {
        this.view = view;
    }

    @Override
    public int getWidth() {
        return view.getWidth();
    }

    @Override
    public int getHeight() {
        return view.getHeight();
    }
}
