package com.example.graph_editor.draw;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graph_editor.R;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.GraphFactory;
import com.example.graph_editor.model.GraphType;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;

import java.util.List;

public class DrawActivity extends AppCompatActivity {
    private GraphView graphView;
    private ActionModeType modeType;

    TextView modeChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        SharedPreferences sharedPref = this.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        int choiceOrd = sharedPref.getInt("GraphType", 0);
        GraphType choice = GraphType.values()[choiceOrd];

        TextView txtChoice = findViewById(R.id.txtChoice);
        txtChoice.setText(choice.toString());

        modeChoice = findViewById(R.id.txtChoice2);
        changeMode(ActionModeType.NONE);

        graphView = findViewById(R.id.viewGraph);

        Graph graph = new GraphFactory(choice).produce();
        for (int i = 0; i< 11; i++)
            graph.addVertex();

        List<Vertex> l = graph.getVertices();
        l.get(0).setPoint(new Point(0.3f, 0.2f));
        l.get(1).setPoint(new Point(0.2f, 0.4f));
        l.get(2).setPoint(new Point(0.4f, 0.4f));
        l.get(3).setPoint(new Point(0.1f, 0.6f));
        l.get(4).setPoint(new Point(0.3f, 0.6f));
        l.get(5).setPoint(new Point(0.5f, 0.6f));

        l.get(6).setPoint(new Point(0.8f, 0.5f));
        l.get(7).setPoint(new Point(0.9f, 0.8f));
        l.get(8).setPoint(new Point(0.67f, 0.6f));
        l.get(9).setPoint(new Point(0.93f, 0.58f));
        l.get(10).setPoint(new Point(0.7f, 0.8f));

        graph.addEdge(l.get(0), l.get(1));
        graph.addEdge(l.get(0), l.get(2));
        graph.addEdge(l.get(1), l.get(3));
        graph.addEdge(l.get(1), l.get(4));
        graph.addEdge(l.get(2), l.get(5));

        graph.addEdge(l.get(6), l.get(7));
        graph.addEdge(l.get(8), l.get(7));
        graph.addEdge(l.get(8), l.get(9));
        graph.addEdge(l.get(10), l.get(9));
        graph.addEdge(l.get(10), l.get(6));

        graphView.setManager(graph.getDrawManager());


        findViewById(R.id.btnVertex).setOnClickListener(v -> changeMode(ActionModeType.NEW_VERTEX));
        findViewById(R.id.btnEdge).setOnClickListener(v -> {
            changeMode(ActionModeType.NEW_EDGE);
            Point old = l.get(10).getPoint();
            l.get(10).setPoint(new Point(old.getX()+0.01, old.getY()+0.01));
            graphView.scale(1);
        });
        findViewById(R.id.btnMoveObject).setOnClickListener(v -> {
            changeMode(ActionModeType.MOVE_OBJECT);
            graphView.setScale(0.95);
        });
        findViewById(R.id.btnMoveCanvas).setOnClickListener(v -> {
            changeMode(ActionModeType.MOVE_CANVAS);
            graphView.translate(0.05, 0.05);
        });
    }

    private void changeMode(ActionModeType type) {
        if (type == modeType) {
            modeType = ActionModeType.NONE;
        } else {
            modeType = type;
        }
        modeChoice.setText(modeType.toString());
    }
}