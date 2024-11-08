package model.item.furniture;

import model.item.Item;
import java.awt.geom.Point2D;
import java.io.File;

public abstract class Furniture extends Item {
    public Furniture(Point2D.Float position, int rotation, File image) {
        super(position, rotation, image);
    }
}
