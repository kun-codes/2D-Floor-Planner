package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import java.awt.geom.Point2D;
import model.rooms.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.io.*;

import static java.lang.Integer.parseInt;

public class flor {
    private JPanel rootPanel;
    private JTabbedPane objectPlace;
    private JButton drawingRoomButton;
    private JButton diningRoomButton;
    private JButton bedroomButton;
    private JButton kitchenButton;
    private JButton bathroomButton;
    private JButton studyButton;
    private JButton doorButton;
    private JButton windowButton;
    private JButton tableButton;
    private JButton bedButton;
    private JButton sofaButton;
    private JButton sinkButton;
    private JButton toiletButton;
    private JButton ovenButton;
    private JTextField heightTextField;
    private JTextField widthTextField;
    private JScrollPane scrollPane;
    private JPanel canvasPanel;
    private JPanel optionsPanel;
    private JLabel snappingLabel;
    private JCheckBox snappingCheckBox;
    private JToolBar toolBar;
    private JButton ExitButton;
    private JButton SaveButton;
    private JButton LoadButton;


    private static final int GRID_SIZE = 50;
    private static final int SNAP_THRESHOLD = 10;
    private static JFrame frame;

    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;

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

        // Add File menu to menu bar
        menuBar.add(fileMenu);

        // Add menu bar to toolbar
        toolBar.add(menuBar);
        /*toolBar.add(saveButton);
        toolBar.add(loadButton);*/
        rootPanel.add(toolBar, BorderLayout.NORTH);
        /*ExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });*/
        // Add ExitButton functionality
        ExitButton.addActionListener(e -> System.exit(0));
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
                        Room room = createRoom(roomName, width, height);
                        if (room != null) {
                            draggedRoom = createRoomPanel(room, width, height);
                            clickOffset = new Point(width/2, height/2);
                        }
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
                JPanel panel = new JPanel() {
                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        setDoubleBuffered(true);
                    }
                };
                panel.putClientProperty("roomData", room);
                panel.setBackground(room.getColor());
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                panel.setLayout(new GridBagLayout());
                
                JLabel nameLabel = new JLabel(roomName) {
                    @Override
                    public void paint(Graphics g) {
                        setVisible(true); // Force label visibility
                        super.paint(g);
                    }
                };
                nameLabel.setForeground(Color.DARK_GRAY);
                nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                
                panel.add(nameLabel, gbc);
                panel.setSize(width, height);
                
                // Force immediate layout calculation
                panel.doLayout();
                panel.validate();
                
                return panel;
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
        // TODO: Implement new file functionality
        System.out.println("New file");
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            // TODO: Implement file opening
            System.out.println("Opening: " + fileChooser.getSelectedFile().getName());
        }
    }

    private void saveFile() {
        /*JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            // TODO: Implement file saving
            System.out.println("Saving: " + fileChooser.getSelectedFile().getName());
        }*/
        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showSaveDialog(frame);
        if (choice == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileChooser.getSelectedFile()))) {
                // Save all rooms on the canvasPanel
                for (Component component : canvasPanel.getComponents()) {
                    if (component instanceof JPanel) {
                        JPanel roomPanel = (JPanel) component;
                        Room room = (Room) roomPanel.getClientProperty("roomData"); // Assuming rooms are stored as client properties
                        if (room != null) {
                            oos.writeObject(room);
                        }
                    }
                }
                JOptionPane.showMessageDialog(frame, "File saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    roomPanel.setBackground(new Color(200, 200, 255)); // Example color
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

}
