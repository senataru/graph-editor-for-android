package com.example.graph_editor.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;

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
        vertexPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        edgePaint = new Paint();
        edgePaint.setColor(Color.CYAN);
        edgePaint.setStyle(Paint.Style.FILL);
        edgePaint.setStrokeWidth(8);
        edgePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setManager(DrawManager manager) {
        this.manager = manager;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (frame == null) {   //has to be done here instead of init since height is lazily calculated
            frame = new Frame(new Point(0, 0), new Point(1, 1.0*getHeight()/getWidth()));
            manager.updateFrame(frame);
        }

        for (Edge e : manager.getEdges())
            drawEdge(canvas, e, e.getSource().getRelativePoint(), e.getTarget().getRelativePoint());

        for (Vertex v : manager.getVertices())
            drawVertex(canvas, v, v.getRelativePoint());
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

    public void setScale(double s) {
        frame.setScale(s);
        manager.updateFrame(frame);
        postInvalidate();
    }

    public void translate(double dx, double dy) {
        frame.translate(dx, dy);
        manager.updateFrame(frame);
        postInvalidate();
    }
}
