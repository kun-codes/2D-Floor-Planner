package model.item.fixture;

import java.awt.geom.Point2D;
import java.io.File;

public class Commode extends Fixture {
    public Commode(Point2D.Float position, int rotation) {
        super(position, rotation, new File("commode.png"));
    }
}