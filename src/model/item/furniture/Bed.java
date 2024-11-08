package model.item.furniture;

import java.awt.geom.Point2D;
import java.io.File;

public class Bed extends Furniture {
    public Bed(Point2D.Float position, int rotation) {
        super(position, rotation, new File("bed.png"));
    }
}
