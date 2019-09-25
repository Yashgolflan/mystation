package com.stayprime.util.geometry;

/**
 * Created by benjamin on 18/1/17.
 */

public class Rect {
    double x;

    double width;

    double y;

    double height;

    public Rect(double left, double top, double width, double height) {
         this.x = left;
        this.width = width;
        this.y = top;
        this.height = height;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
