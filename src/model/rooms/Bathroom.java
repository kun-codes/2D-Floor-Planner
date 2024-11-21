package model.rooms;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Bathroom extends Room {
    public Bathroom(int length, int breadth, Point2D.Float position) {
        super(length, breadth, position, new Color(73, 141, 182));
    }
}
