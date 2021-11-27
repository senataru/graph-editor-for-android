package com.example.graph_editor.draw;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.graph_view.ImageButtonCollection;
import com.example.graph_editor.graphStorage.GraphScanner;
import com.example.graph_editor.graphStorage.InvalidGraphStringException;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.GraphFactory;
import com.example.graph_editor.model.GraphType;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;

import java.util.List;

public class DrawActivity extends AppCompatActivity {
    private GraphView graphView;

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

        assert graph != null;
        graphView.initializeGraph(graph.getDrawManager(), true);

        ImageButtonCollection collection = new ImageButtonCollection(this);
        collection.add(findViewById(R.id.btnVertex), () -> changeMode(ActionModeType.NEW_VERTEX));
        collection.add(findViewById(R.id.btnEdge), () -> changeMode(ActionModeType.NEW_EDGE));
        collection.add(findViewById(R.id.btnMoveObject), () -> changeMode(ActionModeType.MOVE_OBJECT));
        collection.add(findViewById(R.id.btnMoveCanvas), () -> changeMode(ActionModeType.MOVE_CANVAS));
        collection.add(findViewById(R.id.btnRemoveObject), () -> changeMode(ActionModeType.REMOVE_OBJECT));

        changeMode(ActionModeType.MOVE_CANVAS);
        collection.setCurrent(findViewById(R.id.btnMoveCanvas));

        final Graph finalGraph = graph;
        findViewById(R.id.buttonSave).setOnClickListener( v -> new SavePopup(this, this).show(finalGraph));
        findViewById(R.id.buttonClear).setOnClickListener(v -> {
            finalGraph.getVertices().clear(); graphView.postInvalidate();} );
    }

    private void changeMode(ActionModeType type) {
        ActionModeType.setCurrentModeType(type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActionModeType.removeObserver(graphView);
        ActionModeType.resetCurrentModeType();

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentGraph", null);
        editor.apply();
    }
}