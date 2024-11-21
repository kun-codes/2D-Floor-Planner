package model.rooms;

import java.awt.Color;
import java.awt.geom.Point2D;

public class DiningSpaceRoom extends Room {
    public DiningSpaceRoom(int length, int breadth, Point2D.Float position) {
        super(length, breadth, position, new Color(224, 212, 50));
    }
}
