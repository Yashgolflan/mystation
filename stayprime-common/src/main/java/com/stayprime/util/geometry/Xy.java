package com.stayprime.util.geometry;

/**
 * Created by benjamin on 18/1/17.
 */

public class Xy {
    private double x;
    private double y;

    public Xy() {
        this(0, 0);
    }

    public Xy(double x, double y) {
        set(x, y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double distance(double x, double y) {
        return Math.hypot(this.x - x, this.y - y);
    }

    public double distance(Xy xy) {
        return Math.hypot(this.x - xy.getX(), this.y - xy.getY());
    }

}
