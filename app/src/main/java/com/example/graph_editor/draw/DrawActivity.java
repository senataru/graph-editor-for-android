package com.example.graph_editor.draw;

import static com.example.graph_editor.draw.ExtensionsMenuOptions.extensionsOptions;
import static com.example.graph_editor.menu.SharedPrefNames.CURRENT_GRAPH;
import static com.example.graph_editor.menu.SharedPrefNames.CURRENT_GRAPH_ID;
import static com.example.graph_editor.menu.SharedPrefNames.EDGE_PROPERTIES;
import static com.example.graph_editor.menu.SharedPrefNames.GRAPH_TYPE;
import static com.example.graph_editor.menu.SharedPrefNames.VERTEX_PROPERTIES;

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
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.DiscardPopup;
import com.example.graph_editor.draw.popups.SavePopup;
import com.example.graph_editor.extensions.CanvasManagerImpl;
import com.example.graph_editor.extensions.GraphActionManagerImpl;
import com.example.graph_editor.extensions.GraphMenuManager;
import com.example.graph_editor.extensions.GraphMenuManagerImpl;
import com.example.graph_editor.model.graph_storage.GraphScanner;
import com.example.graph_editor.model.graph_storage.GraphWriter;
import com.example.graph_editor.model.graph_storage.InvalidGraphStringException;
import com.example.graph_editor.model.GraphType;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.model.state.StateStackImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;

public class DrawActivity extends AppCompatActivity {
    private final static int extensions_start = 1;
    private GraphView graphView;
    private StateStack stateStack;
    private long currentGraphId = -1;
    private String graphString;
    private Map<String, String> graphVertexPropertyStrings = new HashMap<>();
    private Map<String, String> graphEdgePropertyStrings = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        currentGraphId = sharedPref.getLong(CURRENT_GRAPH_ID, -1);
        graphString = sharedPref.getString(CURRENT_GRAPH, null);
        int choiceOrd = sharedPref.getInt(GRAPH_TYPE, 0);
        Set<String> vertexPropertyStrings = sharedPref.getStringSet(VERTEX_PROPERTIES, Collections.emptySet());
        Set<String> edgePropertyStrings = sharedPref.getStringSet(EDGE_PROPERTIES, Collections.emptySet());

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(CURRENT_GRAPH_ID);
        editor.remove(CURRENT_GRAPH);
        editor.remove(GRAPH_TYPE);
        editor.remove(VERTEX_PROPERTIES);
        editor.remove(EDGE_PROPERTIES);
        editor.apply();

        graphView = findViewById(R.id.viewGraph);

        Graph graph = null;
        List<Graph> stack = null;
        int pointer = 0;
        GraphAction modeType = new GraphAction.MoveCanvas();
        if (savedInstanceState != null) { // re-initialize
            try {
                stack = GraphScanner.fromExactList(savedInstanceState.getString("GraphStack"));
            } catch (InvalidGraphStringException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed graph list scanning");
            }
            pointer = savedInstanceState.getInt("Pointer");
            graph = stack.get(pointer);
//            modeType = ActionModeType.valueOf(savedInstanceState.getString("ActionType"));
            currentGraphId = savedInstanceState.getLong(CURRENT_GRAPH_ID, -1);
        } else { // either from browse or new graph
            if (graphString != null) {  // from browse
                try {
                    graph = GraphScanner.fromExact(graphString);
                    addAllProperties(graph, vertexPropertyStrings, edgePropertyStrings);
                } catch (InvalidGraphStringException e) {
                    e.printStackTrace();
                }
            }
            if (graph == null) {
                GraphType choice = GraphType.values()[choiceOrd];
                graph = new GraphFactory(choice).produce();
            }
        }
        assert graph != null;
        stateStack = new StateStackImpl(
                () -> {
                    invalidateOptionsMenu();
                    graphView.update(stateStack.getCurrentState().getGraphAction());
                },
                // the rectangle is temporary and will be replaced as soon as possible (when the height will be known)
                new State(graph, new Rectangle(new Point(0, 0), new Point(1, 1)), new GraphAction.MoveCanvas()),
                stack,
                pointer
        );
        graphView.initialize(new CanvasManagerImpl(), stateStack,true);
        stateStack.getCurrentState().addObserver(graphView);

