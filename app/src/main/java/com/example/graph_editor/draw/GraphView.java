package com.example.graph_editor.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.EdgeImpl;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.GraphFactory;
import com.example.graph_editor.model.GraphImpl;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.VertexImpl;
import com.example.graph_editor.model.mathematics.Point;

import java.util.List;

public class GraphView extends View {

    private Paint vertexPaint;
    private Paint edgePaint;

    private DrawManager manager;
    private Frame frame;


    public GraphView(Context context) {
        super(context);
        init(null);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        vertexPaint = new Paint();
        vertexPaint.setColor(Color.MAGENTA);
        vertexPaint.setStyle(Paint.Style.FILL);
        edgePaint = new Paint();
        edgePaint.setColor(Color.CYAN);
        edgePaint.setStyle(Paint.Style.FILL);
        edgePaint.setStrokeWidth(8);
        frame = new Frame(new Point(0, 0), new Point(1, 1));
    }

    public void setManager(DrawManager manager) {
        this.manager = manager;
        manager.setFrame(frame.getLeftTop(), frame.getRightBot());
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Edge e : manager.getEdges())
            drawEdge(canvas, e, e.getSource().getCurrentPoint(), e.getTarget().getCurrentPoint());

        for (Vertex v : manager.getVertices())
            drawVertex(canvas, v, v.getCurrentPoint());
    }

    private void drawVertex(Canvas canvas, Vertex vertex, Point point) {
        float x = (float)point.getX()*getWidth();
        float y = (float)point.getY()*getHeight();
        canvas.drawCircle(
                x, y, 10, vertexPaint
        );
//        canvas.drawText(vertex.);
    }

    private void drawEdge(Canvas canvas, Edge edge, Point start, Point end) {
        canvas.drawLine(
                (float)start.getX()*getWidth(), (float)start.getY()*getHeight(),
                (float)end.getX()*getWidth(), (float)end.getY()*getHeight(),
                edgePaint
        );
    }
}
