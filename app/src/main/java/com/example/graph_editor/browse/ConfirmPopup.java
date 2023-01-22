package com.example.graph_editor.browse;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.extensions.CanvasManagerImpl;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.ObservableStackImpl;
import graph_editor.graph.VersionStack.ObservableStack;
import graph_editor.graph.VersionStackImpl;
import graph_editor.visual.GraphVisualization;

public class ConfirmPopup {
    Context context;
    private final GraphVisualization visualization;
    Runnable deleteFunction;

    AlertDialog dialog;

    ConfirmPopup(Context context, GraphVisualization visualization, Runnable deleteFunction) {
        this.context = context;
        this.visualization = visualization;
        this.deleteFunction = deleteFunction;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.confirm_popup, null);

        GraphView graphView = popupView.findViewById(R.id.graphView);
        Button btnYes = popupView.findViewById(R.id.btn_yes);
        Button btnNo = popupView.findViewById(R.id.btn_no);

        ObservableStack<GraphVisualization> stack = new ObservableStackImpl<>(new VersionStackImpl<>(visualization));
        State state = new State(
                new Rectangle(new Point(0, 0), new Point(1, 1)),
                new GraphAction.MoveCanvas()
        );
        graphView.initialize(new CanvasManagerImpl(), stack, state,false);

        btnYes.setOnClickListener(v -> {
            deleteFunction.run();
            dialog.dismiss();
        });
        btnNo.setOnClickListener(v -> dialog.dismiss());

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
