package com.example.graph_editor.draw.graph_view;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.graph_editor.android_view_wrappers.CanvasWrapper;
import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.extensions.DrawerSource;
import com.example.graph_editor.model.GraphType;

import graph_editor.draw.IGraphDrawer;
import graph_editor.draw.point_mapping.CanvasDrawer;
import graph_editor.draw.point_mapping.PointMapper;
import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class GraphView extends View implements VersionStack.ObservableStack.Observer<GraphVisualization<PropertySupportingGraph>> {
    private final int baseVertexRadius = 7;
    private final int baseEdgeWidth = 5;
    private Paint vertexPaint;
    private double vertexRadius = baseVertexRadius;
    private Paint edgePaint;
    private boolean fixedWidth;
    private PointMapper mapper;
    private DrawerSource drawerSource;
    private GraphType graphType;
    private GraphVisualization<PropertySupportingGraph> visualization;

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
    public void initialize(
            DrawerSource drawerSource,
            PointMapper mapper,
            GraphType graphType,
            GraphVisualization<PropertySupportingGraph> visualization) {
        this.drawerSource = drawerSource;
        this.mapper = mapper;
        this.graphType = graphType;
        this.visualization = visualization;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        fixedWidth = Settings.getFixedWidth(getContext());

//        vertexRadius = getDrawWidth(rectangle.getScale(), baseVertexRadius);
//        edgePaint.setStrokeWidth((float)getDrawWidth(rectangle.getScale(), baseEdgeWidth));
//        edgePaint.setStrokeWidth(baseEdgeWidth);
        CanvasDrawer drawer = new CanvasWrapper(canvas);
        IGraphDrawer<PropertySupportingGraph> graphDrawer = new DefaultDrawer(mapper, drawer, graphType);
//                drawerSource.getDrawer(mapper, drawer).orElse(new DefaultDrawer(mapper, drawer, graphType));
        graphDrawer.drawGraph(visualization);
    }

    @Override
    public void notifyChange(GraphVisualization<PropertySupportingGraph> visualization) {
        this.visualization = visualization;
        postInvalidate();
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
        return visualization;
    }

    public GraphType getGraphType() {
        return graphType;
    }
}
