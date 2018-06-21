package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GenerateTree extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel pnlListOfRules;
    private JTextField txtTreeName;
    private ArrayList<JCheckBox> ruleList = new ArrayList<>();
    private GUI gui;

    public GenerateTree(GUI gui) {
        this.gui = gui;
        pnlListOfRules.setLayout(new BoxLayout(pnlListOfRules,BoxLayout.Y_AXIS));
        for (String rule : gui.getRuleList()) {
            JCheckBox ruleBox = new JCheckBox(rule);
            ruleList.add(ruleBox);
            pnlListOfRules.add(ruleBox);
        }

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if(txtTreeName.getText().length()>0) {
            String rules = "";
            for (JCheckBox check :
                    ruleList) {
                if (check.isSelected()) {
                    rules += check.getText();
                }
            }
            if(rules.length()>0) {
                gui.generateTree(txtTreeName.getText(),rules);
                dispose();
            }else{
                JOptionPane.showMessageDialog(null,"No rules has been picked!");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No Name has been set!");
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
