package com.example.graph_editor.draw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graph_editor.R;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.graph_view.NavigationButtonCollection;
import com.example.graph_editor.graph_storage.GraphScanner;
import com.example.graph_editor.graph_storage.GraphWriter;
import com.example.graph_editor.graph_storage.InvalidGraphStringException;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.GraphFactory;
import com.example.graph_editor.model.GraphType;
import com.example.graph_editor.model.graph_generators.GraphGeneratorBipartiteClique;
import com.example.graph_editor.model.graph_generators.GraphGeneratorClique;
import com.example.graph_editor.model.graph_generators.GraphGeneratorCycle;
import com.example.graph_editor.model.graph_generators.GraphGeneratorFullBinaryTree;
import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.model.state.StateStack;
import com.example.graph_editor.model.state.StateStackImpl;

import java.util.ArrayList;
import java.util.List;

public class DrawActivity extends AppCompatActivity {
    public static final String TAG = "DrawActivity";
    private GraphView graphView;
    private StateStack stateStack;
    private int currentGraphId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        currentGraphId = sharedPref.getInt("currentGraphId", -1);
        int choiceOrd = sharedPref.getInt("GraphType", 0);
        String graphString = sharedPref.getString("currentGraph", null);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("currentGraphId");
        editor.remove("GraphType");
        editor.remove("currentGraph");
        editor.apply();

        graphView = findViewById(R.id.viewGraph);
        ActionModeType.addObserver(graphView);

        Graph graph = null;
        List<Graph> stack = null;
        int pointer = 0;
        ActionModeType modeType = ActionModeType.MOVE_CANVAS;
        if (savedInstanceState != null &&
                savedInstanceState.containsKey("GraphStack") &&
                savedInstanceState.containsKey("Pointer") &&
                savedInstanceState.containsKey("ActionType")) { // re-initialize
            try {
                stack = GraphScanner.fromExactList(savedInstanceState.getString("GraphStack"));
            } catch (InvalidGraphStringException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed graph list scanning");
            }
            pointer = savedInstanceState.getInt("Pointer");
            graph = stack.get(pointer);
            modeType = ActionModeType.valueOf(savedInstanceState.getString("ActionType"));
        } else { // either from browse or new graph
            if (graphString != null) {  // from browse
                try {
                    graph = GraphScanner.fromExact(graphString);
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
                    graphView.update(ActionModeType.getCurrentModeType());
                },
                // the rectangle is temporary and will be replaced as soon as possible (when the height will be known)
                new State(graph, new Frame(new Rectangle(new Point(0, 0), new Point(1, 1)), 1)),
                stack,
                pointer
        );
        graphView.initialize(stateStack,true);

        NavigationButtonCollection collection = new NavigationButtonCollection(this);
        collection.add(findViewById(R.id.btnVertex), () -> changeMode(ActionModeType.NEW_VERTEX));
        collection.add(findViewById(R.id.btnEdge), () -> changeMode(ActionModeType.NEW_EDGE));
        collection.add(findViewById(R.id.btnMoveObject), () -> changeMode(ActionModeType.MOVE_OBJECT));
        collection.add(findViewById(R.id.btnMoveCanvas), () -> changeMode(ActionModeType.MOVE_CANVAS));
        collection.add(findViewById(R.id.btnRemoveObject), () -> changeMode(ActionModeType.REMOVE_OBJECT));

        switch (modeType) {
            case NEW_VERTEX:
                collection.setCurrent(findViewById(R.id.btnVertex));
                break;
            case NEW_EDGE:
                collection.setCurrent(findViewById(R.id.btnEdge));
                break;
            case MOVE_OBJECT:
                collection.setCurrent(findViewById(R.id.btnMoveObject));
                break;
            case MOVE_CANVAS:
                collection.setCurrent(findViewById(R.id.btnMoveCanvas));
                break;
            case REMOVE_OBJECT:
                collection.setCurrent(findViewById(R.id.btnRemoveObject));
                break;
        }
        changeMode(modeType);
    }

    private void changeMode(ActionModeType type) {
        ActionModeType.setCurrentModeType(type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActionModeType.removeObserver(graphView);
        ActionModeType.resetCurrentModeType();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String s = GraphWriter.toExactList(stateStack.getGraphStack());
        outState.putString("GraphStack", s);
        outState.putInt("Pointer", stateStack.getPointer());
        outState.putString("ActionType", ActionModeType.getCurrentModeType().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.graph_options_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_btn_save:
                if(currentGraphId == -1) {
                    new SavePopup(this).show(stateStack.getCurrentState().getGraph());
                } else {
                    SavesDatabase database = SavesDatabase.getDbInstance(getApplicationContext());
                    database.saveDao().updateGraph(currentGraphId, GraphWriter.toExact(stateStack.getCurrentState().getGraph()), System.currentTimeMillis());
                    Toast.makeText(this, "Graph saved", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.options_btn_redo:
                stateStack.redo();
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_undo:
                stateStack.undo();
                graphView.postInvalidate();
                return true;
            //more actions
            case R.id.options_btn_clear:
                stateStack.backup();
                stateStack.getCurrentState().getGraph().getVertices().clear();
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_normalize:
                stateStack.backup();
                State state = stateStack.getCurrentState();
                DrawManager.normalizeGraph(state.getGraph());
                Frame frame = state.getFrame();
                Rectangle newRectangle = DrawManager.getOptimalRectangle(state.getGraph(), 0.1, frame.getRectangle());
                state.setFrame(new Frame(newRectangle, 1.2));
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_settings:
                new SettingsPopup(this).show();
                return true;
            case R.id.options_btn_save_as:
                new SavePopup(this).show(stateStack.getCurrentState().getGraph());
                return true;
            case R.id.options_btn_share:
                new ShareIntent(this, stateStack).show();
                return true;
            case R.id.options_btn_import:
                new ImportPopup(this, stateStack).show();
                return true;
            //generate graph
            case R.id.generate_btn_cycle:
                new GeneratePopup(this, stateStack, new GraphGeneratorCycle()).show();
                return true;
            case R.id.generate_btn_clique:
                new GeneratePopup(this, stateStack, new GraphGeneratorClique()).show();
                return true;
            case R.id.generate_btn_bipartite_clique:
                new GeneratePopup(this, stateStack, new GraphGeneratorBipartiteClique()).show();
                return true;
            case R.id.generate_btn_full_binary_tree:
                new GeneratePopup(this, stateStack, new GraphGeneratorFullBinaryTree()).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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