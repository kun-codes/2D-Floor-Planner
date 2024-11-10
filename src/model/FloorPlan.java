package model;

// a new test comment

import model.item.fixture.Fixture;
import model.item.furniture.Furniture;
import model.opening.Door;
import model.opening.Window;
import model.rooms.Room;

import java.util.ArrayList;
import java.util.List;

public class FloorPlan {
    private List<Room> rooms;
    private List<Door> doors;
    private List<Window> windows;
    private List<Furniture> furnitures;
    private List<Fixture> fixtures;

    public FloorPlan() {
        rooms = new ArrayList<>();
        doors = new ArrayList<>();
        windows = new ArrayList<>();
        furnitures = new ArrayList<>();
        fixtures = new ArrayList<>();
    }

    public void savePlan() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static FloorPlan loadPlan(String filePath) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented yet");

    }
}
