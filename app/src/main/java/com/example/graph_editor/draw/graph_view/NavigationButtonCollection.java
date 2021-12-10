package com.example.graph_editor.draw.graph_view;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.example.graph_editor.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationButtonCollection {
    ImageButton current = null;
    Context context;
    int ACTIVE, NOT_ACTIVE;

    public NavigationButtonCollection(Context context) {
        this.context = context;
        ACTIVE = ContextCompat.getColor(context, R.color.grey);
        NOT_ACTIVE = ContextCompat.getColor(context, R.color.lt_grey);
    }
    public void add(ImageButton button, Runnable function) {
        button.setOnClickListener(v -> {
            function.run();
            setCurrent(button);
        });
        button.setBackgroundColor(NOT_ACTIVE);
    }
    public void setCurrent(ImageButton button) {
        if(current != null) {
            current.setClickable(true);
            current.setBackgroundColor(NOT_ACTIVE);
        }
        current = button;
        button.setClickable(false);
        current.setBackgroundColor(ACTIVE);
    }
}
