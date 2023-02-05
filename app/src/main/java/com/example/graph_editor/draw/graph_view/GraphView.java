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
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.extensions.CanvasManager;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.point_mapping.PointMapper;
import com.example.graph_editor.point_mapping.ScreenPoint;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack.ObservableStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class GraphView extends View {
    private final int baseVertexRadius = 7;
    private final int baseEdgeWidth = 5;

    private Paint vertexPaint;
    private double vertexRadius = baseVertexRadius;
    private Paint edgePaint;
    private boolean fixedWidth;
    private boolean interactive = false;
    private PointMapper mapper;
    private boolean isLazyInitialised = false;
    private CanvasManager canvasManager;
    private ObservableStack<GraphVisualization<PropertySupportingGraph>> stack;
    private State state;

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
        vertexPaint.setColor(Color.BLUE);
        vertexPaint.setStyle(Paint.Style.FILL);
        vertexPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        edgePaint = new Paint();
        edgePaint.setColor(Color.GRAY);
        edgePaint.setStyle(Paint.Style.FILL);
        edgePaint.setStrokeWidth(baseEdgeWidth);
        edgePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    // !! this alone is not enough, all due to height being lazily calculated
    public void initialize(CanvasManager canvasManager, ObservableStack<GraphVisualization<PropertySupportingGraph>> stack, State state, boolean interactive, PointMapper mapper) {
        this.canvasManager = canvasManager;
        this.interactive = interactive;
        this.stack = stack;
        this.state = state;
        this.mapper = mapper;
        isLazyInitialised = false;
        postInvalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void lazyInitialize() {
//        Rectangle rec = new Rectangle(new Point(0, 0), new Point(1.0, 1.0 * getHeight() / getWidth()));
//        Rectangle optimalRec = DrawManager.getOptimalRectangle(stack.getCurrent().getVisualization(), stack.getCurrent().getGraph(),0.1, rec);
//        state.setRectangle(optimalRec);
        if (interactive) {
            GraphOnTouchListener onTouchListener = new GraphOnTouchListener(getContext(), this, stack, state, mapper);
            this.setOnTouchListener(onTouchListener);
        }
        isLazyInitialised = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isLazyInitialised) {   //has to be done here instead of in init or initializeGraph since height is lazily calculated
            lazyInitialize();
        }

        fixedWidth = Settings.getFixedWidth(getContext());
        GraphVisualization<PropertySupportingGraph> visualization = stack.getCurrent();
        Graph graph = visualization.getGraph();

//        vertexRadius = getDrawWidth(rectangle.getScale(), baseVertexRadius);
//        edgePaint.setStrokeWidth((float)getDrawWidth(rectangle.getScale(), baseEdgeWidth));
        edgePaint.setStrokeWidth(baseEdgeWidth);

        CanvasManager.EdgeDrawer edgeDrawer =
                canvasManager.getEdgeDrawer().orElse((edge, r, canvas1) ->
                        drawEdge(
                                canvas1,
                                mapper.mapIntoView(visualization.getVertexPoint(edge.getSource())),
                                mapper.mapIntoView(visualization.getVertexPoint(edge.getTarget()))
//TODO use graphType?           graph.getType()
                        )
                );
        graph.getEdges().forEach(e -> edgeDrawer.drawEdge(
                e,
                mapper,
                canvas)
        );

        CanvasManager.VertexDrawer vertexDrawer =
                canvasManager.getVertexDrawer().orElse((v, r, canvas1) ->
                        drawVertex(canvas1, mapper.mapIntoView(visualization.getVertexPoint(v))));
        graph.getVertices().forEach(v -> vertexDrawer.drawVertex(v, mapper, canvas));

        canvasManager.getExtendedDrawers().forEach(drawer -> drawer.drawElements(mapper, canvas));
    }

    private void drawVertex(Canvas canvas, ScreenPoint point) {
        canvas.drawCircle(point.getX(), point.getY(), (float)vertexRadius, vertexPaint);
    }

    private void drawEdge(Canvas canvas, ScreenPoint start, ScreenPoint end /*, GraphType type*/) {
        canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(), edgePaint);
//        if (type == GraphType.DIRECTED) {
//            drawArrow(edgePaint, canvas, x1, y1, x2, y2);
//        }
    }

    private void drawArrow(Paint paint, Canvas canvas, float x1, float y1, float x2, float y2) {
//        float radius = (float)getDrawWidth(state.getRectangle().getScale(), 50);
        float radius = baseEdgeWidth * 10;
        float angle = 30;

        float angleRad= (float) (PI*angle/180.0f);
        float lineAngleRad= (float) (atan2(y2-y1,x2-x1));

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x2, y2);
        path.lineTo((float)(x2-radius*cos(lineAngleRad - (angleRad / 2.0))),
                (float)(y2-radius*sin(lineAngleRad - (angleRad / 2.0))));
        path.lineTo((float)(x2-radius*cos(lineAngleRad + (angleRad / 2.0))),
                (float)(y2-radius*sin(lineAngleRad + (angleRad / 2.0))));
        path.close();

        canvas.drawPath(path, paint);
    }

    private double getDrawWidth(double scale, double value) {
        double fun = fixedWidth ? 1 : scale;
        return value / fun * (getWidth()/1000.0);
    }

    public Point getRelative(Point point) {
        return new Point(point.getX()/getWidth(), point.getY()/getHeight());
    }

    public GraphVisualization<PropertySupportingGraph> getVisualization() {
        return stack.getCurrent();
    }
    public State getState() {
        return state;
    }
}
