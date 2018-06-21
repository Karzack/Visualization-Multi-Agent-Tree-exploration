package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RemoveRule extends JDialog {
    private ArrayList<JCheckBox> ruleList = new ArrayList<>();
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel pnlListOfRules;
    private GUI gui;

    public RemoveRule(GUI gui) {
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

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        ArrayList<String> toFile = new ArrayList<>();
        for (JCheckBox check: ruleList) {
            if(!check.isSelected()){
                toFile.add(check.getText());
            }
        }
        gui.writeNewRuleList(toFile);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
