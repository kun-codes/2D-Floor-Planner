package model.item;

import java.awt.geom.Point2D;
import java.io.File;

public abstract class Item {
    private Point2D.Float position;
    private int rotation;
    private File image;

    public Item(Point2D.Float position, int rotation, File image) {
        this.position = position;
        this.rotation = rotation;
        this.image = image;
    }

    public void setPosition(Point2D.Float position) {
        this.position = position;
    }

    public Point2D.Float getPosition() {
        return position;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public File getImage() {
        return image;
    }

    public boolean checkOverlap() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}