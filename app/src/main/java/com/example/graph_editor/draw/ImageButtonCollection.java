package com.example.graph_editor.draw;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.example.graph_editor.R;

import java.util.ArrayList;
import java.util.List;

public class ImageButtonCollection {
    ImageButton current = null;
    void add(ImageButton button, Runnable function) {
        button.setOnClickListener(v -> {
            function.run();
            setCurrent(button);
        });
    }
    public void setCurrent(ImageButton button) {
        if(current != null) {
            current.setClickable(true);
            current.setBackgroundColor(Color.LTGRAY);
        }
        current = button;
        button.setClickable(false);
        current.setBackgroundColor(Color.GRAY);
    }
}
