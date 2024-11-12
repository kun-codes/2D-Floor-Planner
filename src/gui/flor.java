package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class flor {
    private JPanel rootPanel;
    private JTabbedPane objectPlace;
    private JButton livingRoomButton;
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

    public flor() {
        addRoomButtonActionListener(livingRoomButton, "Living Room");
        addRoomButtonActionListener(diningRoomButton, "Dining Room");
        addRoomButtonActionListener(bedroomButton, "Bedroom");
        addRoomButtonActionListener(kitchenButton, "Kitchen");
        addRoomButtonActionListener(bathroomButton, "Bathroom");
        addRoomButtonActionListener(studyButton, "Study");

        scrollPane.setViewportView(canvasPanel);
    }

    private void addRoomButtonActionListener(JButton button, String roomName) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String heightText = heightTextField.getText();
                String widthText = widthTextField.getText();
                
                if (isPositiveInteger(heightText) && isPositiveInteger(widthText)) {
                    System.out.println(roomName + " of dimensions " + heightText + " by " + widthText + " has been created");
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
            int value = Integer.parseInt(text);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void createUIComponents() {
        canvasPanel = new CanvasPanel();
    }

    private class CanvasPanel extends JPanel {
        private Point origin = new Point(0, 0);
        private static final double SCROLL_SPEED_FACTOR = 1.0; // Reduced for smoother movement

        public CanvasPanel() {
            setPreferredSize(new Dimension(2000, 2000));

            MouseAdapter mouseAdapter = new MouseAdapter() {
                private Point lastDragPoint;

                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        lastDragPoint = e.getPoint();
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        Point currentDragPoint = e.getPoint();
                        if (lastDragPoint != null) {
                            JViewport viewport = scrollPane.getViewport();
                            Point viewPosition = viewport.getViewPosition();
                            
                            int deltaX = (int)((currentDragPoint.x - lastDragPoint.x) * SCROLL_SPEED_FACTOR);
                            int deltaY = (int)((currentDragPoint.y - lastDragPoint.y) * SCROLL_SPEED_FACTOR);
                            
                            viewPosition.x = Math.max(0, Math.min(viewPosition.x - deltaX, 
                                getWidth() - viewport.getWidth()));
                            viewPosition.y = Math.max(0, Math.min(viewPosition.y - deltaY, 
                                getHeight() - viewport.getHeight()));
                            
                            viewport.setViewPosition(viewPosition);
                            lastDragPoint = currentDragPoint;
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    lastDragPoint = null;
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw grid lines
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

    public static void main(String args[])
    {
        JFrame frame = new JFrame("flor");
        frame.setContentPane(new flor().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1920,1080);
        frame.setLocationRelativeTo(null);
        frame.setLocation(0,0);
        frame.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(100, 100, (int) dim.getWidth(), (int) dim.getHeight());
        frame.setLocationRelativeTo(null);
    }
}
