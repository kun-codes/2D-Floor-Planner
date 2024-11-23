package model.opening;

import java.awt.geom.Point2D;

public abstract class Opening {
    private Point2D.Float position;
    private String orientation;

    public Opening(Point2D.Float position, String orientation) {
        this.position = position;
        this.orientation = orientation;
    }

    public void setPosition(Point2D.Float position) {
        this.position = position;
    }

    public Point2D.Float getPosition() {
        return position;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getOrientation() {
        return orientation;
    }

    public boolean checkOverlap() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isDoor() {
        return this instanceof Door;
    }
}