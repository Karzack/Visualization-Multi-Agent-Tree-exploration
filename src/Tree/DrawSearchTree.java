package Tree;

import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class DrawSearchTree extends JFrame {
    DrawPanel drawPanel;

    public DrawSearchTree() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        int width = (int)(tk.getScreenSize().getWidth());
        int height = (int)(tk.getScreenSize().getHeight());
        setSize(width,height);
        setLocation(0,0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER,width,20));
        JButton addNodesButton = new JButton("Add random nodes to the tree");
        addNodesButton.addActionListener(new AddNodesAction());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addNodesButton);
        contentPane.add(new JScrollPane(buttonPanel));
        drawPanel = new DrawPanel();
        drawPanel.setBackground(Color.white);
        drawPanel.setPreferredSize(new Dimension(width,height-50));
        contentPane.add(new JScrollPane(drawPanel));
        setVisible(true);
    } // end constructor

    class AddNodesAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String numNodesString = JOptionPane.showInputDialog("Enter the number of nodes to add","20");
            if( numNodesString == null ) return;
            int numNodes;
            try {
                numNodes = new Integer(numNodesString).intValue();
            } catch(NumberFormatException exp) {
                JOptionPane.showMessageDialog(null,"Enter digits only");
                return;
            }
            Random r = new Random();
            for( int i = 0; i < numNodes; i++ ) {

            }
            drawPanel.repaint();
            JOptionPane.showMessageDialog(null,"The nodes in inorder\n");
        }
    }

    class DrawPanel extends JPanel {
        private Color color;
        public void paintComponent(Graphics g) {

            String text = "Nr";
            int x = 250, y = 200;
            int height = 20, width = 20;

            g.setColor(Color.black);
            g.drawLine(x,y,x+100,y+50);

            g.setColor(Color.yellow);
            g.fillOval(x-height/2, y-width/2,width, height);
            g.fillOval((x-height/2)+100, (y-width/2)+50,width, height);
            g.fillOval((x-height/2)+200, (y-width/2)+100,width, height);

            FontMetrics fm = g.getFontMetrics();
            double textWidth = fm.getStringBounds(text, g).getWidth();
            g.setColor(Color.black);
            g.drawString(text, (int) (x - textWidth/2),(int) (y + fm.getMaxAscent() / 2));
            g.drawString(text, (int) (x - textWidth/2)+100,(int) (y + fm.getMaxAscent() / 2)+50);
        }
    }

}