        NavigationButtonCollection buttonCollection = new NavigationButtonCollection(this, stateStack);
        buttonCollection.add(findViewById(R.id.btnVertex), new GraphAction.NewVertex());
        buttonCollection.add(findViewById(R.id.btnEdge), new GraphAction.NewEdge());
        buttonCollection.add(findViewById(R.id.btnMoveObject), new GraphAction.MoveObject());
        buttonCollection.add(findViewById(R.id.btnMoveCanvas), new GraphAction.MoveCanvas());
        buttonCollection.add(findViewById(R.id.btnRemoveObject), new GraphAction.RemoveObject());

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
        stateStack.getCurrentState().setGraphAction(modeType);
    }

    private void addAllProperties(Graph graph, Set<String> vertexPropertyStrings,
                                  Set<String> edgePropertyStrings) throws InvalidGraphStringException {
        for (String vertexPropertyString : vertexPropertyStrings) {
            GraphScanner.addVertexProperty(graph, vertexPropertyString);
        }
        for (String edgePropertyString : edgePropertyStrings) {
            GraphScanner.addEdgeProperty(graph, edgePropertyString);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stateStack.getCurrentState().removeObserver(graphView);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String s = GraphWriter.toExactList(stateStack.getGraphStack());
        outState.putString("GraphStack", s);
        outState.putInt("Pointer", stateStack.getPointer());
//        outState.putString("ActionType", stateStack.getCurrentState().getGraphAction().name());
        outState.putLong("currentGraphId", currentGraphId);
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

    public void updateGraph(long id, String string) {
        currentGraphId = id;
        graphString = string;
    }

    public void updateGraphVertexProperties(Map<String, String> propertyStrings) {
        for(String propertyName : propertyStrings.keySet()) {
            updateGraphVertexProperty(propertyName, propertyStrings.get(propertyName));
        }
    }

    public void updateGraphVertexProperty(String name, String value) {
        Objects.requireNonNull(name, "Property name can not be null");
        Objects.requireNonNull(value, "Property value can not be null");
        graphVertexPropertyStrings.put(name, value);
    }

    public void updateGraphEdgeProperties(Map<String, String> propertyStrings) {
        for(String propertyName : propertyStrings.keySet()) {
            updateGraphEdgeProperty(propertyName, propertyStrings.get(propertyName));
        }
    }

    public void updateGraphEdgeProperty(String name, String value) {
        Objects.requireNonNull(name, "Property name can not be null");
        Objects.requireNonNull(value, "Property value can not be null");
        graphEdgePropertyStrings.put(name, value);
    }

    public void makeSave(Runnable afterTask) {
        if(currentGraphId == -1) {
            new SavePopup().show(stateStack.getCurrentState().getGraph(), this, afterTask);
        } else {
            Graph graph = stateStack.getCurrentState().getGraph();
            graphString = GraphWriter.toExact(graph);
            graphVertexPropertyStrings = GraphWriter.getAllVertexPropertyStrings(graph);
            graphEdgePropertyStrings = GraphWriter.getAllEdgePropertyStrings(graph);
            SavesDatabase database = SavesDatabase.getDbInstance(getApplicationContext());
            database.saveDao().updateGraph(currentGraphId, graphString, System.currentTimeMillis());
            saveAllProperties(database);

            Toast.makeText(this, "Graph saved", Toast.LENGTH_LONG).show();
            afterTask.run();
        }
    }

    private void saveAllProperties(SavesDatabase database) {
        for (String propertyString : graphVertexPropertyStrings.values()) {
            database.vertexPropertySaveDao().updateProperty(currentGraphId, propertyString,
                    System.currentTimeMillis());
        }
        for (String propertyString : graphEdgePropertyStrings.values()) {
            database.edgePropertySaveDao().updateProperty(currentGraphId, propertyString,
                    System.currentTimeMillis());
        }
    }

    ActivityResultLauncher<Intent> importActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        (ActivityResult result) -> ImportExportLaunchers.exportCommand(result, this, stateStack));

    ActivityResultLauncher<Intent> exportActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> ImportExportLaunchers.importCommand(result, this, stateStack));

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (stateStack.getCurrentState().isCurrentlyModified()) return false;
        if(!OptionsHandler.handle(item, this, stateStack, graphView,
                ()->makeSave(()->{}), importActivityResultLauncher, exportActivityResultLauncher))
            return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        Graph graph = stateStack.getCurrentState().getGraph();
        if(currentGraphId == -1) {
            if(GraphWriter.toExact(graph).equals(GraphWriter.toExact(new GraphFactory(graph.getType()).produce()))) {
                super.onBackPressed();
                return;
            }
        } else if(GraphWriter.toExact(graph).equals(graphString)) {
            super.onBackPressed();
            return;
        }
        new DiscardPopup(this, super::onBackPressed, () -> makeSave(super::onBackPressed)).show();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem undo = menu.findItem(R.id.options_btn_undo);
        MenuItem redo = menu.findItem(R.id.options_btn_redo);

        redo.setEnabled(stateStack.isRedoPossible());
        redo.getIcon().setAlpha(stateStack.isRedoPossible() ? 255 : 128);
        undo.setEnabled(stateStack.isUndoPossible());
        undo.getIcon().setAlpha(stateStack.isUndoPossible() ? 255 : 128);
        return true;
    }
}