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
    private JTextField textField2;
    private JTextField textField1;

    public flor()
    {
        livingRoomButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JLabel labellivroom = new JLabel("Text-Only Label");
            }
        });
    }

    /*
        ImageIcon image=new ImageIcon("C:\Users\AJAY\Pictures\Screenshots\holy taco report.png");
        final int IMG_WIDTH = image.getIconWidth();
        final int IMG_HEIGHT = image.getIconHeight();
        Point image_corner;
        Point previousPoint;

        flor()
        {
            image_corner=new Point(0,0);
            ClickListener click=new ClickListener();
            this.addMouseListener(click);

        }
        public void paintComponent(Graphics g)
        {

        }
        private class ClickListener extends MouseAdapter
        {

        }
        private class DragListener extends MouseMotionAdapter
        {

        }*/
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
