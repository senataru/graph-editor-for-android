package com.example.graph_editor.draw;

import static com.example.graph_editor.draw.ExtensionsMenuOptions.extensionsOptions;
import static com.example.graph_editor.menu.SharedPrefNames.CURRENT_GRAPH_NAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.draw.graph_action.GraphActionObserver;
import com.example.graph_editor.draw.graph_action.MoveCanvas;
import com.example.graph_editor.draw.graph_action.NewVertex;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.DiscardPopup;
import com.example.graph_editor.draw.popups.SavePopup;
import com.example.graph_editor.extensions.CanvasManagerImpl;
import com.example.graph_editor.extensions.GraphActionManagerImpl;
import com.example.graph_editor.extensions.GraphMenuManager;
import com.example.graph_editor.extensions.GraphMenuManagerImpl;
import com.example.graph_editor.file_serialization.Loader;
import com.example.graph_editor.file_serialization.Saver;
import com.example.graph_editor.fs.FSDirectories;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.point_mapping.PointMapperImpl;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.ObservableStackImpl;
import graph_editor.graph.SimpleGraphBuilder;
import graph_editor.graph.VersionStack.ObservableStack;
import graph_editor.graph.VersionStackImpl;
import graph_editor.properties.PropertyGraphBuilder;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.BuilderVisualizer;
import graph_editor.visual.GraphVisualization;

public class DrawActivity extends AppCompatActivity {
    private final static int extensions_start = 1;
    private GraphView graphView;
    private ObservableStack<GraphVisualization<PropertySupportingGraph>> stack;

    private State state;

    private String name;
    private boolean stackChangedSinceLastSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);

        name = sharedPref.getString(CURRENT_GRAPH_NAME, null);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(CURRENT_GRAPH_NAME);

        editor.apply();

        graphView = findViewById(R.id.viewGraph);
        GraphAction modeType = new NewVertex();

        GraphVisualization<PropertySupportingGraph> visualization;
        if (name != null) {
            visualization = Loader.load(new File(getFilesDir(), FSDirectories.graphsDirectory), name);
        } else  {
            visualization = new BuilderVisualizer().generateVisual(new PropertyGraphBuilder(new SimpleGraphBuilder(0).build()).build());
        }

        stack = new ObservableStackImpl<>(new VersionStackImpl<>(visualization));
        state = new State(new NewVertex());
        graphView.initialize(new CanvasManagerImpl(), stack, state, true, new PointMapperImpl(graphView, new Point(0,0)));
        state.addObserver(actionObserver);
        stack.addObserver(stackObserver);

        NavigationButtonCollection buttonCollection = new NavigationButtonCollection(this, state);
        buttonCollection.add(findViewById(R.id.btnVertex), new NewVertex());

        //TODO implement asap
//        buttonCollection.add(findViewById(R.id.btnEdge), new GraphAction.NewEdge());
//        buttonCollection.add(findViewById(R.id.btnMoveObject), new ...());
        buttonCollection.add(findViewById(R.id.btnMoveCanvas), new MoveCanvas());
//        buttonCollection.add(findViewById(R.id.btnRemoveObject), new GraphAction.RemoveObject());

        for (Pair<String, GraphAction> it : GraphActionManagerImpl.getRegisteredActions()) {
            LinearLayout ll = findViewById(R.id.linearLayout);
            ImageButton imageButton = (ImageButton) getLayoutInflater().inflate(R.layout.action_button, ll, false);
            ll.addView(imageButton);
            System.out.println(getFilesDir().getAbsolutePath());
            //TODO when server implemented, change to use app filesystem and it.first
            imageButton.setImageResource(R.drawable.app_icon);
            buttonCollection.add(imageButton, it.second);
        }
        buttonCollection.setCurrent(modeType);
        state.setGraphAction(modeType);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        state.removeObserver(actionObserver);
        stack.removeObserver(stackObserver);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

//        String s = GraphWriter.toExactList(stack.getStack());
//        outState.putString("GraphStack", s);
//        outState.putInt("Pointer", graphStack.getPointer());
//        outState.putString("ActionType", stateStack.getCurrentState().getGraphAction().name());
//        outState.putLong("currentGraphId", currentGraphId);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.graph_options_menu, menu);

        //TODO change
        int id = extensions_start;
        Collection<Pair<String, GraphMenuManager.MenuOptionHandler>> options = GraphMenuManagerImpl.getRegisteredOptions();
        extensionsOptions = new HashMap<>();
        for (Pair<String, GraphMenuManager.MenuOptionHandler> it : options) {
            extensionsOptions.put(id, it.second);
            menu.add(0, id++, 0, it.first);
        }
        return true;
    }

    public void makeSave(Runnable afterTask) {
        if(name == null) {
            new SavePopup().show(stack.getCurrent(), this, afterTask);
        } else {
            GraphVisualization<PropertySupportingGraph> visualization = stack.getCurrent();
            Saver.save(this, new File(getFilesDir(), FSDirectories.graphsDirectory), name , (Serializable) visualization);

            Toast.makeText(this, "Graph saved", Toast.LENGTH_LONG).show();
            afterTask.run();
        }
        stackChangedSinceLastSave = false;
    }
    ActivityResultLauncher<Intent> importActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        (ActivityResult result) -> ImportExportLaunchers.exportCommand(result, this, stack, state));

    ActivityResultLauncher<Intent> exportActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> ImportExportLaunchers.importCommand(result, this, stack, state));

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (state.isCurrentlyModified()) return false;
        if(!OptionsHandler.handle(item, this, stack, state, graphView,
                ()->makeSave(()->{}), importActivityResultLauncher, exportActivityResultLauncher))
            return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!stackChangedSinceLastSave) {
            super.onBackPressed();
            return;
        }
        new DiscardPopup(this, super::onBackPressed, () -> makeSave(super::onBackPressed)).show();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem undo = menu.findItem(R.id.options_btn_undo);
        MenuItem redo = menu.findItem(R.id.options_btn_redo);

        redo.setEnabled(stack.isRedoPossible());
        redo.getIcon().setAlpha(stack.isRedoPossible() ? 255 : 128);
        undo.setEnabled(stack.isUndoPossible());
        undo.getIcon().setAlpha(stack.isUndoPossible() ? 255 : 128);
        return true;
    }

    private final GraphActionObserver actionObserver = action -> {
        graphView.postInvalidate();
    };

    public void setName(String name) {
        this.name = name;
    }

    private final ObservableStack.Observer<GraphVisualization<PropertySupportingGraph>> stackObserver = visualization -> {
        stackChangedSinceLastSave = true;
        invalidateOptionsMenu();
        graphView.postInvalidate();
    };
}
