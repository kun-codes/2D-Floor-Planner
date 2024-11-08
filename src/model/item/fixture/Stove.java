package model.item.fixture;

import java.awt.geom.Point2D;
import java.io.File;

public class Stove extends Fixture {
    public Stove(Point2D.Float position, int rotation) {
        super(position, rotation, new File("stove.png"));
    }
}