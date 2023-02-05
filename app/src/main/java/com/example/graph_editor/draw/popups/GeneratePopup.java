package com.example.graph_editor.draw.popups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graph_editor.R;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.InputSanitizer;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack;
import graph_editor.graph.Vertex;
import graph_editor.graph_generators.GraphGenerator;
import graph_editor.graph_generators.Parameter;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class GeneratePopup {
    private final Context context;
    private final VersionStack<GraphVisualization<PropertySupportingGraph>> stack;
    private final GraphGenerator generator;
    private final State state;

    private AlertDialog dialog;

    public GeneratePopup(Context context, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, State state, GraphGenerator generator) {
        this.context = context;
        this.stack = stack;
        this.state = state;
        this.generator = generator;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.generate_popup, null);

        LinearLayout parametersLayout = popupView.findViewById(R.id.generate_parameters);
        List<EditText> parameterInputs = new ArrayList<>();
        List<Parameter> parameters = generator.getParameters();

        for (Parameter s : parameters) {
            TextView name = new TextView(context);
            name.setText(s.getName());
            name.setTextSize(24);
            name.setPadding(0, 0, 8, 0);

            EditText value = new EditText(context);
            value.setInputType(InputType.TYPE_CLASS_NUMBER);
            value.setTextSize(24);

            LinearLayout entry = new LinearLayout(context);
            entry.addView(name);
            entry.addView(value);
            entry.setGravity(Gravity.CENTER);

            parametersLayout.addView(entry);
            parameterInputs.add(value);
        }

        popupView.findViewById(R.id.generate_popup_confirm).setOnClickListener(v -> {
            List<String> parameterStrings = new ArrayList<>();
            for (EditText e : parameterInputs) {
                parameterStrings.add(e.getText().toString());
            }

            assert parameterStrings.size() == parameters.size();
            List<Integer> parametersInteger = new ArrayList<>();
            for (int i = 0; i< parameters.size(); i++) {
                Parameter param = parameters.get(i);
                String str = parameterStrings.get(i);

                if (!InputSanitizer.isInteger(str, param.getMinVal(), param.getMaxVal())) {
                    @SuppressLint("DefaultLocale")
                    String toastString = String.format("Parameter %s should be integer between %d and %d", param.getName().toLowerCase(Locale.ROOT), param.getMinVal(), param.getMaxVal());
                    Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
                    return;
                }
                parametersInteger.add(Integer.parseInt(str));
            }

            GraphVisualization<Graph> rawVisualization = generator.generate(parametersInteger);
            BuilderVisualizer visualizer = new BuilderVisualizer();
            rawVisualization.getVisualization().forEach(visualizer::addCoordinates);
            PropertyGraphBuilder propertyGraphBuilder = new PropertyGraphBuilder(rawVisualization.getGraph());
            GraphVisualization<PropertySupportingGraph> visualization = visualizer.generateVisual(propertyGraphBuilder.build());
            stack.push(visualization);

//            Rectangle oldRec = state.getRectangle();
//            Rectangle optimalRec = DrawManager.getOptimalRectangle(visualization.getVisualization(), visualization.getGraph(), 0.1, oldRec);
//            state.setRectangle(optimalRec);

            dialog.dismiss();
        });

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
