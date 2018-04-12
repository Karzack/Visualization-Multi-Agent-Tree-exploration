package GUI;

import javax.swing.*;


public class Main {


    public static void main(String[] args){
        JFrame frame = new JFrame("Multi-agents visualization");
        frame.setContentPane(new GUI().getPanel());
        frame.pack();
        frame.setSize(1200,700);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
    }
}
