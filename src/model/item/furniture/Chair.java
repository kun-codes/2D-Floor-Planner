package model.item.furniture;

import java.awt.geom.Point2D;
import java.io.File;

public class Chair extends Furniture {
    public Chair(Point2D.Float position, int rotation) {
        super(position, rotation, new File("chair.png"));
    }
}
