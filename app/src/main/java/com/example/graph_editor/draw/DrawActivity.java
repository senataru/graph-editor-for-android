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

    ZoomLayout zoomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        int choiceOrd = sharedPref.getInt("GraphType", 0);
        GraphType choice = GraphType.values()[choiceOrd];

        graphView = findViewById(R.id.viewGraph);
        ActionModeType.addObserver(graphView);
        zoomLayout = findViewById(R.id.layZoom);
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
//        for (int i = 0; i< 11; i++)
//            graph.addVertex();
//
//        List<Vertex> l = graph.getVertices();
//        l.get(0).setPoint(new Point(0.3f, 0.2f));
//        l.get(1).setPoint(new Point(0.2f, 0.4f));
//        l.get(2).setPoint(new Point(0.4f, 0.4f));
//        l.get(3).setPoint(new Point(0.1f, 0.6f));
//        l.get(4).setPoint(new Point(0.3f, 0.6f));
//        l.get(5).setPoint(new Point(0.5f, 0.6f));
//
//        l.get(6).setPoint(new Point(0.8f, 0.5f));
//        l.get(7).setPoint(new Point(0.9f, 0.8f));
//        l.get(8).setPoint(new Point(0.67f, 0.6f));
//        l.get(9).setPoint(new Point(0.93f, 0.58f));
//        l.get(10).setPoint(new Point(0.7f, 0.8f));
//
//        graph.addEdge(l.get(0), l.get(1));
//        graph.addEdge(l.get(0), l.get(2));
//        graph.addEdge(l.get(1), l.get(3));
//        graph.addEdge(l.get(1), l.get(4));
//        graph.addEdge(l.get(2), l.get(5));
//
//        graph.addEdge(l.get(6), l.get(7));
//        graph.addEdge(l.get(8), l.get(7));
//        graph.addEdge(l.get(8), l.get(9));
//        graph.addEdge(l.get(10), l.get(9));
//        graph.addEdge(l.get(10), l.get(6));

        assert graph != null;
        graphView.initializeGraph(graph.getDrawManager(), true);

        ImageButtonCollection collection = new ImageButtonCollection(this);
        collection.add(findViewById(R.id.btnVertex), () -> changeMode(ActionModeType.NEW_VERTEX));
        collection.add(findViewById(R.id.btnEdge), () -> changeMode(ActionModeType.NEW_EDGE));
        collection.add(findViewById(R.id.btnMoveObject), () -> changeMode(ActionModeType.MOVE_OBJECT));
        collection.add(findViewById(R.id.btnMoveCanvas), () -> changeMode(ActionModeType.MOVE_CANVAS));

        changeMode(ActionModeType.MOVE_CANVAS);
        collection.setCurrent(findViewById(R.id.btnMoveCanvas));

        final Graph finalGraph = graph;
        findViewById(R.id.buttonSave).setOnClickListener( v -> new SavePopup(this, zoomLayout).show(finalGraph));
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