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
import com.example.graph_editor.database.SavesDatabase;
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
import com.example.graph_editor.model.graph_storage.InvalidGraphStringException;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.point_mapping.PointMapperImpl;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

//    private long currentGraphId = -1;
//    private String graphString;
//    private Map<String, String> graphVertexPropertyStrings = new HashMap<>();
//    private Map<String, String> graphEdgePropertyStrings = new HashMap<>();

    private String name;
    private boolean stackChangedSinceLastSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);

        name = sharedPref.getString(CURRENT_GRAPH_NAME, null);

//        currentGraphId = sharedPref.getLong(CURRENT_GRAPH_ID, -1);
//        graphString = sharedPref.getString(CURRENT_GRAPH, null);
//        int choiceOrd = sharedPref.getInt(GRAPH_TYPE, 0);
//        Set<String> vertexPropertyStrings = sharedPref.getStringSet(VERTEX_PROPERTIES, Collections.emptySet());
//        Set<String> edgePropertyStrings = sharedPref.getStringSet(EDGE_PROPERTIES, Collections.emptySet());

        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.remove(CURRENT_GRAPH_ID);
//        editor.remove(CURRENT_GRAPH);
//        editor.remove(GRAPH_TYPE);
//        editor.remove(VERTEX_PROPERTIES);
//        editor.remove(EDGE_PROPERTIES);
        editor.remove(CURRENT_GRAPH_NAME);

        editor.apply();

        graphView = findViewById(R.id.viewGraph);
        GraphAction modeType = new NewVertex();
//        if (savedInstanceState != null) { // re-initialize
//            try {
//                stack = GraphScanner.fromExactList(savedInstanceState.getString("GraphStack"));
//            } catch (InvalidGraphStringException e) {
//                e.printStackTrace();
//                throw new RuntimeException("Failed graph list scanning");
//            }
//            pointer = savedInstanceState.getInt("Pointer");
//            graph = stack.get(pointer);
////            modeType = ActionModeType.valueOf(savedInstanceState.getString("ActionType"));
//            currentGraphId = savedInstanceState.getLong(CURRENT_GRAPH_ID, -1);
//        } else { // either from browse or new graph
//            if (graphString != null) {  // from browse
//                try {
//                    graph = GraphScanner.fromExact(graphString);
//                    addAllProperties(graph, vertexPropertyStrings, edgePropertyStrings);
//                } catch (InvalidGraphStringException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (graph == null) {
//                GraphType choice = GraphType.values()[choiceOrd];
//                graph = new GraphFactory(choice).produce();
//            }
//        }
//        assert graph != null;

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

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void addAllProperties(Graph graph, Set<String> vertexPropertyStrings,
                                  Set<String> edgePropertyStrings) throws InvalidGraphStringException {
        for (String vertexPropertyString : vertexPropertyStrings) {
//            GraphScanner.addVertexProperty(graph, vertexPropertyString);
        }
        for (String edgePropertyString : edgePropertyStrings) {
//            GraphScanner.addEdgeProperty(graph, edgePropertyString);
        }
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

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void updateGraph(long id, String string) {
//        currentGraphId = id;
//        graphString = string;
    }

    public void updateGraphVertexProperties(Map<String, String> propertyStrings) {
        for(String propertyName : propertyStrings.keySet()) {
            updateGraphVertexProperty(propertyName, propertyStrings.get(propertyName));
        }
    }

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void updateGraphVertexProperty(String name, String value) {
        Objects.requireNonNull(name, "Property name can not be null");
        Objects.requireNonNull(value, "Property value can not be null");
//        graphVertexPropertyStrings.put(name, value);
    }

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void updateGraphEdgeProperties(Map<String, String> propertyStrings) {
        for(String propertyName : propertyStrings.keySet()) {
            updateGraphEdgeProperty(propertyName, propertyStrings.get(propertyName));
        }
    }

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void updateGraphEdgeProperty(String name, String value) {
        Objects.requireNonNull(name, "Property name can not be null");
        Objects.requireNonNull(value, "Property value can not be null");
//        graphEdgePropertyStrings.put(name, value);
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

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void saveAllProperties(SavesDatabase database) {
//        for (String propertyString : graphVertexPropertyStrings.values()) {
//            database.vertexPropertySaveDao().updateProperty(currentGraphId, propertyString,
//                    System.currentTimeMillis());
//        }
//        for (String propertyString : graphEdgePropertyStrings.values()) {
//            database.edgePropertySaveDao().updateProperty(currentGraphId, propertyString,
//                    System.currentTimeMillis());
//        }
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