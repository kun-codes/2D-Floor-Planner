package model.rooms;

import java.awt.Color;
import java.awt.geom.Point2D;

public class KitchenRoom extends Room {
    public KitchenRoom(int length, int breadth, Point2D.Float position) {
        super(length, breadth, position, new Color(216, 39, 75));
    }
}
