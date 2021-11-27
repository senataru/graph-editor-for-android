package com.example.graph_editor.draw;

import android.util.Pair;

import com.example.graph_editor.R;
import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;

import java.util.function.Function;

public class Frame {
    private Rectangle rec;
    private double scale;
    private double dx;
    private double dy;

    public Frame(Rectangle rec, double scale) {
        this.rec = rec;
        this.scale = scale;
    }

    public Rectangle getRectangle() {
        return rec;
    }

    public void updateRectangle(Rectangle newRec) {
        this.scale = newRec.getHeight()/this.rec.getHeight();
        this.rec = newRec;
    }

    public void setScale(double s) {
        double rescale = s/this.scale;

        this.rec = new Rectangle(this.rec, rescale);

        this.scale = s;
    }

    public void translate(double dxNew, double dyNew) {
//        double ddx = (this.dx - dxNew) * scale;
//        double ddy = (this.dy - dyNew) * scale;

//        this.rec = new Rectangle(this.rec, ddx, ddy);
        this.rec = new Rectangle(this.rec, dxNew*scale, dyNew*scale);

//        this.dx = dxNew;
//        this.dy = dyNew;
    }

    public double getScale() { return this.scale; }
}
