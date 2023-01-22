package com.example.graph_editor.draw;

import android.content.Context;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.model.state.State;

import java.util.HashMap;
import java.util.Map;

public class NavigationButtonCollection {
    private final State state;

    private final Map<GraphAction, ImageButton> buttons;
    private ImageButton current = null;
    private final int ACTIVE;
    private final int NOT_ACTIVE;

    public NavigationButtonCollection(Context context, State state) {
        this.state = state;
        this.buttons = new HashMap<>();
        ACTIVE = ContextCompat.getColor(context, R.color.grey);
        NOT_ACTIVE = ContextCompat.getColor(context, R.color.lt_grey);
    }
    public void add(ImageButton button, GraphAction modeType) {
        button.setOnClickListener(v -> {
            if (state.isCurrentlyModified()) return;

            state.setGraphAction(modeType);
            setCurrent(button);
        });
        button.setBackgroundColor(NOT_ACTIVE);
        buttons.put(modeType, button);
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

    public void setCurrent(GraphAction type) {
        ImageButton button = buttons.get(type);
        if (button != null)
            setCurrent(button);
    }
}
