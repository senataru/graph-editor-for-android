package com.example.graph_editor.model.mathematics;

public class Frame {
    private Rectangle rec;
    private double scale;

    public static final float MAX_SCALE = 16f;
    public static final float MIN_SCALE = 1/16f;

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

    public void rescale(double scale) {
//        double newScale = Math.min(Math.max(scale*this.scale, MIN_SCALE), MAX_SCALE);
        double newScale = scale*this.scale;

        this.rec = new Rectangle(this.rec, newScale/this.scale);

        this.scale = newScale;
    }

    public void translate(double dxNew, double dyNew) {
        this.rec = new Rectangle(this.rec, dxNew*scale, dyNew*scale);
    }

    public double getScale() { return this.scale; }

    public Frame deepCopy() {
        return new Frame(rec.deepCopy(), scale);
    }
}
