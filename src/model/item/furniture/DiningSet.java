package model.item.furniture;

import java.awt.geom.Point2D;
import java.io.File;

public class DiningSet extends Furniture {
    public DiningSet(Point2D.Float position, int rotation) {
        super(position, rotation, new File("dining_set.png"));
    }
}
