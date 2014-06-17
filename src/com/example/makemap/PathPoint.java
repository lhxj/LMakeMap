package com.example.makemap;

/**
 * Created by Administrator on 2014/6/17.
 */
public class PathPoint {
    private int x;
    private int y;

    public PathPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return "x=" + this.x + "  y=" + this.y;
    }
}
