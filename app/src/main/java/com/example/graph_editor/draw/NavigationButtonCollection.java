package com.example.graph_editor.draw;

import android.widget.ImageButton;
import androidx.core.content.ContextCompat;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_action.GraphAction;

import java.util.HashMap;
import java.util.Map;

public class NavigationButtonCollection {
    private GraphAction graphAction;
    private final Map<GraphAction, ImageButton> buttons;
    private ImageButton current = null;
    private final int ACTIVE;
    private final int NOT_ACTIVE;
    private final DrawActivity activity;

    public NavigationButtonCollection(DrawActivity activity) {
        this.activity = activity;
        this.buttons = new HashMap<>();
        this.graphAction = (mapper, event, stack) -> true;
        ACTIVE = ContextCompat.getColor(activity, R.color.grey);
        NOT_ACTIVE = ContextCompat.getColor(activity, R.color.lt_grey);
    }
    public void add(ImageButton button, GraphAction modeType) {
        button.setOnClickListener(v -> {
            if (activity.isLocked()) return;
            setGraphAction(modeType);
            setCurrent(button);
        });
        button.setBackgroundColor(NOT_ACTIVE);
        buttons.put(modeType, button);
    }
    public GraphAction getGraphAction() { return graphAction; }
    private void setGraphAction(GraphAction graphAction) {
        this.graphAction = graphAction;
    }
    private void setCurrent(ImageButton button) {
        if (current != null) {
            current.setClickable(true);
            current.setBackgroundColor(NOT_ACTIVE);
        }
        current = button;
        current.setClickable(false);
        current.setBackgroundColor(ACTIVE);
    }
}
