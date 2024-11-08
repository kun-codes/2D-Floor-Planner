package model.item.fixture;

import java.awt.geom.Point2D;
import java.io.File;

public class Shower extends Fixture {
    public Shower(Point2D.Float position, int rotation) {
        super(position, rotation, new File("shower.png"));
    }
}