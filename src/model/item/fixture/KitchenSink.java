package model.item.fixture;

import java.awt.geom.Point2D;
import java.io.File;

public class KitchenSink extends Fixture {
    public KitchenSink(Point2D.Float position, int rotation) {
        super(position, rotation, new File("kitchensink.png"));
    }
}