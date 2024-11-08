package model.item.furniture;

import java.awt.geom.Point2D;
import java.io.File;

public class Sofa extends Furniture {
    public Sofa(Point2D.Float position, int rotation) {
        super(position, rotation, new File("sofa.png"));
    }
}
