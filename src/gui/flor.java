package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public flor() {
        addRoomButtonActionListener(livingRoomButton, "Living Room");
        addRoomButtonActionListener(diningRoomButton, "Dining Room");
        addRoomButtonActionListener(bedroomButton, "Bedroom");
        addRoomButtonActionListener(kitchenButton, "Kitchen");
        addRoomButtonActionListener(bathroomButton, "Bathroom");
        addRoomButtonActionListener(studyButton, "Study");
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
