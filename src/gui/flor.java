package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.geom.Point2D;
import model.rooms.*;

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

    public flor() {
        addRoomButtonActionListener(drawingRoomButton, "Drawing Room");
        addRoomButtonActionListener(diningRoomButton, "Dining Room");
        addRoomButtonActionListener(bedroomButton, "Bedroom");
        addRoomButtonActionListener(kitchenButton, "Kitchen");
        addRoomButtonActionListener(bathroomButton, "Bathroom");
        addRoomButtonActionListener(studyButton, "Study");

        scrollPane.setViewportView(canvasPanel);
    }

    // Adds action listener to room creation buttons to handle room placement and sizing
    private void addRoomButtonActionListener(JButton button, String roomName) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String heightText = heightTextField.getText();
                String widthText = widthTextField.getText();
                
                if (isPositiveInteger(heightText) && isPositiveInteger(widthText)) {
                    int height = parseInt(heightText);
                    int width = parseInt(widthText);
                    
                    // Get origin point (center of canvas)
                    Point2D.Float origin = new Point2D.Float(1000, 1000); // Canvas is 2000x2000
                    
                    Room room = null;
                    switch(roomName) {
                        case "Drawing Room":
                            room = new DrawingRoom(width, height, origin);
                            break;
                        case "Dining Room": 
                            room = new DiningSpaceRoom(width, height, origin);
                            break;
                        case "Bedroom":
                            room = new Bedroom(width, height, origin);
                            break;
                        case "Kitchen":
                            room = new KitchenRoom(width, height, origin);
                            break;
                        case "Bathroom":
                            room = new Bathroom(width, height, origin);
                            break;
                    }

                    if (room != null) {
                        JPanel roomPanel = new JPanel();
                        roomPanel.setBackground(room.getColor());
                        roomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        roomPanel.setLayout(new GridBagLayout()); // For centering label
                        
                        // Create label for room name
                        JLabel nameLabel = new JLabel();
                        nameLabel.setForeground(Color.DARK_GRAY);
                        
                        // Calculate available space for text
                        int availableWidth = width - 10; // 5px padding on each side
                        int availableHeight = height - 10;
                        
                        // Format room name based on available space
                        String displayText = roomName;
                        FontMetrics fm = nameLabel.getFontMetrics(nameLabel.getFont());
                        
                        if (fm.stringWidth(roomName) > availableWidth) {
                            // Try splitting by words
                            String[] words = roomName.split(" ");
                            StringBuilder text = new StringBuilder("<html><center>");
                            for (String word : words) {
                                text.append(word).append("<br>");
                            }
                            text.append("</center></html>");
                            displayText = text.toString();
                            
                            // Check if height is still too small
                            if (fm.getHeight() * words.length > availableHeight) {
                                displayText = "...";
                            }
                        }
                        
                        nameLabel.setText(displayText);
                        roomPanel.add(nameLabel);
                        
                        // Set size
                        roomPanel.setSize(new Dimension(width, height));
                        
                        // Calculate position to center room at origin
                        int x = (int)origin.x - width/2;
                        int y = (int)origin.y - height/2;
                        roomPanel.setBounds(x, y, width, height);
                        
                        // Add room to canvas and refresh display
                        canvasPanel.add(roomPanel);
                        canvasPanel.revalidate();
                        canvasPanel.repaint();
                        
                        // Add drag and drop functionality to the room
                        addDragAndDropToRoom(roomPanel);
                        
                        System.out.println(roomName + " of dimensions " + width + " by " + height + " has been created");
                    }
                    
                    // Clear input fields after room creation
                    heightTextField.setText("");
                    widthTextField.setText("");
                } else {
                    System.out.println("Enter valid positive dimensions");
                    heightTextField.setText("");
                    widthTextField.setText("");
                }
            }
        });
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
                    
                    roomPanel.setLocation(newX, newY);
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

    public static void main(String args[])
    {
        JFrame frame = new JFrame("flor");
        flor florInstance = new flor();
        frame.setContentPane(florInstance.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1920, 1080);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setLocation(0,0);
        frame.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(100, 100, (int) dim.getWidth(), (int) dim.getHeight());
        frame.setLocationRelativeTo(null);
        // Center viewport after frame is visible
        SwingUtilities.invokeLater(() -> {
            if (florInstance.canvasPanel instanceof CanvasPanel) {
                ((CanvasPanel)florInstance.canvasPanel).centerViewport();
            }
        });
    }
}
