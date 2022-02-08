package com.example.graph_editor.draw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graph_editor.R;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.action_mode_type.ActionModeType;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.DiscardPopup;
import com.example.graph_editor.draw.popups.GeneratePopup;
import com.example.graph_editor.draw.popups.SavePopup;
import com.example.graph_editor.draw.popups.SettingsPopup;
import com.example.graph_editor.draw.popups.ShareIntent;
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
import com.example.graph_editor.model.graph_generators.GraphGeneratorGrid;
import com.example.graph_editor.model.graph_generators.GraphGeneratorKingGrid;
import com.example.graph_editor.model.mathematics.Frame;
import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.model.state.StateStack;
import com.example.graph_editor.model.state.StateStackImpl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class DrawActivity extends AppCompatActivity {
    public static final String TAG = "DrawActivity";
    private GraphView graphView;
    private StateStack stateStack;
    private int currentGraphId = -1;
    private String graphString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        currentGraphId = sharedPref.getInt("currentGraphId", -1);
        int choiceOrd = sharedPref.getInt("GraphType", 0);
        graphString = sharedPref.getString("currentGraph", null);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("currentGraphId");
        editor.remove("GraphType");
        editor.remove("currentGraph");
        editor.apply();

        graphView = findViewById(R.id.viewGraph);

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
                    graphView.update(stateStack.getCurrentState().getActionModeType());
                },
                // the rectangle is temporary and will be replaced as soon as possible (when the height will be known)
                new State(graph, new Frame(new Rectangle(new Point(0, 0), new Point(1, 1)), 1), ActionModeType.MOVE_CANVAS),
                stack,
                pointer
        );
        graphView.initialize(stateStack,true);
        stateStack.getCurrentState().addObserver(graphView);

        NavigationButtonCollection buttonCollection = new NavigationButtonCollection(this, stateStack);
        buttonCollection.add(findViewById(R.id.btnVertex), ActionModeType.NEW_VERTEX);
        buttonCollection.add(findViewById(R.id.btnEdge), ActionModeType.NEW_EDGE);
        buttonCollection.add(findViewById(R.id.btnMoveObject), ActionModeType.MOVE_OBJECT);
        buttonCollection.add(findViewById(R.id.btnMoveCanvas), ActionModeType.MOVE_CANVAS);
        buttonCollection.add(findViewById(R.id.btnRemoveObject), ActionModeType.REMOVE_OBJECT);

        buttonCollection.setCurrent(modeType);
        stateStack.getCurrentState().setCurrentModeType(modeType);
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
        outState.putString("ActionType", stateStack.getCurrentState().getActionModeType().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.graph_options_menu, menu);
        return true;
    }

    public void makeSave(Runnable afterTask) {
        if(currentGraphId == -1) {
            new SavePopup(this, afterTask).show(stateStack.getCurrentState().getGraph());
        } else {
            SavesDatabase database = SavesDatabase.getDbInstance(getApplicationContext());
            database.saveDao().updateGraph(currentGraphId, GraphWriter.toExact(stateStack.getCurrentState().getGraph()), System.currentTimeMillis());
            Toast.makeText(this, "Graph saved", Toast.LENGTH_LONG).show();
            afterTask.run();
        }
    }

    ActivityResultLauncher<Intent> importActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        (ActivityResult result) -> {
            if( result.getResultCode() != Activity.RESULT_OK || result.getData() == null)
                return;
            Uri uri = result.getData().getData();
            Graph g;
            try {
                String content;
                try {
                    InputStream in = getContentResolver().openInputStream(uri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    content = total.toString();
                    System.out.println(content);
                }catch (Exception e) {
                    Toast.makeText(this, "Invalid text", Toast.LENGTH_SHORT).show();
                    return;
                }
                g = GraphScanner.fromExact(content);
            } catch (InvalidGraphStringException e) {
                Toast.makeText(this, "Invalid graph", Toast.LENGTH_SHORT).show();
                return;
            }
            stateStack.backup();
            Rectangle oldRec = stateStack.getCurrentState().getFrame().getRectangle();
            Rectangle optimalRec = DrawManager.getOptimalRectangle(g, 0.1, oldRec);
            State currentState = stateStack.getCurrentState();
            currentState.setGraph(g);
            currentState.setFrame(new Frame(optimalRec, optimalRec.getWidth()));
            stateStack.invalidateView();

            Toast.makeText(this, "Import complete", Toast.LENGTH_SHORT).show();
        }
    );

    ActivityResultLauncher<Intent> exportActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if( result.getResultCode() != Activity.RESULT_OK || result.getData() == null)
                    return;
                Uri uri = result.getData().getData();
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    outputStream.write(GraphWriter.toExact(stateStack.getCurrentState().getGraph()).getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    Toast.makeText(this, "Invalid text", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Export complete", Toast.LENGTH_SHORT).show();
            }
    );

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (stateStack.getCurrentState().isCurrentlyModified()) return false;

        switch (item.getItemId()) {
            case R.id.options_btn_save:
                makeSave(()->{});
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
            case R.id.options_btn_recenter:
                State state1 = stateStack.getCurrentState();
                Frame frame1 = state1.getFrame();
                Rectangle newRectangle1 = DrawManager.getOptimalRectangle(state1.getGraph(), 0.1, frame1.getRectangle());
                state1.setFrame(new Frame(newRectangle1, newRectangle1.getWidth()));
                graphView.postInvalidate();
                return true;
            case R.id.options_btn_settings:
                new SettingsPopup(this, ()->graphView.postInvalidate()).show();
                return true;
            case R.id.options_btn_save_as:
                new SavePopup(this, ()->{}).show(stateStack.getCurrentState().getGraph());
                return true;
            case R.id.options_btn_share:
                new ShareIntent(this, stateStack).show();
                return true;
            case R.id.options_btn_import:
                Intent importIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                importIntent.setType("text/plain");

                importIntent = Intent.createChooser(importIntent, "Choose file containing a graph");
                importActivityResultLauncher.launch(importIntent);
                return true;
            case R.id.options_btn_export:
                Intent exportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                exportIntent.setType("text/plain");
                exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
                exportIntent = Intent.createChooser(exportIntent, "Choose where to save the graph");
                exportActivityResultLauncher.launch(exportIntent);
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
            case R.id.generate_btn_grid:
                new GeneratePopup(this, stateStack, new GraphGeneratorGrid()).show();
                return true;
            case R.id.generate_btn_king_grid:
                new GeneratePopup(this, stateStack, new GraphGeneratorKingGrid()).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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