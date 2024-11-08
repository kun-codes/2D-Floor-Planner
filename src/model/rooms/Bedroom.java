package model.rooms;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Bedroom extends Room {
    public Bedroom(int length, int breadth, Point2D.Float position) {
        super(length, breadth, position, Color.GREEN);
    }
}