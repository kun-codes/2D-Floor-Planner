package model.item.fixture;

import model.item.Item;
import java.awt.geom.Point2D;
import java.io.File;

public abstract class Fixture extends Item {
    public Fixture(Point2D.Float position, int rotation, File image) {
        super(position, rotation, image);
    }
}