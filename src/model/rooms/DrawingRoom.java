package model.rooms;

import java.awt.Color;
import java.awt.geom.Point2D;

public class DrawingRoom extends Room {
    public DrawingRoom(int length, int breadth, Point2D.Float position) {
        super(length, breadth, position, new Color(240, 173, 15));
    }
}
