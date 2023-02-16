package com.example.graph_editor.draw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.GeneratePopup;
import com.example.graph_editor.draw.popups.SavePopup;
import com.example.graph_editor.draw.popups.SettingsPopup;

import java.util.Map;
import java.util.Objects;

import graph_editor.extensions.OnOptionSelection;
import graph_editor.graph.SimpleGraphBuilder;
import graph_editor.graph.VersionStack;
import graph_editor.graph_generators.GraphGeneratorBipartiteClique;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;
import graph_editor.visual.PlanarGraphVisualizer;

public class OptionsHandler {
    @SuppressLint("NonConstantResourceId")
    public static boolean handle(@NonNull MenuItem item, DrawActivity context, VersionStack<GraphVisualization<PropertySupportingGraph>> stack,
                                 GraphView graphView, Runnable makeSave,
                                 ActivityResultLauncher<Intent> importActivityResultLauncher,
                                 ActivityResultLauncher<Intent> exportActivityResultLauncher,
                                 Map<Integer, OnOptionSelection> extensionsOptions) {
        if (extensionsOptions.containsKey(item.getItemId())) {
            Objects
                    .requireNonNull(extensionsOptions.get(item.getItemId()))
                    .handle(stack);
            return true;
        }
        GraphVisualization<PropertySupportingGraph> visualization;
        switch (item.getItemId()) {
            case R.id.options_btn_save:
                makeSave.run();
                return true;
            case R.id.options_btn_redo:
                stack.redo();
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_undo:
                stack.undo();
                graphView.postInvalidate();
                return true;
            //more actions
            case R.id.options_btn_clear:
                PropertySupportingGraph emptyGraph = new PropertyGraphBuilder(new SimpleGraphBuilder(0).build()).build();
                visualization = new BuilderVisualizer().generateVisual(emptyGraph);
                stack.push(visualization);
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_arrange: // TODO: change strings to enums
                visualization = new PlanarGraphVisualizer().generateVisual(stack.getCurrent().getGraph() , stack.getCurrent(), "arrange");
                stack.push(visualization);
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_planar_arrange:
                visualization = new PlanarGraphVisualizer().generateVisual(stack.getCurrent().getGraph() , stack.getCurrent(), "arrangePlanar");
                stack.push(visualization);
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_makePlanar:
                visualization = new PlanarGraphVisualizer().generateVisual(stack.getCurrent().getGraph() , stack.getCurrent(), "planar");
                stack.push(visualization);
                graphView.postInvalidate();
                return true;

//            case R.id.options_btn_normalize:
//                stateStack.backup();
//                State state = stateStack.getCurrentState();
//                DrawManager.normalizeGraph(state.getGraph());
//                Rectangle newRectangle = DrawManager.getOptimalRectangle(state.getGraph(), 0.1, state.getRectangle());
//                state.setRectangle(new Rectangle(newRectangle, 1.2));
//                graphView.postInvalidate();
//                return true;

            //TODO reimplement
//            case R.id.options_btn_recenter:
//                visualization = stack.getCurrent();
//                Rectangle newRectangle1 = DrawManager.getOptimalRectangle(visualization.getVisualization(), visualization.getGraph(), 0.1, state.getRectangle());
//                state.setRectangle(newRectangle1);
//                graphView.postInvalidate();
//                return true;

            case R.id.options_btn_settings:
                new SettingsPopup(context, graphView::postInvalidate).show();
                return true;
            case R.id.options_btn_save_as:
                new SavePopup().show(stack.getCurrent(), context, ()->{});
                return true;
//            case R.id.options_btn_export_txt:
//                new ShareAsTxtIntent(context, stack.getCurrent()).show();
//                return true;
//            case R.id.options_btn_import_txt:
//                new ImportFromTxtPopup(context, stack, state).show();
//                return true;
            case R.id.options_btn_export_file:
                Intent exportAsFileIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                exportAsFileIntent.setType("text/plain");
                exportAsFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                exportAsFileIntent = Intent.createChooser(exportAsFileIntent, "Choose where to save the graph");
                exportActivityResultLauncher.launch(exportAsFileIntent);
                return true;
            case R.id.options_btn_import_file:
                Intent importFromFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                importFromFileIntent.setType("text/plain");

                importFromFileIntent = Intent.createChooser(importFromFileIntent, "Choose file containing a graph");
                importActivityResultLauncher.launch(importFromFileIntent);
                return true;
            //generate graph
            //TODO retrieve disabled generators

//            case R.id.generate_btn_cycle:
//                new GeneratePopup(context, graphStack, new GraphGeneratorCycle()).show();
//                return true;
//            case R.id.generate_btn_clique:
//                new GeneratePopup(context, graphStack, new GraphGeneratorClique()).show();
//                return true;
            case R.id.generate_btn_bipartite_clique:
                new GeneratePopup(context, stack, new GraphGeneratorBipartiteClique()).show();
                return true;
//            case R.id.generate_btn_full_binary_tree:
//                new GeneratePopup(context, graphStack, new GraphGeneratorFullBinaryTree()).show();
//                return true;
//            case R.id.generate_btn_grid:
//                new GeneratePopup(context, graphStack, new GraphGeneratorGrid()).show();
//                return true;
//            case R.id.generate_btn_king_grid:
//                new GeneratePopup(context, graphStack, new GraphGeneratorKingGrid()).show();
//                return true;
            default:
                return false;
        }
    }
}
