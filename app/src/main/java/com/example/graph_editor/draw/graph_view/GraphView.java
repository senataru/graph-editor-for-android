package com.example.graph_editor.draw.graph_view;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.graph_editor.draw.ActionModeType;
import com.example.graph_editor.draw.ActionModeTypeObserver;
import com.example.graph_editor.draw.Frame;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Edge;
import com.example.graph_editor.model.GraphType;
import com.example.graph_editor.model.Vertex;
import com.example.graph_editor.model.mathematics.Point;

public class GraphView extends View implements ActionModeTypeObserver {
    private final int baseVertexRadius = 7;
    private final int baseEdgeWidth = 5;

    private Paint vertexPaint;
    private double vertexRadius = baseVertexRadius;
    private Paint edgePaint;
    private Paint highlightPaint;

    private DrawManager manager;
    public Frame frame;

    public Vertex highlighted = null;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vertexPaint.setColor(Color.rgb( 0.4f, 0.4f, 1f));
        } else {
            vertexPaint.setColor(Color.BLUE);
        }
        vertexPaint.setStyle(Paint.Style.FILL);
        vertexPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        highlightPaint = new Paint();
        highlightPaint.setColor(Color.DKGRAY);
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        edgePaint = new Paint();
        edgePaint.setColor(Color.GRAY);
        edgePaint.setStyle(Paint.Style.FILL);
        edgePaint.setStrokeWidth(baseEdgeWidth);
        edgePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initializeGraph(DrawManager manager, boolean interactive) {
        this.manager = manager;
        if (interactive)
            this.setOnTouchListener(new GraphOnTouchListenerImpl(this, manager));
        else
            this.setOnTouchListener(new GraphOnTouchListenerNull());
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (frame == null) {   //has to be done here instead of init since height is lazily calculated
            frame = new Frame(new Point(0, 0), new Point(1, 1.0*getHeight()/getWidth()));
//            frame = manager.getOptimalFrame(0.1, frame);
            manager.updateFrame(frame);
        }

        for (Edge e : manager.getEdges())
            drawEdge(canvas, e, manager.getRelative(e.getSource().getPoint()), manager.getRelative(e.getTarget().getPoint()));

        for (Vertex v : manager.getVertices())
            drawVertex(canvas, v, manager.getRelative(v.getPoint()), v==highlighted);
    }

    private void drawVertex(Canvas canvas, Vertex vertex, Point point, boolean highlighted) {
        float x = (float)point.getX()*getWidth();
        float y = (float)point.getY()*getHeight();

        if (highlighted) {
            canvas.drawCircle(x, y, (float)vertexRadius*2, highlightPaint);
        }

        canvas.drawCircle(x, y, (float)vertexRadius, vertexPaint);
//        canvas.drawText(vertex.);
    }

    private void drawEdge(Canvas canvas, Edge edge, Point start, Point end) {
        float x1 = (float) start.getX() * getWidth();
        float y1 = (float) start.getY() * getHeight();
        float x2 = (float) end.getX() * getWidth();
        float y2 = (float) end.getY() * getHeight();
        if (manager.getGraph().getType() == GraphType.UNDIRECTED) {
            canvas.drawLine(x1, y1, x2, y2, edgePaint);
        } else {
            System.out.println("directed");
            drawArrow(edgePaint, canvas, x1, y1, x2, y2);
        }
    }

    private void drawArrow(Paint paint, Canvas canvas, float from_x, float from_y, float to_x, float to_y)
    {
        float angle,anglerad, radius, lineangle;

        //values to change for other appearance *CHANGE THESE FOR OTHER SIZE ARROWHEADS*
        radius=25;
        angle=30;

        //some angle calculations
        anglerad= (float) (PI*angle/180.0f);
        lineangle= (float) (atan2(to_y-from_y,to_x-from_x));

        //tha line
        canvas.drawLine(from_x,from_y,to_x,to_y,paint);

        //tha triangle
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(to_x, to_y);
        path.lineTo((float)(to_x-radius*cos(lineangle - (anglerad / 2.0))),
                (float)(to_y-radius*sin(lineangle - (anglerad / 2.0))));
        path.lineTo((float)(to_x-radius*cos(lineangle + (anglerad / 2.0))),
                (float)(to_y-radius*sin(lineangle + (anglerad / 2.0))));
        path.close();

        canvas.drawPath(path, paint);
    }

    public void setScale(double s) {
        frame.setScale(s);
        manager.updateFrame(frame);
        vertexRadius = getDrawWidth(s, baseVertexRadius);
        edgePaint.setStrokeWidth((float)getDrawWidth(s, baseEdgeWidth));
        postInvalidate();
    }

    public void translate(double dx, double dy) {
        frame.translate(dx/getWidth(), dy/getWidth());
        manager.updateFrame(frame);
        postInvalidate();
    }

    private double getDrawWidth(double scale, double value) {
        return value / scale;
//        return value / ((scale-1)*4+1);
//        return value;
    }

    @Override
    public void update(ActionModeType newType) {
        highlighted = null;
        postInvalidate();
    }

    public Point getRelative(Point point) {
        return new Point(point.getX()/getWidth(), point.getY()/getHeight());
    }
}
