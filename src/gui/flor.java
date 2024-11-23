package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import java.awt.geom.Point2D;
import model.rooms.*;

import model.opening.Opening;
import model.opening.Door;
import model.opening.Window;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.io.*;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.List;

public class flor {
    private JPanel rootPanel;
    private JTabbedPane objectPlace;
    private JButton drawingRoomButton;
    private JButton diningRoomButton;
    private JButton bedroomButton;
    private JButton kitchenButton;
    private JButton bathroomButton;
    private JButton studyButton;
    private JButton verticalDoorButton;
    private JButton verticalWindowButton;
    private JButton tableButton;
    private JButton bedButton;
    private JButton sofaButton;
    private JButton kitchenSinkButton;
    private JButton commodeButton;
    private JButton stoveButton;
    private JTextField heightTextField;
    private JTextField widthTextField;
    private JScrollPane scrollPane;
    private JPanel canvasPanel;
    private JPanel optionsPanel;
    private JLabel snappingLabel;
    private JCheckBox snappingCheckBox;
    private JToolBar toolBar;
    private JLabel doorWindowSizeLabel;
    private JTextField doorWindowSizeTextBox;
    private JButton horizontalDoorButton;
    private JButton horizontalWindowButton;
    private JButton chairButton;
    private JButton diningSetButton;
    private JPanel FurnitureLabel;
    private JButton washBasinButton;
    private JButton showerButton;
    private JButton SaveButton;
    private JButton LoadButton;


    private static final int GRID_SIZE = 50;
    private static final int SNAP_THRESHOLD = 10;
    private static JFrame frame;

    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;

    private static class RoomData {
        String type;
        int width;
        int height;
        int x;
        int y;
        Color color;
        
        RoomData(String type, int width, int height, int x, int y, Color color) {
            this.type = type;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
    private class OpeningPanel extends JPanel {
        protected final Opening opening;
        protected final int size;
        protected final boolean isVertical;
        private static final int ADJACENT_DOOR_THICKNESS = 4;
        private static final int EXTERIOR_DOOR_THICKNESS = 2;
        private static final int WINDOW_THICKNESS = 2;
    
        public OpeningPanel(Opening opening, int size, boolean isVertical) {
            this.opening = opening;
            this.size = size;
            this.isVertical = isVertical;
            setOpaque(true);
            setBackground(Color.WHITE);
            updateThickness();
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (!opening.isDoor()) {
                // Draw dashed line for windows
                Graphics2D g2d = (Graphics2D) g;
                float[] dash = {5.0f};
                g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2d.setColor(Color.BLACK);
                
                if (isVertical) {
                    int centerX = getWidth() / 2;
                    g2d.drawLine(centerX, 0, centerX, getHeight());
                } else {
                    int centerY = getHeight() / 2;
                    g2d.drawLine(0, centerY, getWidth(), centerY);
                }
            }
        }
    
        public void updateThickness() {
            int thickness;
            if (opening.isDoor()) {
                Point center = new Point(
                    getX() + getWidth()/2,
                    getY() + getHeight()/2
                );
                thickness = isWallAdjacent(center, isVertical) ? 
                    ADJACENT_DOOR_THICKNESS : EXTERIOR_DOOR_THICKNESS;
    
                // Update door color to match room
                updateOpeningColor(center);
            } else {
                thickness = WINDOW_THICKNESS;
                // Update window color to match room
                Point center = new Point(
                    getX() + getWidth()/2,
                    getY() + getHeight()/2
                );
                updateOpeningColor(center);
            }
            setSize(isVertical ? thickness : size, isVertical ? size : thickness);
        }
    
        private void updateOpeningColor(Point center) {
            for (Component comp : canvasPanel.getComponents()) {
                if (comp instanceof JPanel && !(comp instanceof OpeningPanel)) {
                    Rectangle bounds = comp.getBounds();
                    if (isVertical) {
                        if (center.y >= bounds.y && center.y <= bounds.y + bounds.height &&
                            (Math.abs(center.x - bounds.x) < SNAP_THRESHOLD ||
                             Math.abs(center.x - (bounds.x + bounds.width)) < SNAP_THRESHOLD)) {
                            setBackground(comp.getBackground());
                            break;
                        }
                    } else {
                        if (center.x >= bounds.x && center.x <= bounds.x + bounds.width &&
                            (Math.abs(center.y - bounds.y) < SNAP_THRESHOLD ||
                             Math.abs(center.y - (bounds.y + bounds.height)) < SNAP_THRESHOLD)) {
                            setBackground(comp.getBackground());
                            break;
                        }
                    }
                }
            }
        }
    }

    public flor() {
        addRoomButtonActionListener(drawingRoomButton, "Drawing Room");
        addRoomButtonActionListener(diningRoomButton, "Dining Room");
        addRoomButtonActionListener(bedroomButton, "Bedroom");
        addRoomButtonActionListener(kitchenButton, "Kitchen");
        addRoomButtonActionListener(bathroomButton, "Bathroom");
        addRoomButtonActionListener(studyButton, "Study");

        scrollPane.setViewportView(canvasPanel);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create File menu
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // Create menu items
        newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newMenuItem.addActionListener(e -> newFile());

        openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openMenuItem.addActionListener(e -> openFile());

        saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveMenuItem.addActionListener(e -> saveFile());

        // Add items to File menu
        fileMenu.add(newMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);

        // Create exit menu item
        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_Q);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitMenuItem.addActionListener(e -> exitApplication());

        // Add separator and exit item to File menu
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Add File menu to menu bar
        menuBar.add(fileMenu);

        // Add menu bar to toolbar
        toolBar.add(menuBar);
        rootPanel.add(toolBar, BorderLayout.NORTH);

        // Add to constructor
        addOpeningButtonListener(verticalDoorButton, true, true);
        addOpeningButtonListener(horizontalDoorButton, true, false);
        addOpeningButtonListener(verticalWindowButton, false, true);
        addOpeningButtonListener(horizontalWindowButton, false, false);
    }

