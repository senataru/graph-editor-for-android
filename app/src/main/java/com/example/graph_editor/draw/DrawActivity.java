package com.example.graph_editor.draw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.graph_view.NavigationButtonCollection;
import com.example.graph_editor.graphStorage.GraphScanner;
import com.example.graph_editor.graphStorage.InvalidGraphStringException;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.GraphFactory;
import com.example.graph_editor.model.GraphType;

public class DrawActivity extends AppCompatActivity {
    private GraphView graphView;
    //TODO: remove this temporary solution
    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        int choiceOrd = sharedPref.getInt("GraphType", 0);
        GraphType choice = GraphType.values()[choiceOrd];

        graphView = findViewById(R.id.viewGraph);
        ActionModeType.addObserver(graphView);
        changeMode(ActionModeType.NONE);

        Graph graph = null;

        String graphString = sharedPref.getString("currentGraph", null);
        if (graphString != null) {
            try {
                graph = GraphScanner.fromExact(graphString);
            } catch (InvalidGraphStringException e) {
                e.printStackTrace();
            }
        } else {
            graph = new GraphFactory(choice).produce();
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentGraph", null);
        editor.apply();

        assert graph != null;
        graphView.initializeGraph(graph, true);

        NavigationButtonCollection collection = new NavigationButtonCollection(this);
        collection.add(findViewById(R.id.btnVertex), () -> changeMode(ActionModeType.NEW_VERTEX));
        collection.add(findViewById(R.id.btnEdge), () -> changeMode(ActionModeType.NEW_EDGE));
        collection.add(findViewById(R.id.btnMoveObject), () -> changeMode(ActionModeType.MOVE_OBJECT));
        collection.add(findViewById(R.id.btnMoveCanvas), () -> changeMode(ActionModeType.MOVE_CANVAS));
        collection.add(findViewById(R.id.btnRemoveObject), () -> changeMode(ActionModeType.REMOVE_OBJECT));

        changeMode(ActionModeType.MOVE_CANVAS);
        collection.setCurrent(findViewById(R.id.btnMoveCanvas));

        this.graph = graph;
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
                new SavePopup(this, this).show(graph);
                return true;
            case R.id.options_btn_clear:
                graph.getVertices().clear();
                graphView.postInvalidate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}