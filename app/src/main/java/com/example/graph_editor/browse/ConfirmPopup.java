package com.example.graph_editor.browse;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.graph_editor.R;
import com.example.graph_editor.ViewWrapper;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.extensions.CanvasManagerImpl;

import graph_editor.draw.point_mapping.PointMapperImpl;
import graph_editor.geometry.Point;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class ConfirmPopup {
    Context context;
    private final GraphVisualization<PropertySupportingGraph> visualization;
    Runnable deleteFunction;

    AlertDialog dialog;

    ConfirmPopup(Context context, GraphVisualization<PropertySupportingGraph> visualization, Runnable deleteFunction) {
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

        graphView.initialize(new CanvasManagerImpl(), new PointMapperImpl(new ViewWrapper(graphView), new Point(0,0)), visualization);

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