    // Adds action listener to room creation buttons to handle room placement and sizing
    private void addRoomButtonActionListener(JButton button, String roomName) {
        MouseAdapter buttonDragAdapter = new MouseAdapter() {
            private JPanel draggedRoom = null;
            private Point clickOffset;
            private boolean wasDragged = false;
            private Point lastPoint = null;
            private long lastUpdateTime = 0;
            private static final long UPDATE_THRESHOLD = 16; // ~60 FPS

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    button.getModel().setArmed(true);
                    wasDragged = false;
                    
                    // Pre-create room on press
                    String heightText = heightTextField.getText();
                    String widthText = widthTextField.getText();
                    
                    if (isPositiveInteger(heightText) && isPositiveInteger(widthText)) {
                        int height = parseInt(heightText);
                        int width = parseInt(widthText);

                        height *= 20;  // dimensions are entered in m and 1m = 20px
                        width *= 20;  // dimensions are entered in m and 1m = 20px
                        Room room = createRoom(roomName, width, height);
                        if (room != null) {
                            draggedRoom = createRoomPanel(room, width, height);
                            clickOffset = new Point(width/2, height/2);
                        }
                        canvasPanel.setLayout(null); // setting layout again to avoid errors in gridbaglayout
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && draggedRoom != null) {
                    wasDragged = true;
                    long currentTime = System.currentTimeMillis();
                    
                    // Throttle updates
                    if (currentTime - lastUpdateTime < UPDATE_THRESHOLD) {
                        return;
                    }
                    
                    Point currentPoint = SwingUtilities.convertPoint(
                        button, 
                        e.getPoint(),
                        canvasPanel
                    );
                    
                    // Only update if moved significantly
                    if (lastPoint == null || currentPoint.distance(lastPoint) > 1) {
                        if (!canvasPanel.isAncestorOf(draggedRoom)) {
                            canvasPanel.add(draggedRoom);
                        }
                        
                        int newX = currentPoint.x - clickOffset.x;
                        int newY = currentPoint.y - clickOffset.y;
                        
                        newX = Math.max(0, Math.min(newX, canvasPanel.getWidth() - draggedRoom.getWidth()));
                        newY = Math.max(0, Math.min(newY, canvasPanel.getHeight() - draggedRoom.getHeight()));
                        
                        Rectangle proposedBounds = new Rectangle(newX, newY, 
                            draggedRoom.getWidth(), draggedRoom.getHeight());
                        Point snapPoint = getSnapPosition(proposedBounds);
                        
                        draggedRoom.setLocation(snapPoint.x, snapPoint.y);
                        lastPoint = currentPoint;
                        lastUpdateTime = currentTime;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.getModel().setArmed(false);
                
                if (!wasDragged) {
                    JOptionPane.showMessageDialog(canvasPanel,
                        "Please drag the button to create a room",
                        "Action Required",
                        JOptionPane.WARNING_MESSAGE);
                    heightTextField.setText("");
                    widthTextField.setText("");
                    return;
                }
                
                if (draggedRoom != null) {
                    if (hasOverlap(draggedRoom)) {
                        canvasPanel.remove(draggedRoom);
                        JOptionPane.showMessageDialog(canvasPanel,
                            "Room overlaps with existing room!",
                            "Overlap Error",
                            JOptionPane.ERROR_MESSAGE);
                    } else {
                        addDragAndDropToRoom(draggedRoom);
                    }
                    canvasPanel.repaint();
                    draggedRoom = null;
                }
                
                heightTextField.setText("");
                widthTextField.setText("");
            }
        };
        
        button.addMouseListener(buttonDragAdapter);
        button.addMouseMotionListener(buttonDragAdapter);
    }

    private boolean isPositiveInteger(String text) {
        try {
            int value = parseInt(text);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void createUIComponents() {
        canvasPanel = new CanvasPanel();
        canvasPanel.setLayout(null); // Set layout to null for absolute positioning
    }

    private class CanvasPanel extends JPanel {
        private Point origin = new Point(0, 0);
        private static final double SCROLL_SPEED_FACTOR = 0.3;

        public CanvasPanel() {
            setPreferredSize(new Dimension(2000, 2000));
            
            // Add component listener to handle initial layout
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    centerViewport();
                }
            });

            // Mouse adapter for canvas panning
            MouseAdapter mouseAdapter = new MouseAdapter() {
                private Point lastDragPoint;

                // Track starting point of drag
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        lastDragPoint = e.getPoint();
                    }
                }

                // Handle panning by calculating delta movement and updating viewport position
                @Override 
                public void mouseDragged(MouseEvent e) {
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        Point currentDragPoint = e.getPoint();
                        if (lastDragPoint != null) {
                            JViewport viewport = scrollPane.getViewport();
                            Point viewPosition = viewport.getViewPosition();
                            
                            // Calculate movement with reduced speed factor
                            int deltaX = (int)((currentDragPoint.x - lastDragPoint.x) * SCROLL_SPEED_FACTOR);
                            int deltaY = (int)((currentDragPoint.y - lastDragPoint.y) * SCROLL_SPEED_FACTOR);
                            
                            // Constrain viewport movement within canvas bounds
                            viewPosition.x = Math.max(0, Math.min(viewPosition.x - deltaX, 
                                getWidth() - viewport.getWidth()));
                            viewPosition.y = Math.max(0, Math.min(viewPosition.y - deltaY, 
                                getHeight() - viewport.getHeight()));
                            
                            viewport.setViewPosition(viewPosition);
                            lastDragPoint = currentDragPoint;
                        }
                    }
                }

