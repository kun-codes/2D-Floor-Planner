package model.rooms;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Room {
    private int length;
    private int breadth;
    private Point2D.Float position;
    private Color color;

    public Room(int length, int breadth, Point2D.Float position, Color color) {
        this.length = length;
        this.breadth = breadth;
        this.position = position;
        this.color = color;
    }

    public boolean checkOverlap() {
        // Implement overlap checking logic for Room
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setPosition(Point2D.Float position) {
        this.position = position;
    }

    public Point2D.Float getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getBreadth() {
        return breadth;
    }

    public void setBreadth(int breadth) {
        this.breadth = breadth;
    }
}