                // Reset drag point when mouse released
                @Override
                public void mouseReleased(MouseEvent e) {
                    lastDragPoint = null;
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        // Centers the viewport on the canvas when component is shown
        private void centerViewport() {
            if (scrollPane != null && scrollPane.getViewport() != null) {
                JViewport viewport = scrollPane.getViewport();
                Point center = new Point(
                    (getWidth() - viewport.getWidth()) / 2,
                    (getHeight() - viewport.getHeight()) / 2
                );
                viewport.setViewPosition(center);
            }
        }

        // Custom painting for canvas grid lines
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Enable antialiasing for smoother lines
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw light gray grid lines at 50px intervals
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1));
            
            // Vertical grid lines
            for (int x = 0; x < getWidth(); x += 50) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            
            // Horizontal grid lines
            for (int y = 0; y < getHeight(); y += 50) {
                g2d.drawLine(0, y, getWidth(), y);
            }

            // Draw main axes
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            
            // X axis
            int centerY = getHeight() / 2;
            g2d.drawLine(0, centerY, getWidth(), centerY);
            
            // Y axis
            int centerX = getWidth() / 2;
            g2d.drawLine(centerX, 0, centerX, getHeight());
        }
    }

    private void addDragAndDropToRoom(JPanel roomPanel) {
        MouseAdapter roomDragAdapter = new MouseAdapter() {
            private Point clickOffset;
            private Point originalPosition;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    roomPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    clickOffset = new Point(
                        roomPanel.getWidth() / 2 - e.getX(),
                        roomPanel.getHeight() / 2 - e.getY()
                    );
                    originalPosition = roomPanel.getLocation();
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point canvasPoint = SwingUtilities.convertPoint(
                        roomPanel,
                        e.getX() + clickOffset.x,
                        e.getY() + clickOffset.y,
                        canvasPanel
                    );
                    
                    int newX = canvasPoint.x - roomPanel.getWidth() / 2;
                    int newY = canvasPoint.y - roomPanel.getHeight() / 2;
                    
                    newX = Math.max(0, Math.min(newX, canvasPanel.getWidth() - roomPanel.getWidth()));
                    newY = Math.max(0, Math.min(newY, canvasPanel.getHeight() - roomPanel.getHeight()));
                    
                    Rectangle proposedBounds = new Rectangle(newX, newY, 
                        roomPanel.getWidth(), roomPanel.getHeight());
                    Point snapPoint = getSnapPosition(proposedBounds);
                    
                    roomPanel.setLocation(snapPoint.x, snapPoint.y);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                roomPanel.setCursor(Cursor.getDefaultCursor());
                
                // Check for overlaps with other rooms
                if (hasOverlap(roomPanel)) {
                    // Show error dialog
                    JOptionPane.showMessageDialog(canvasPanel,
                        "Room overlaps with existing room!",
                        "Overlap Error",
                        JOptionPane.ERROR_MESSAGE);
                        
                    // Snap back to original position
                    roomPanel.setLocation(originalPosition);
                }
            }
        };
        
        roomPanel.addMouseListener(roomDragAdapter);
        roomPanel.addMouseMotionListener(roomDragAdapter);
    }

    private boolean hasOverlap(JPanel roomPanel) {
        Rectangle bounds = roomPanel.getBounds();
        
        // Check against all other room panels
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof JPanel && comp != roomPanel) {
                Rectangle otherBounds = comp.getBounds();
                if (bounds.intersects(otherBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Point getSnapPosition(Rectangle roomBounds) {
        if (!snappingCheckBox.isSelected()) {
            return new Point(roomBounds.x, roomBounds.y);
        }

        int snapX = roomBounds.x;
        int snapY = roomBounds.y;
        int centerX = roomBounds.x + roomBounds.width/2;
        int centerY = roomBounds.y + roomBounds.height/2;
        
        // Snap to grid
        int gridSnapX = Math.round((float)snapX / GRID_SIZE) * GRID_SIZE;
        int gridSnapY = Math.round((float)snapY / GRID_SIZE) * GRID_SIZE;
        
        // Snap to axes
        int canvasCenterX = canvasPanel.getWidth() / 2;
        int canvasCenterY = canvasPanel.getHeight() / 2;
        
        // Check axis snap
        if (Math.abs(centerX - canvasCenterX) < SNAP_THRESHOLD) {
            snapX = canvasCenterX - roomBounds.width/2;
        }
        if (Math.abs(centerY - canvasCenterY) < SNAP_THRESHOLD) {
            snapY = canvasCenterY - roomBounds.height/2;
        }
        
        // Check grid snap
        if (Math.abs(snapX - gridSnapX) < SNAP_THRESHOLD) {
            snapX = gridSnapX;
        }
        if (Math.abs(snapY - gridSnapY) < SNAP_THRESHOLD) {
            snapY = gridSnapY;
        }
        
        // Snap to other rooms
        for (Component comp : canvasPanel.getComponents()) {
            // Skip if not a room panel or is the canvas itself
            if (!(comp instanceof JPanel) || comp == canvasPanel) {
                continue;
            }
            
            // Skip if this is the room being dragged
            Point compLoc = comp.getLocation();
            if (compLoc.x == roomBounds.x && compLoc.y == roomBounds.y) {
                continue;
            }

            Rectangle otherBounds = comp.getBounds();
            
            // Snap to left/right edges
            if (Math.abs(roomBounds.x - otherBounds.x) < SNAP_THRESHOLD) {
                snapX = otherBounds.x;
            }
            if (Math.abs(roomBounds.x + roomBounds.width - otherBounds.x) < SNAP_THRESHOLD) {
                snapX = otherBounds.x - roomBounds.width;
            }
            if (Math.abs(roomBounds.x - (otherBounds.x + otherBounds.width)) < SNAP_THRESHOLD) {
                snapX = otherBounds.x + otherBounds.width;
            }
            
            // Snap to top/bottom edges
            if (Math.abs(roomBounds.y - otherBounds.y) < SNAP_THRESHOLD) {
                snapY = otherBounds.y;
            }
            if (Math.abs(roomBounds.y + roomBounds.height - otherBounds.y) < SNAP_THRESHOLD) {
                snapY = otherBounds.y - roomBounds.height;
            }
            if (Math.abs(roomBounds.y - (otherBounds.y + otherBounds.height)) < SNAP_THRESHOLD) {
                snapY = otherBounds.y + otherBounds.height;
            }
        }
        
        return new Point(snapX, snapY);
    }

    public static void main(String args[]) {
        frame = new JFrame("flor");
        flor florInstance = new flor();
        frame.setContentPane(florInstance.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add F11 key listener
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    toggleFullScreen();
                }
            }
            
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        frame.setFocusable(true);
        frame.requestFocus();
        
        // Set initial fullscreen state
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        if (ge.getDefaultScreenDevice().isFullScreenSupported()) {
            ge.getDefaultScreenDevice().setFullScreenWindow(frame);
        }
        
        frame.setVisible(true);
        
        // Center viewport after frame is fully initialized and visible
        SwingUtilities.invokeLater(() -> {
            if (florInstance.canvasPanel instanceof CanvasPanel) {
                ((CanvasPanel)florInstance.canvasPanel).centerViewport();
            }
        });
    }

    private static void toggleFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (frame.isUndecorated()) {
            frame.dispose();
            frame.setUndecorated(false);
            ge.getDefaultScreenDevice().setFullScreenWindow(null);
            frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        } else {
            frame.dispose();
            frame.setUndecorated(true);
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            if (ge.getDefaultScreenDevice().isFullScreenSupported()) {
                ge.getDefaultScreenDevice().setFullScreenWindow(frame);
            }
            frame.setVisible(true);
        }
        frame.requestFocus();
    }

    private void newFile() {
        // Check if there are rooms to save
        boolean hasRooms = false;
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof JPanel && comp != canvasPanel) {
                hasRooms = true;
                break;
            }
        }
        
        if (hasRooms) {
            int response = JOptionPane.showConfirmDialog(
                frame,
                "Do you want to save the current floor plan?",
                "Save Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                return; // Do nothing, let user save manually
            } else if (response == JOptionPane.NO_OPTION) {
                // Clear all rooms
                Component[] components = canvasPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel && comp != canvasPanel) {
                        canvasPanel.remove(comp);
                    }
                }
                canvasPanel.revalidate();
                canvasPanel.repaint();
            }
            // If CANCEL, do nothing
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Floor Plan Files (*.flp)", "flp"
        ));
        
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            // Clear existing rooms
            Component[] components = canvasPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel && comp != canvasPanel) {
                    canvasPanel.remove(comp);
                }
            }
            canvasPanel.repaint();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                // Skip header lines
                reader.readLine(); // Skip "# Floor Plan Layout File"
                reader.readLine(); // Skip format description
                
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 8) {
                        String roomType = parts[0];
                        int width = Integer.parseInt(parts[1]);
                        int height = Integer.parseInt(parts[2]);
                        int x = Integer.parseInt(parts[3]);
                        int y = Integer.parseInt(parts[4]);
                        Color color = new Color(
                            Integer.parseInt(parts[5]),
                            Integer.parseInt(parts[6]),
                            Integer.parseInt(parts[7])
                        );
                        
                        // Create room
                        Room room = createRoom(roomType, width, height);
                        if (room != null) {
                            canvasPanel.setLayout(null); // setting layout again to avoid errors in gridbaglayout
                            JPanel roomPanel = createRoomPanel(room, width, height);
                            roomPanel.setLocation(x, y);
                            canvasPanel.add(roomPanel);
                            addDragAndDropToRoom(roomPanel);
                        }
                    }
                }
                
                canvasPanel.revalidate();
                canvasPanel.repaint();
                
                JOptionPane.showMessageDialog(frame,
                    "Floor plan loaded successfully!",
                    "Load Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Error loading file: " + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Floor Plan Files (*.flp)", "flp"
        ));
        
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".flp")) {
                file = new File(file.getParentFile(), file.getName() + ".flp");
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write header
                writer.write("# Floor Plan Layout File");
                writer.newLine();
                writer.write("# Format: RoomType,Width,Height,X,Y,ColorR,ColorG,ColorB");
                writer.newLine();
                
                // Write each room's data
                for (Component comp : canvasPanel.getComponents()) {
                    if (comp instanceof JPanel && comp != canvasPanel) {
                        JPanel roomPanel = (JPanel) comp;
                        JLabel nameLabel = (JLabel) roomPanel.getComponent(0);
                        Color color = roomPanel.getBackground();
                        
                        String roomData = String.format("%s,%d,%d,%d,%d,%d,%d,%d",
                            nameLabel.getText(),
                            roomPanel.getWidth(),
                            roomPanel.getHeight(),
                            roomPanel.getX(),
                            roomPanel.getY(),
                            color.getRed(),
                            color.getGreen(),
                            color.getBlue()
                        );
                        writer.write(roomData);
                        writer.newLine();
                    }
                }
                
                JOptionPane.showMessageDialog(frame,
                    "Floor plan saved successfully!",
                    "Save Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Error saving file: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("layout.dat"))) {
            canvasPanel.removeAll();  // Clear the canvas

            while (true) {
                try {
                    Rectangle bounds = (Rectangle) in.readObject(); // Read component bounds
                    String name = (String) in.readObject();         // Read component name

                    // Create a new JPanel for the component
                    JPanel roomPanel = new JPanel();
                    roomPanel.setBounds(bounds);
                    roomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    roomPanel.setName(name);
                    roomPanel.add(new JLabel(name)); // Add a label to show room name

                    canvasPanel.add(roomPanel);
                } catch (EOFException e) {
                    break; // End of file
                }
            }

            canvasPanel.revalidate();
            canvasPanel.repaint();
            JOptionPane.showMessageDialog(frame, "Layout loaded successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading layout: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Room createRoom(String roomName, int width, int height) {
        Point2D.Float origin = new Point2D.Float(0, 0);
        switch(roomName) {
            case "Drawing Room": return new DrawingRoom(width, height, origin);
            case "Dining Room": return new DiningSpaceRoom(width, height, origin);
            case "Bedroom": return new Bedroom(width, height, origin);
            case "Kitchen": return new KitchenRoom(width, height, origin);
            case "Bathroom": return new Bathroom(width, height, origin);
            default: return null;
        }
    }

    private JPanel createRoomPanel(Room room, int width, int height) {
        // Create panel with null layout
        JPanel panel = new JPanel(null);
        panel.setBackground(room.getColor());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setSize(width, height);
        
        // Get room name
        String roomName = room.getClass().getSimpleName().replace("Room", "");
        if (roomName.equals("Drawing")) {
            roomName = "Drawing Room";
        } else if (roomName.equals("DiningSpace")) {
            roomName = "Dining Room";
        }
        
        // Create and position label
        JLabel nameLabel = new JLabel(roomName);
        nameLabel.setForeground(Color.DARK_GRAY);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Center label in panel
        int labelWidth = width - 10; // Padding
        int labelHeight = 20;        // Fixed height
        int x = 5;                   // Left padding
        int y = (height - labelHeight) / 2; // Vertical center
        nameLabel.setBounds(x, y, labelWidth, labelHeight);
        
        panel.add(nameLabel);
        
        return panel;
    }

    // Add button listener method
    private void addOpeningButtonListener(JButton button, boolean isDoor, boolean isVertical) {
        MouseAdapter openingDragAdapter = new MouseAdapter() {
            private OpeningPanel draggedOpening = null;
            private Point clickOffset;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    String sizeText = doorWindowSizeTextBox.getText();
                    if (!isPositiveInteger(sizeText)) {
                        JOptionPane.showMessageDialog(canvasPanel,
                            "Please enter valid opening size",
                            "Invalid Size",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    int size = parseInt(sizeText) * 20; // Convert to pixels
                    Point2D.Float position = new Point2D.Float(0, 0);
                    String orientation = isVertical ? "0" : "90";
                    
                    Opening opening = isDoor ? 
                        new Door(position, orientation) : 
                        new Window(position, orientation);
                        
                    draggedOpening = new OpeningPanel(opening, size, isVertical);
                    clickOffset = new Point(draggedOpening.getWidth()/2, draggedOpening.getHeight()/2);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedOpening != null) {
                    Point canvasPoint = SwingUtilities.convertPoint(
                        button,
                        e.getPoint(),
                        canvasPanel
                    );
                    
                    Point snapPoint = findNearestWall(canvasPoint, draggedOpening);
                    if (snapPoint != null) {
                        if (!canvasPanel.isAncestorOf(draggedOpening)) {
                            canvasPanel.add(draggedOpening);
                            canvasPanel.setComponentZOrder(draggedOpening, 0); // Set to top layer
                        }
                        draggedOpening.setLocation(
                            snapPoint.x - clickOffset.x,
                            snapPoint.y - clickOffset.y
                        );
                        draggedOpening.updateThickness();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedOpening != null) {
                    if (!isValidOpeningPlacement(draggedOpening)) {
                        canvasPanel.remove(draggedOpening);
                        JOptionPane.showMessageDialog(canvasPanel,
                            "Invalid opening placement",
                            "Placement Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    canvasPanel.repaint();
                }
                draggedOpening = null;
            }
        };

        button.addMouseListener(openingDragAdapter);
        button.addMouseMotionListener(openingDragAdapter);
    }

    // Add helper methods
    private Point findNearestWall(Point p, OpeningPanel opening) {
        int minDistance = Integer.MAX_VALUE;
        Point bestPoint = null;
        
        // If it's a window, don't allow placement between rooms
        if (!opening.opening.isDoor()) {
            if (isWindowBetweenRooms(p, opening)) {
                return null;
            }
        }
        
        // Rest of existing findNearestWall code...
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof JPanel && !(comp instanceof OpeningPanel)) {
                Rectangle bounds = comp.getBounds();
                
                if (opening.isVertical) {
                    // Left wall
                    if (Math.abs(p.x - bounds.x) < SNAP_THRESHOLD) {
                        int dist = Math.abs(p.y - bounds.y);
                        if (dist < minDistance && opening.size <= bounds.height) {
                            minDistance = dist;
                            bestPoint = new Point(bounds.x + 1, p.y); // Offset by 1px
                        }
                    }
                    // Right wall
                    if (Math.abs(p.x - (bounds.x + bounds.width)) < SNAP_THRESHOLD) {
                        int dist = Math.abs(p.y - bounds.y);
                        if (dist < minDistance && opening.size <= bounds.height) {
                            minDistance = dist;
                            bestPoint = new Point(bounds.x + bounds.width - 1, p.y); // Offset by 1px
                        }
                    }
                } else {
                    // Top wall
                    if (Math.abs(p.y - bounds.y) < SNAP_THRESHOLD) {
                        int dist = Math.abs(p.x - bounds.x);
                        if (dist < minDistance && opening.size <= bounds.width) {
                            minDistance = dist;
                            bestPoint = new Point(p.x, bounds.y + 1); // Offset by 1px
                        }
                    }
                    // Bottom wall
                    if (Math.abs(p.y - (bounds.y + bounds.height)) < SNAP_THRESHOLD) {
                        int dist = Math.abs(p.x - bounds.x);
                        if (dist < minDistance && opening.size <= bounds.width) {
                            minDistance = dist;
                            bestPoint = new Point(p.x, bounds.y + bounds.height - 1); // Offset by 1px
                        }
                    }
                }
            }
        }
        
        return bestPoint;
    }

    // Add method to check if window is between rooms
    private boolean isWindowBetweenRooms(Point location, OpeningPanel opening) {
        // Find all rooms that have walls near the opening
        List<Rectangle> adjacentRooms = new ArrayList<>();
        
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof JPanel && !(comp instanceof OpeningPanel)) {
                Rectangle bounds = comp.getBounds();
                
                if (opening.isVertical) {
                    // Check if opening is near vertical walls
                    if (Math.abs(location.x - bounds.x) < SNAP_THRESHOLD ||
                        Math.abs(location.x - (bounds.x + bounds.width)) < SNAP_THRESHOLD) {
                        adjacentRooms.add(bounds);
                    }
                } else {
                    // Check if opening is near horizontal walls
                    if (Math.abs(location.y - bounds.y) < SNAP_THRESHOLD ||
                        Math.abs(location.y - (bounds.y + bounds.height)) < SNAP_THRESHOLD) {
                        adjacentRooms.add(bounds);
                    }
                }
            }
        }
        
        // If we found more than one room, check if their walls are adjacent
        if (adjacentRooms.size() > 1) {
            for (int i = 0; i < adjacentRooms.size(); i++) {
                for (int j = i + 1; j < adjacentRooms.size(); j++) {
                    if (areWallsAdjacent(adjacentRooms.get(i), adjacentRooms.get(j), opening.isVertical)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    private boolean areWallsAdjacent(Rectangle room1, Rectangle room2, boolean isVertical) {
        if (isVertical) {
            // Check if rooms share a vertical wall
            boolean rightToLeft = Math.abs((room1.x + room1.width) - room2.x) < SNAP_THRESHOLD;
            boolean leftToRight = Math.abs(room1.x - (room2.x + room2.width)) < SNAP_THRESHOLD;
            
            if (rightToLeft || leftToRight) {
                // Check if rooms overlap vertically
                int overlapStart = Math.max(room1.y, room2.y);
                int overlapEnd = Math.min(room1.y + room1.height, room2.y + room2.height);
                return overlapEnd > overlapStart;
            }
        } else {
            // Check if rooms share a horizontal wall
            boolean topToBottom = Math.abs((room1.y + room1.height) - room2.y) < SNAP_THRESHOLD;
            boolean bottomToTop = Math.abs(room1.y - (room2.y + room2.height)) < SNAP_THRESHOLD;
            
            if (topToBottom || bottomToTop) {
                // Check if rooms overlap horizontally
                int overlapStart = Math.max(room1.x, room2.x);
                int overlapEnd = Math.min(room1.x + room1.width, room2.x + room2.width);
                return overlapEnd > overlapStart;
            }
        }
        
        return false;
    }

    private boolean isValidOpeningPlacement(OpeningPanel opening) {
        // First check for overlaps with other openings
        if (hasOpeningOverlap(opening)) {
            return false;
        }
        
        // Check if opening is on a wall
        Point center = new Point(
            opening.getX() + opening.getWidth()/2,
            opening.getY() + opening.getHeight()/2
        );
        
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof JPanel && !(comp instanceof OpeningPanel)) {
                Rectangle bounds = comp.getBounds();
                if (opening.isVertical) {
                    // Check vertical walls (left and right)
                    if (Math.abs(center.x - bounds.x) < SNAP_THRESHOLD ||
                        Math.abs(center.x - (bounds.x + bounds.width)) < SNAP_THRESHOLD) {
                        return true;
                    }
                } else {
                    // Check horizontal walls (top and bottom)
                    if (Math.abs(center.y - bounds.y) < SNAP_THRESHOLD ||
                        Math.abs(center.y - (bounds.y + bounds.height)) < SNAP_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasOpeningOverlap(OpeningPanel newOpening) {
        Rectangle newBounds = newOpening.getBounds();
        
        // Check against all existing openings
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof OpeningPanel && comp != newOpening) {
                OpeningPanel existingOpening = (OpeningPanel) comp;
                Rectangle existingBounds = existingOpening.getBounds();
                
                // Check if openings intersect
                if (newBounds.intersects(existingBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWallAdjacent(Point wallPoint, boolean isVertical) {
        boolean hasRoomOnFirstSide = false;
        boolean hasRoomOnSecondSide = false;
        
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof JPanel && !(comp instanceof OpeningPanel)) {
                Rectangle bounds = comp.getBounds();
                
                if (isVertical) {
                    // Check if point is within vertical range of the room
                    if (wallPoint.y >= bounds.y && wallPoint.y <= bounds.y + bounds.height) {
                        // Check left side
                        if (Math.abs(wallPoint.x - bounds.x) < SNAP_THRESHOLD) {
                            hasRoomOnFirstSide = true;
                        }
                        // Check right side
                        if (Math.abs(wallPoint.x - (bounds.x + bounds.width)) < SNAP_THRESHOLD) {
                            hasRoomOnSecondSide = true;
                        }
                    }
                } else {
                    // Check if point is within horizontal range of the room
                    if (wallPoint.x >= bounds.x && wallPoint.x <= bounds.x + bounds.width) {
                        // Check top side
                        if (Math.abs(wallPoint.y - bounds.y) < SNAP_THRESHOLD) {
                            hasRoomOnFirstSide = true;
                        }
                        // Check bottom side
                        if (Math.abs(wallPoint.y - (bounds.y + bounds.height)) < SNAP_THRESHOLD) {
                            hasRoomOnSecondSide = true;
                        }
                    }
                }
            }
        }
        
        // Return true if wall is interior (has rooms on both sides)
        return hasRoomOnFirstSide && hasRoomOnSecondSide;
    }

    // Add exit handler method
    private void exitApplication() {
        // Check if there are rooms
        boolean hasRooms = false;
        for (Component comp : canvasPanel.getComponents()) {
            if (comp instanceof JPanel && comp != canvasPanel) {
                hasRooms = true;
                break;
            }
        }
        
        if (hasRooms) {
            int response = JOptionPane.showConfirmDialog(
                frame,
                "Do you want to save the existing floor plan?",
                "Save before exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (response == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            // If YES, do nothing - let user save manually
        } else {
            System.exit(0);
        }
    }
}
